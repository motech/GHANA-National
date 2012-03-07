package org.motechproject.ghana.national.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.repository.*;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.util.DateUtil;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.api.InvalidCheckDigitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.motechproject.ghana.national.domain.EncounterType.PATIENT_EDIT_VISIT;
import static org.motechproject.ghana.national.domain.EncounterType.PATIENT_REG_VISIT;
import static org.motechproject.ghana.national.tools.Utility.emptyToNull;

@Service
public class PatientService {
    private AllPatients allPatients;
    private IdentifierGenerator identifierGenerator;
    private AllEncounters allEncounters;
    private AllSchedules allSchedules;
    private AllAppointments allAppointments;
    private SearchMRSPatient searchMRSPatient;

    @Autowired
    public PatientService(AllPatients allPatients, IdentifierGenerator identifierGenerator, AllEncounters allEncounters, AllSchedules allSchedules, AllAppointments allAppointments, SearchMRSPatient searchMRSPatient) {
        this.allPatients = allPatients;
        this.identifierGenerator = identifierGenerator;
        this.allEncounters = allEncounters;
        this.allSchedules = allSchedules;
        this.allAppointments = allAppointments;
        this.searchMRSPatient = searchMRSPatient;
    }

    public Patient registerPatient(Patient patient, String staffId)
            throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        try {

            if (StringUtils.isEmpty(patient.getMrsPatient().getMotechId())) {
                MRSPatient mrsPatient = patient.getMrsPatient();
                String motechId = identifierGenerator.newPatientId();
                patient = new Patient(new MRSPatient(mrsPatient.getId(), motechId, mrsPatient.getPerson(), mrsPatient.getFacility()), patient.getParentId());
            }
            Patient savedPatient = allPatients.save(patient);
            allEncounters.persistEncounter(savedPatient.getMrsPatient(), staffId, patient.getMrsPatient().getFacility().getId(),
                    PATIENT_REG_VISIT.value(), DateUtil.today().toDate(), null);
            return savedPatient;
        } catch (IdentifierNotUniqueException e) {
            throw new PatientIdNotUniqueException();
        } catch (InvalidCheckDigitException e) {
            throw new PatientIdIncorrectFormatException();
        }
    }

    public Patient getPatientByMotechId(String patientId) {
        return allPatients.getPatientByMotechId(patientId);
    }

    public List<Patient> search(String name, String motechId) {
        return allPatients.search(emptyToNull(name), emptyToNull(motechId));
    }

    public String updatePatient(Patient patient, String staffId) throws ParentNotFoundException {
        String savedPatientId = allPatients.update(patient);
        Patient savedPatient = getPatientByMotechId(savedPatientId);
        Relationship relationship = allPatients.getMotherRelationship(savedPatient.getMrsPatient().getPerson());

        if (relationship != null && StringUtils.isEmpty(patient.getParentId())) {
            allPatients.voidMotherChildRelationship(savedPatient.getMrsPatient().getPerson());
        }
        if (relationship == null && StringUtils.isNotEmpty(patient.getParentId())) {
            createRelationship(patient.getParentId(), savedPatientId);
        }
        if (relationship != null && StringUtils.isNotEmpty(patient.getParentId())) {
            updateRelationship(patient.getParentId(), savedPatient, relationship);
        }
        allEncounters.persistEncounter(savedPatient.getMrsPatient(), staffId, patient.getMrsPatient().getFacility().getId(),
                PATIENT_EDIT_VISIT.value(), DateUtil.today().toDate(), null);
        return savedPatientId;
    }

    public Integer getAgeOfPatientByMotechId(String motechId) {
        return allPatients.getAgeOfPersonByMotechId(motechId);
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

    public void deceasePatient(Date dateOfDeath, String patientMotechId, String causeOfDeath, String comment) {
        Patient patient = getPatientByMotechId(patientMotechId);
        allPatients.deceasePatient(dateOfDeath, patientMotechId, (causeOfDeath.equals("OTHER") ? "OTHER NON-CODED" : "NONE"), comment);
        allSchedules.unEnroll(patient.getMRSPatientId(), patient.allCareProgramsToUnEnroll());
        allAppointments.remove(patient);
    }

    public List<MRSPatient> getPatients(String firstName, String lastName, String phoneNumber, Date dateOfBirth, String insuranceNumber) {
        return searchMRSPatient.getPatients(firstName, lastName, phoneNumber, dateOfBirth, insuranceNumber);
    }
}
