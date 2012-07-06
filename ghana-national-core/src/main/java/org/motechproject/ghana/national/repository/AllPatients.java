package org.motechproject.ghana.national.repository;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.services.OpenMRSRelationshipAdapter;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

@Repository
public class AllPatients {
    //todo remove dependency on patientAdapter, openMRSRelationshipAdapter, move to service layer - this class is just an abstraction of couch repo
    @Autowired
    private MRSPatientAdapter patientAdapter;
    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private OpenMRSRelationshipAdapter openMRSRelationshipAdapter;

    public Patient save(Patient patient) throws ParentNotFoundException {
        MRSPatient mrsPatient = patientAdapter.savePatient(patient.getMrsPatient());
        if (StringUtils.isNotEmpty(patient.getParentId())) {
            Patient mother = getPatientByMotechId(patient.getParentId());
            if (mother == null) throw new ParentNotFoundException();
            createMotherChildRelationship(mother.getMrsPatient().getPerson(), mrsPatient.getPerson());
        }
        return new Patient(mrsPatient, patient.getParentId());
    }

    public Patient patientByOpenmrsId(String patientId) {
        MRSPatient mrsPatient = patientAdapter.getPatient(patientId);
        return (mrsPatient != null) ? new Patient(mrsPatient) : null;
    }

    public Patient getPatientByMotechId(String id) {
        MRSPatient mrsPatient = patientAdapter.getPatientByMotechId(id);
        if (mrsPatient != null) {
            Patient patient = new Patient(mrsPatient);
            Relationship motherRelationship = getMotherRelationship(patient.getMrsPatient().getPerson());
            if (motherRelationship != null) {
                setParentId(patient, motherRelationship);
            }
            return patient;
        }
        return null;
    }

    private void setParentId(Patient patient, Relationship motherRelationship) {
        Person mother = motherRelationship.getPersonA();
        if (mother != null && !mother.getNames().isEmpty()) {
            List<Patient> patients = search(mother.getNames().iterator().next().getFullName(), null);
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

    public List<Patient> search(String name, String motechId) {
        return convert(patientAdapter.search(name, motechId), new Converter<MRSPatient, Patient>() {
            @Override
            public Patient convert(MRSPatient mrsPatient) {
                return new Patient(mrsPatient);
            }
        });
    }

    public Integer getAgeOfPersonByMotechId(String motechId) {
        return patientAdapter.getAgeOfPatientByMotechId(motechId);
    }

    public Patient update(Patient patient) {
        return new Patient(patientAdapter.updatePatient(patient.getMrsPatient()));
    }

    public void createMotherChildRelationship(MRSPerson mother, MRSPerson child) {
        openMRSRelationshipAdapter.createMotherChildRelationship(mother.getId(), child.getId());
    }

    public Relationship getMotherRelationship(MRSPerson person) {
        return openMRSRelationshipAdapter.getMotherRelationship(person.getId());
    }

    public Relationship updateMotherChildRelationship(MRSPerson mother, MRSPerson child) {
        return openMRSRelationshipAdapter.updateMotherRelationship(mother.getId(), child.getId());
    }

    public Relationship voidMotherChildRelationship(MRSPerson child) {
        return openMRSRelationshipAdapter.voidRelationship(child.getId());
    }

    public void deceasePatient(Date dateOfDeath, String patientMotechId, String causeOfDeath, String comment) {
        try {
            patientAdapter.deceasePatient(patientMotechId, causeOfDeath, dateOfDeath, comment);
        } catch (PatientNotFoundException e) {
            logger.warn(e.getMessage());
        }
    }
}
