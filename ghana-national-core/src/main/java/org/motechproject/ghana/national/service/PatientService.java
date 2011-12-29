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
            final String savedPatientId = allPatients.save(patient);
            createRelationship(typeOfPatient, parentId, savedPatientId);
            return savedPatientId;
        } catch (IdentifierNotUniqueException e) {
            throw new PatientIdNotUniqueException();
        } catch (InvalidCheckDigitException e) {
            throw new PatientIdIncorrectFormatException();
        }
    }

    void createRelationship(PatientType typeOfPatient, String parentId, String savedPatientId) throws ParentNotFoundException {
        if (PatientType.CHILD_UNDER_FIVE.equals(typeOfPatient) && StringUtils.isNotEmpty(parentId)) {
            Patient mother = getPatientById(parentId);
            if (mother == null) {
                throw new ParentNotFoundException();
            }
            final Patient child = getPatientById(savedPatientId);
            allPatients.createMotherChildRelationship(mother.mrsPatient().getPerson(), child.mrsPatient().getPerson());
        }
    }

    public Patient getPatientById(String patientId) {
        final Patient patient = allPatients.patientById(patientId);
        if (patient == null) {
            return patient;
        }
        final Relationship motherRelationship = allPatients.getMotherRelationship(patient.mrsPatient().getPerson());
        if (motherRelationship != null) {
            final Person mother = motherRelationship.getPersonA();
            if (mother != null && !mother.getNames().isEmpty()) {
                final List<Patient> patients = allPatients.search(mother.getNames().iterator().next().getFullName(), null);
                if (patients != null && !patients.isEmpty()) {
                    patient.parentId(getParentId(mother, patients));
                }
            }
        }
        return patient;
    }

    private String getParentId(Person mother, List<Patient> patients) {
        for (Patient motherPatient : patients) {
            if (motherPatient.mrsPatient().getPerson().getId().equals(mother.getId().toString())) {
                return motherPatient.mrsPatient().getMotechId();
            }
        }
        return null;
    }

    public List<Patient> search(String name, String motechId) {
        return allPatients.search(emptyToNull(name), emptyToNull(motechId));
    }

    public String updatePatient(Patient patient, PatientType typeOfPatient, String parentId) throws ParentNotFoundException {
        String savedPatientId = allPatients.update(patient);
        Patient savedPatient = getPatientById(savedPatientId);
        Relationship relationship = allPatients.getMotherRelationship(savedPatient.mrsPatient().getPerson());

        if (relationship != null) {
            if (StringUtils.isNotEmpty(parentId)) {
                Patient updatedMother = getPatientById(parentId);
                Person personA = relationship.getPersonA();
                if (personA != null && !personA.getId().toString().equals(updatedMother.mrsPatient().getPerson().getId())) {
                    allPatients.updateMotherChildRelationship(updatedMother.mrsPatient().getPerson(), savedPatient.mrsPatient().getPerson());
                } else {
                    //relationship has not changed!
                }
            } else {
                allPatients.voidMotherChildRelationship(savedPatient.mrsPatient().getPerson());
            }
        } else if (StringUtils.isNotEmpty(parentId)) {
            createRelationship(typeOfPatient, parentId, savedPatientId);
        }
        return savedPatientId;
    }
}
