package org.motechproject.ghana.national.repository;

import ch.lambdaj.Lambda;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.on;
import static org.motechproject.ghana.national.domain.Concept.EDD;
import static org.motechproject.ghana.national.domain.Concept.PREGNANCY;

@Repository
public class AllObservations {

    @Autowired
    MRSObservationAdapter mrsObservationAdapter;

    Logger logger = Logger.getLogger(AllObservations.class);

    public MRSObservation findObservation(String patientMotechId, String conceptName) {
        return mrsObservationAdapter.findObservation(patientMotechId, conceptName);
    }

    public List<MRSObservation> findObservations(String patientMotechId, String conceptName) {
        return mrsObservationAdapter.findObservations(patientMotechId, conceptName);
    }

    public MRSObservation findLatestObservation(String patientMotechId, String conceptName) {
        List<MRSObservation> observations = mrsObservationAdapter.findObservations(patientMotechId, conceptName);
        if (CollectionUtils.isNotEmpty(observations)) {
            List<MRSObservation> sortedList = Lambda.sort(observations, on(MRSObservation.class).getDate());
            return sortedList.get(sortedList.size() - 1);
        }
        return null;
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

    public MRSObservation activePregnancyObservation(String motechId) {
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

    public void voidObservation(MRSObservation mrsObservation, String reason, String staffId) {
        if (mrsObservation != null) {
            mrsObservationAdapter.voidObservation(mrsObservation, reason, staffId);
        }
    }
}
