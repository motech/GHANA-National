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
        if (estimatedDeliveryDate == null) {
            return observations;
        }
        MRSObservation eddObservation = findObservation(patient.getMotechId(), EDD.getName());
        Date oldEdd = (eddObservation == null) ? null : (Date) eddObservation.getValue();
        if (oldEdd == null || !oldEdd.equals(estimatedDeliveryDate)) {
            observations.add(createNewEddObservation(activePregnancyObservation(patient.getMotechId()), eddObservation, staffId, estimatedDeliveryDate));
        }
        return observations;
    }

    private MRSObservation activePregnancyObservation(String motechId) {
        MRSObservation activePregnancyObservation = findObservation(motechId, PREGNANCY.getName());
        if (activePregnancyObservation == null) {
            logger.warn("No active pregnancy found while checking for EDD. Patient ID :" + motechId);
            activePregnancyObservation = new MRSObservation<Object>(new Date(), PREGNANCY.getName(), null);
        }
        return activePregnancyObservation;
    }
    
    private MRSObservation createNewEddObservation(MRSObservation activePregnancyObservation, MRSObservation eddObservation, String staffId, Date estDeliveryDate) {
        voidObservation(eddObservation, "Replaced by new EDD value", staffId);
        activePregnancyObservation.addDependantObservation(new MRSObservation<Date>(new Date(), EDD.getName(), estDeliveryDate));
        return activePregnancyObservation;
    }

}
