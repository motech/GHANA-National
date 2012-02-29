package org.motechproject.ghana.national.factory;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.service.request.PregnancyDeliveryRequest;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.mrs.model.MRSObservation;

import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.PREG_TERM_VISIT;
import static org.motechproject.ghana.national.tools.Utility.safeParseInteger;

public class PregnancyEncounterFactory extends BaseObservationFactory {

    public Encounter createTerminationEncounter(PregnancyTerminationRequest pregnancyTerminationRequest) {
        return new Encounter(pregnancyTerminationRequest.getPatient().getMrsPatient(), pregnancyTerminationRequest.getStaff(),
                pregnancyTerminationRequest.getFacility(), PREG_TERM_VISIT, pregnancyTerminationRequest.getTerminationDate(), preparePregnancyTerminationObservations(pregnancyTerminationRequest));
    }

    private Set<MRSObservation> preparePregnancyTerminationObservations(PregnancyTerminationRequest request) {
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();

        for (String complication : request.getComplications()) {
            setObservation(mrsObservations, request.getTerminationDate(), TERMINATION_COMPLICATION.getName(), safeParseInteger(complication));
        }
        setObservation(mrsObservations, request.getTerminationDate(), PREGNANCY_STATUS.getName(), false);
        setObservation(mrsObservations, request.getTerminationDate(), TERMINATION_TYPE.getName(), safeParseInteger(request.getTerminationType()));
        String terminationProcedure = request.getTerminationProcedure();
        if (!terminationProcedure.equals(Constants.NOT_APPLICABLE)) {
            setObservation(mrsObservations, request.getTerminationDate(), TERMINATION_PROCEDURE.getName(), safeParseInteger(terminationProcedure));
        }
        setObservation(mrsObservations, request.getTerminationDate(), MATERNAL_DEATH.getName(), request.isDead());
        setObservation(mrsObservations, request.getTerminationDate(), REFERRED.getName(), request.isReferred());
        setObservation(mrsObservations, request.getTerminationDate(), COMMENTS.getName(), request.getComments());
        setObservation(mrsObservations, request.getTerminationDate(), POST_ABORTION_FP_ACCEPTED.getName(), request.getPostAbortionFPAccepted());
        setObservation(mrsObservations, request.getTerminationDate(), POST_ABORTION_FP_COUNSELING.getName(), request.getPostAbortionFPCounselling());
        return mrsObservations;
    }

    public Encounter createDeliveryEncounter(PregnancyDeliveryRequest pregnancyDeliveryRequest) {
        return new Encounter(pregnancyDeliveryRequest.getPatient().getMrsPatient(), pregnancyDeliveryRequest.getStaff(),
                pregnancyDeliveryRequest.getFacility(), PREG_TERM_VISIT, pregnancyDeliveryRequest.getDeliveryDateTime().toDate(),
                preparePregnancyDeliveryObservations(pregnancyDeliveryRequest));
    }

    private Set<MRSObservation> preparePregnancyDeliveryObservations(PregnancyDeliveryRequest pregnancyDeliveryRequest) {
        return null;
    }
}
