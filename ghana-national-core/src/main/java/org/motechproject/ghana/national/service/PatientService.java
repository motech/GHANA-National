package org.motechproject.ghana.national.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.repository.AllPatients;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.api.InvalidCheckDigitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.motechproject.ghana.national.tools.Utility.emptyToNull;

@Service
public class PatientService {
    AllPatients allPatients;

    @Autowired
    public PatientService(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    public String registerPatient(Patient patient, PatientType typeOfPatient, String parentId)
            throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        try {
            String savedPatientId = allPatients.save(patient);
            if (StringUtils.isNotEmpty(parentId)) {
                createRelationship(parentId, savedPatientId);
            }
            return savedPatientId;
        } catch (IdentifierNotUniqueException e) {
            throw new PatientIdNotUniqueException();
        } catch (InvalidCheckDigitException e) {
            throw new PatientIdIncorrectFormatException();
        }
    }

    public Patient getPatientByMotechId(String patientId) {
        Patient patient = allPatients.patientByMotechId(patientId);
        if (patient == null) {
            return null;
        }
        Relationship motherRelationship = allPatients.getMotherRelationship(patient.getMrsPatient().getPerson());
        if (motherRelationship != null) {
            setParentId(patient, motherRelationship);
        }
        return patient;
    }

    public List<Patient> search(String name, String motechId) {
        return allPatients.search(emptyToNull(name), emptyToNull(motechId));
    }

    public String updatePatient(Patient patient, PatientType typeOfPatient, String parentId) throws ParentNotFoundException {
        String savedPatientId = allPatients.update(patient);
        Patient savedPatient = getPatientByMotechId(savedPatientId);
        Relationship relationship = allPatients.getMotherRelationship(savedPatient.getMrsPatient().getPerson());

        if (relationship != null && StringUtils.isEmpty(parentId)) {
            allPatients.voidMotherChildRelationship(savedPatient.getMrsPatient().getPerson());
        }
        if (relationship == null && StringUtils.isNotEmpty(parentId)) {
            createRelationship(parentId, savedPatientId);
        }
        if (relationship != null && StringUtils.isNotEmpty(parentId)) {
            updateRelationship(parentId, savedPatient, relationship);
        }
        return savedPatientId;
    }

    private void setParentId(Patient patient, Relationship motherRelationship) {
        Person mother = motherRelationship.getPersonA();
        if (mother != null && !mother.getNames().isEmpty()) {
            List<Patient> patients = allPatients.search(mother.getNames().iterator().next().getFullName(), null);
            if (patients != null && !patients.isEmpty()) {
                patient.parentId(getParentId(mother, patients));
            }
        }
    }

    private String getParentId(Person mother, List<Patient> patients) {
        for (Patient patient : patients) {
            if (patient.getMrsPatient().getPerson().getId().equals(mother.getId().toString())) {
                return patient.getMrsPatient().getMotechId();
            }
        }
        return null;
    }

    void createRelationship(String parentId, String savedPatientId) throws ParentNotFoundException {
        Patient mother = getPatientByMotechId(parentId);
        if (mother == null) {
            throw new ParentNotFoundException();
        }
        Patient child = getPatientByMotechId(savedPatientId);
        allPatients.createMotherChildRelationship(mother.getMrsPatient().getPerson(), child.getMrsPatient().getPerson());
    }

    private void updateRelationship(String parentId, Patient savedPatient, Relationship relationship) throws ParentNotFoundException {
        Patient updatedMother = getPatientByMotechId(parentId);
        if (updatedMother == null) {
            throw new ParentNotFoundException();
        }
        Person personA = relationship.getPersonA();
        if (personA != null && !personA.getId().toString().equals(updatedMother.getMrsPatient().getPerson().getId())) {
            allPatients.updateMotherChildRelationship(updatedMother.getMrsPatient().getPerson(), savedPatient.getMrsPatient().getPerson());
        }
    }
}
