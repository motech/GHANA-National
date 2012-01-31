package org.motechproject.ghana.national.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.mrs.model.MRSEncounter;
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

import static org.motechproject.ghana.national.tools.Utility.emptyToNull;

@Service
public class PatientService {
    AllPatients allPatients;
    AllEncounters allEncounters;
    IdentifierGenerationService identifierGenerationService;
    EncounterService encounterService;

    @Autowired
    public PatientService(AllPatients allPatients, AllEncounters allEncounters , IdentifierGenerationService identifierGenerationService,EncounterService encounterService) {
        this.allPatients = allPatients;
        this.allEncounters = allEncounters;
        this.identifierGenerationService = identifierGenerationService;
        this.encounterService = encounterService;
    }

    public String registerPatient(Patient patient,String staffId)
            throws ParentNotFoundException, PatientIdNotUniqueException, PatientIdIncorrectFormatException {
        try {

            if (StringUtils.isEmpty(patient.getMrsPatient().getMotechId())) {
                MRSPatient mrsPatient = patient.getMrsPatient();
                String motechId = identifierGenerationService.newPatientId();
                patient = new Patient(new MRSPatient(mrsPatient.getId(), motechId, mrsPatient.getPerson(), mrsPatient.getFacility()), patient.getParentId());
            }
            MRSPatient savedMRSPatient = allPatients.save(patient);
            String savedPatientMotechId = savedMRSPatient.getMotechId();

            if (StringUtils.isNotEmpty(patient.getParentId())) {
                createRelationship(patient.getParentId(), savedPatientMotechId);
            }
            encounterService.persistEncounter(savedMRSPatient, staffId, patient.getMrsPatient().getFacility().getId(), Constants.ENCOUNTER_PATIENTREGVISIT, DateUtil.today().toDate(), null);
            return savedPatientMotechId;
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

    public String updatePatient(Patient patient) throws ParentNotFoundException {
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
        return savedPatientId;
    }

    public Integer getAgeOfPatientByMotechId(String motechId) {
        return allPatients.getAgeOfPersonByMotechId(motechId);
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

    public void saveEncounter(MRSEncounter mrsEncounter) {
        allEncounters.save(mrsEncounter);
    }

    public void deceasePatient(String patientMotechId, Date dateOfDeath, String causeOfDeath, String comment) {
        Patient patient = getPatientByMotechId(patientMotechId);
        patient.getMrsPatient().getPerson().dead(true);
        patient.getMrsPatient().getPerson().deathDate(dateOfDeath);
        allPatients.update(patient);
        allPatients.saveCauseOfDeath(dateOfDeath, patient.getMrsPatient().getId(), (causeOfDeath.equals("OTHER") ? "OTHER NON-CODED" : "NONE"), comment);
    }
}
