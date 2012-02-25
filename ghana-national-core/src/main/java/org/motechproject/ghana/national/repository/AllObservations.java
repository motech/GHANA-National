package org.motechproject.ghana.national.repository;

import org.apache.log4j.Logger;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.EDD;
import static org.motechproject.ghana.national.domain.Concept.PREGNANCY;

@Repository
public class AllObservations {

    @Autowired
    MRSObservationAdapter mrsObservationAdapter;

    Logger logger = Logger.getLogger(AllObservations.class);

    public void voidObservation(MRSObservation mrsObservation, String reason, String userMotechId) {
        mrsObservationAdapter.voidObservation(mrsObservation, reason, userMotechId);
    }

    public MRSObservation findObservation(String patientMotechId, String conceptName) {
        return mrsObservationAdapter.findObservation(patientMotechId, conceptName);
    }

    public Set<MRSObservation> updateEDD(Date estimatedDeliveryDate, Patient patient, String staffId) {
        HashSet<MRSObservation> observations = new HashSet<MRSObservation>();
        String motechId = patient.getMotechId();

        if (estimatedDeliveryDate == null) {
            return observations;
        }

        MRSObservation activePregnancyObservation = findObservation(motechId, PREGNANCY.getName());
        if (activePregnancyObservation == null) {
            logger.warn("No active pregnancy found while checking for EDD. Patient ID :" + patient.getMrsPatient().getMotechId());
            return observations; //no active pregnancy
        }

        MRSObservation eddObservation = findObservation(motechId, EDD.getName());
        Date oldEdd = (eddObservation == null) ? null : (Date) eddObservation.getValue();

        if (oldEdd == null || !oldEdd.equals(estimatedDeliveryDate)) {
            observations.add(createNewEddObservation(activePregnancyObservation, eddObservation, staffId, estimatedDeliveryDate));
        }
        return observations;
    }

    private MRSObservation createNewEddObservation(MRSObservation activePregnancyObservation, MRSObservation eddObservation, String staffId, Date estDeliveryDate) {
        voidObservation(eddObservation, "Replaced by new EDD value", staffId);
        activePregnancyObservation.addDependantObservation(new MRSObservation<Date>(new Date(), EDD.getName(), estDeliveryDate));
        return activePregnancyObservation;
    }

}
