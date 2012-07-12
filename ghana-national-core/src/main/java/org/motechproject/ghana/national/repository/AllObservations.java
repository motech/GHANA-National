package org.motechproject.ghana.national.repository;

import ch.lambdaj.Lambda;
import org.apache.commons.collections.CollectionUtils;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    //todo remove dependency on mrsObservationAdapter, move to service layer - this class is just an abstraction of couch repo
    @Autowired
    MRSObservationAdapter mrsObservationAdapter;

    Logger logger = LoggerFactory.getLogger(this.getClass());

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

    public Set<MRSObservation> updateEDD(Date estimatedDeliveryDate, Patient patient, String staffId, Date observationDate) {
        HashSet<MRSObservation> observations = new HashSet<MRSObservation>();
        if (estimatedDeliveryDate == null) {
            return observations;
        }
        MRSObservation eddObservation = findObservation(patient.getMotechId(), EDD.getName());
        Date oldEdd = (eddObservation == null) ? null : (Date) eddObservation.getValue();
        if (oldEdd == null || !oldEdd.equals(estimatedDeliveryDate)) {
            voidObservation(eddObservation, "Replaced by new EDD value", staffId);
            observations.add(createNewEddObservation(activePregnancyObservation(patient.getMotechId(), observationDate), estimatedDeliveryDate, observationDate));
        }
        return observations;
    }

    public MRSObservation activePregnancyObservation(String motechId, Date observationDate) {
        MRSObservation activePregnancyObservation = findLatestObservation(motechId, PREGNANCY.getName());
        if (activePregnancyObservation == null) {
            logger.debug("No active pregnancy found while checking for EDD. Patient ID :" + motechId);
            activePregnancyObservation = new MRSObservation<Object>(observationDate, PREGNANCY.getName(), null);
        }
        return activePregnancyObservation;
    }

    private MRSObservation createNewEddObservation(MRSObservation activePregnancyObservation, Date estDeliveryDate, Date observationDate) {
        activePregnancyObservation.addDependantObservation(new MRSObservation<Date>(observationDate, EDD.getName(), estDeliveryDate));
        return activePregnancyObservation;
    }

    public void voidObservation(MRSObservation mrsObservation, String reason, String staffId) {
        try {
            if (mrsObservation != null) {
                mrsObservationAdapter.voidObservation(mrsObservation, reason, staffId);
            }
        } catch (ObservationNotFoundException e) {
            logger.warn(e.getMessage());
        }
    }
}
