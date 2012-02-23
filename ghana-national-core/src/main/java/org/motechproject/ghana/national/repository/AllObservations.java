package org.motechproject.ghana.national.repository;

import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllObservations {

    @Autowired
    MRSObservationAdapter mrsObservationAdapter;

    public void voidObservation(MRSObservation mrsObservation, String reason, String userMotechId) {
        mrsObservationAdapter.voidObservation(mrsObservation, reason, userMotechId);
    }

    public MRSObservation findObservation(String patientMotechId, String conceptName) {
        return mrsObservationAdapter.findObservation(patientMotechId, conceptName);
    }
}
