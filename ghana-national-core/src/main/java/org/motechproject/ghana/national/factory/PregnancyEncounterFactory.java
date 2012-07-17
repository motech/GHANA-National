package org.motechproject.ghana.national.factory;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.DeliveryComplications;
import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.service.request.DeliveredChildRequest;
import org.motechproject.ghana.national.service.request.PregnancyDeliveryRequest;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.*;
import static org.motechproject.ghana.national.tools.Utility.safeParseDouble;
import static org.motechproject.ghana.national.tools.Utility.safeParseInteger;

public class PregnancyEncounterFactory extends BaseObservationFactory {

    public Encounter createTerminationEncounter(PregnancyTerminationRequest pregnancyTerminationRequest, MRSObservation activePregnancyObservation) {
        return new Encounter(pregnancyTerminationRequest.getPatient().getMrsPatient(), pregnancyTerminationRequest.getStaff(),
                pregnancyTerminationRequest.getFacility(), PREG_TERM_VISIT, pregnancyTerminationRequest.getTerminationDate(), preparePregnancyTerminationObservations(pregnancyTerminationRequest, activePregnancyObservation));
    }

    private Set<MRSObservation> preparePregnancyTerminationObservations(PregnancyTerminationRequest request, MRSObservation activePregnancyObservation) {
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();

        for (String complication : request.getComplications()) {
            setObservation(mrsObservations, request.getTerminationDate(), TERMINATION_COMPLICATION.getName(), safeParseInteger(complication));
        }
        setObservation(mrsObservations, request.getTerminationDate(), TERMINATION_TYPE.getName(), safeParseInteger(request.getTerminationType()));
        String terminationProcedure = request.getTerminationProcedure();
        if (!terminationProcedure.equals(Constants.NOT_APPLICABLE)) {
            setObservation(mrsObservations, request.getTerminationDate(), TERMINATION_PROCEDURE.getName(), safeParseInteger(terminationProcedure));
        }
        setObservation(mrsObservations, request.getTerminationDate(), MATERNAL_DEATH.getName(), request.isDead());
        setObservation(mrsObservations, request.getTerminationDate(), REFERRED.getName(), request.isReferred());
        setObservation(mrsObservations, request.getTerminationDate(), COMMENTS.getName(), request.getComments());
        setObservation(mrsObservations, request.getTerminationDate(), POST_ABORTION_FP_COUNSELING.getName(), request.getPostAbortionFPCounselling());
        setObservation(mrsObservations, request.getTerminationDate(), POST_ABORTION_FP_ACCEPTED.getName(), request.getPostAbortionFPAccepted());
        if (activePregnancyObservation != null) {
            Set<MRSObservation> dependantObservations = activePregnancyObservation.getDependantObservations();
            if (dependantObservations != null) {
                for (MRSObservation dependantObservation : dependantObservations) {
                    if (dependantObservation.getConceptName().equals(PREGNANCY_STATUS.getName())) {
                        dependantObservations.remove(dependantObservation);
                        break;
                    }
                }
            }
            activePregnancyObservation.addDependantObservation(new MRSObservation<>(request.getTerminationDate(), PREGNANCY_STATUS.getName(), false));
            mrsObservations.add(activePregnancyObservation);
        }
        return mrsObservations;
    }

    public Encounter createDeliveryEncounter(PregnancyDeliveryRequest pregnancyDeliveryRequest, MRSObservation activePregnancyObservation) {
        return new Encounter(pregnancyDeliveryRequest.getPatient().getMrsPatient(), pregnancyDeliveryRequest.getStaff(),
                pregnancyDeliveryRequest.getFacility(), PREG_DEL_VISIT, pregnancyDeliveryRequest.getDeliveryDateTime().toDate(),
                preparePregnancyDeliveryObservations(pregnancyDeliveryRequest, activePregnancyObservation));
    }

    private Set<MRSObservation> preparePregnancyDeliveryObservations(PregnancyDeliveryRequest pregnancyDeliveryRequest, MRSObservation activePregnancyObservation) {
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();

        Date deliveryDate = pregnancyDeliveryRequest.getDeliveryDateTime().toDate();
        setObservation(mrsObservations, deliveryDate, DELIVERY_MODE.getName(), safeEnumValue(pregnancyDeliveryRequest.getChildDeliveryMode()));
        setObservation(mrsObservations, deliveryDate, DELIVERY_OUTCOME.getName(), safeEnumValue(pregnancyDeliveryRequest.getChildDeliveryOutcome()));
        setObservation(mrsObservations, deliveryDate, MALE_INVOLVEMENT.getName(), pregnancyDeliveryRequest.getMaleInvolved());
        setObservation(mrsObservations, deliveryDate, DELIVERY_LOCATION.getName(), safeEnumValue(pregnancyDeliveryRequest.getChildDeliveryLocation()));
        setObservation(mrsObservations, deliveryDate, DELIVERED_BY.getName(), safeEnumValue(pregnancyDeliveryRequest.getChildDeliveredBy()));
        for (DeliveryComplications complication : pregnancyDeliveryRequest.getDeliveryComplications()) {
            setObservation(mrsObservations, deliveryDate, DELIVERY_COMPLICATION.getName(), safeEnumValue(complication));
        }
        setObservation(mrsObservations, deliveryDate, VVF_REPAIR.getName(), safeEnumValue(pregnancyDeliveryRequest.getVvf()));
        setObservation(mrsObservations, deliveryDate, MATERNAL_DEATH.getName(), pregnancyDeliveryRequest.getMaternalDeath());
        setObservation(mrsObservations, deliveryDate, COMMENTS.getName(), pregnancyDeliveryRequest.getComments());
        if (activePregnancyObservation != null) {
            Set<MRSObservation> dependantObservations = activePregnancyObservation.getDependantObservations();
            if (dependantObservations != null) {
                for (MRSObservation dependantObservation : dependantObservations) {
                    if (dependantObservation.getConceptName().equals(PREGNANCY_STATUS.getName())) {
                        dependantObservations.remove(dependantObservation);
                        break;
                    }
                }
            }
        }
        activePregnancyObservation.addDependantObservation(new MRSObservation(deliveryDate, PREGNANCY_STATUS.getName(), Boolean.FALSE));
        mrsObservations.add(activePregnancyObservation);

        for (DeliveredChildRequest deliveredChildRequest : pregnancyDeliveryRequest.getDeliveredChildRequests()) {
            setObservation(mrsObservations, deliveryDate, BIRTH_OUTCOME.getName(), deliveredChildRequest.getChildBirthOutcome().getValue());
        }
        return mrsObservations;
    }

    public Encounter createBirthEncounter(DeliveredChildRequest childRequest, MRSPatient mrsPatient, MRSUser staff, Facility facility, Date birthDate) {
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        setObservation(mrsObservations, birthDate, WEIGHT_KG.getName(), safeParseDouble(childRequest.getChildWeight()));

        Encounter encounter = new Encounter(mrsPatient, staff,
                facility, BIRTH_VISIT, birthDate,
                mrsObservations);
        return encounter;
    }
}
