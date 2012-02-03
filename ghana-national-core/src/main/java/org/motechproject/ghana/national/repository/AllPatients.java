package org.motechproject.ghana.national.repository;

import ch.lambdaj.function.convert.Converter;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.motechproject.openmrs.services.OpenMRSRelationshipAdaptor;
import org.openmrs.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

@Repository
public class AllPatients {
    @Autowired
    private MRSPatientAdaptor patientAdaptor;

    @Autowired
    private OpenMRSRelationshipAdaptor openMRSRelationshipAdaptor;

    public Patient save(Patient patient) {
        MRSPatient mrsPatient = patientAdaptor.savePatient(patient.getMrsPatient());
        return new Patient(mrsPatient, patient.getParentId());
    }

    public Patient patientByOpenmrsId(String patientId) {
        MRSPatient mrsPatient = patientAdaptor.getPatient(patientId);
        return (mrsPatient != null) ? new Patient(mrsPatient) : null;
    }

    public Patient patientByMotechId(String id) {
        MRSPatient mrsPatient = getPatientByMotechId(id);
        return (mrsPatient != null) ? new Patient(mrsPatient) : null;
    }

    private MRSPatient getPatientByMotechId(String id) {
        return patientAdaptor.getPatientByMotechId(id);
    }

    public List<Patient> search(String name, String motechId) {
        return convert(patientAdaptor.search(name, motechId), new Converter<MRSPatient, Patient>() {
            @Override
            public Patient convert(MRSPatient mrsPatient) {
                return new Patient(mrsPatient);
            }
        });
    }
    
    public Integer getAgeOfPersonByMotechId(String motechId){
        return patientAdaptor.getAgeOfPatientByMotechId(motechId);
    }

    public String update(Patient patient) {
        return patientAdaptor.updatePatient(patient.getMrsPatient());
    }

    public void createMotherChildRelationship(MRSPerson mother, MRSPerson child) {
        openMRSRelationshipAdaptor.createMotherChildRelationship(mother.getId(), child.getId());
    }

    public Relationship getMotherRelationship(MRSPerson person) {
        return openMRSRelationshipAdaptor.getMotherRelationship(person.getId());
    }

    public Relationship updateMotherChildRelationship(MRSPerson mother, MRSPerson child) {
        return openMRSRelationshipAdaptor.updateMotherRelationship(mother.getId(), child.getId());
    }

    public Relationship voidMotherChildRelationship(MRSPerson child) {
        return openMRSRelationshipAdaptor.voidRelationship(child.getId());
    }

    public void saveCauseOfDeath(Date dateOfDeath, String mrsPatientId, String causeOfDeath, String comment) {
       patientAdaptor.savePatientCauseOfDeathObservation(mrsPatientId, causeOfDeath, dateOfDeath, comment);
    }
}
