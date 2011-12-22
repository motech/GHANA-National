package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSPatientAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class AllPatients extends MotechAuditableRepository<Patient> {
    private MRSPatientAdaptor patientAdaptor;

    @Autowired
    protected AllPatients(@Qualifier("couchDbConnector") CouchDbConnector db, MRSPatientAdaptor patientAdaptor) {
        super(Patient.class, db);
        this.patientAdaptor = patientAdaptor;
    }

    @Override
    public void add(Patient patient) {
        final MRSPatient savedPatient = patientAdaptor.savePatient(patient.mrsPatient());
        super.add(patient.mrsPatientId(savedPatient.getId()));
    }

    public Patient patientById(String id) {
        MRSPatient mrsPatient = patientAdaptor.getPatientByMotechId(id);
        return (mrsPatient != null) ? new Patient(mrsPatient) : null;
    }

    @View(name = "find_by_patient_ids", map = "function(doc) { if(doc.type === 'Patient') emit(null, doc); }")
    public List<Patient> findByPatientIds(Set<String> patientIds) {
        ViewQuery viewQuery = createQuery("find_by_patient_ids").keys(patientIds);
        return db.queryView(viewQuery, Patient.class);
    }
}
