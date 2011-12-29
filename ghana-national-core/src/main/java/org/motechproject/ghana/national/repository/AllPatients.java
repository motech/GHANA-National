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

import java.util.List;

import static ch.lambdaj.Lambda.convert;

@Repository
public class AllPatients {
    @Autowired
    private MRSPatientAdaptor patientAdaptor;

    @Autowired
    private OpenMRSRelationshipAdaptor openMRSRelationshipAdaptor;

    public String save(Patient patient) {
        final MRSPatient savedPatient = patientAdaptor.savePatient(patient.mrsPatient());
        return savedPatient.getMotechId();
    }

    public Patient patientById(String id) {
        MRSPatient mrsPatient = patientAdaptor.getPatientByMotechId(id);
        return (mrsPatient != null) ? new Patient(mrsPatient) : null;
    }

    public List<Patient> search(String name, String motechId) {
        return convert(patientAdaptor.search(name, motechId), new Converter<MRSPatient, Patient>() {
            @Override
            public Patient convert(MRSPatient mrsPatient) {
                return new Patient(mrsPatient);
            }
        });
    }

    public String update(Patient patient) {
        return patientAdaptor.updatePatient(patient.mrsPatient());
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
}
