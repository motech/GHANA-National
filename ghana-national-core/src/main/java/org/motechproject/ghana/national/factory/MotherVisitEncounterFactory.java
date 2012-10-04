package org.motechproject.ghana.national.factory;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.domain.ANCVisit;
import org.motechproject.ghana.national.service.request.PNCMotherRequest;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSObservation;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_VISIT;
import static org.motechproject.ghana.national.domain.EncounterType.PNC_MOTHER_VISIT;

public class MotherVisitEncounterFactory extends BaseObservationFactory {


    public Encounter createEncounter(ANCVisit ancVisit, Set<MRSObservation> mrsObservations) {
        return new Encounter(ancVisit.getPatient().getMrsPatient(), ancVisit.getStaff(),
                ancVisit.getFacility(), ANC_VISIT, ancVisit.getDate(), mrsObservations);
    }

    public Encounter createEncounter(PNCMotherRequest pncMotherRequest) {
        return new Encounter(pncMotherRequest.getPatient().getMrsPatient(), pncMotherRequest.getStaff(), pncMotherRequest.getFacility(),
                PNC_MOTHER_VISIT, pncMotherRequest.getDate().toDate(), createMRSObservations(pncMotherRequest));
    }

    public Set<MRSObservation> createMRSObservations(ANCVisit ancVisit) {
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Date registrationDate = ancVisit.getDate();

        setObservation(mrsObservations, registrationDate, ANC_PNC_LOCATION.getName(), toInteger(ancVisit.getLocation()));
        setObservation(mrsObservations, registrationDate, COMMUNITY.getName(), ancVisit.getCommunity());
        setObservation(mrsObservations, registrationDate, COMMENTS.getName(), ancVisit.getComments());
        setObservation(mrsObservations, registrationDate, DEWORMER.getName(), toBoolean(ancVisit.getDewormer()));
        setObservation(mrsObservations, registrationDate, DIASTOLIC_BLOOD_PRESSURE.getName(), ancVisit.getBpDiastolic());
        setObservation(mrsObservations, registrationDate, FHR.getName(), ancVisit.getFhr());
        setObservation(mrsObservations, registrationDate, FHT.getName(), ancVisit.getFht());
        setObservation(mrsObservations, registrationDate, HEMOGLOBIN.getName(), ancVisit.getHemoglobin());
        setObservation(mrsObservations, registrationDate, HIV_TEST_RESULT.getName(), ancVisit.getHivTestResult());
        setObservation(mrsObservations, registrationDate, HIV_PRE_TEST_COUNSELING.getName(), toBoolean(ancVisit.getPreTestCounseled()));
        setObservation(mrsObservations, registrationDate, HIV_POST_TEST_COUNSELING.getName(), toBoolean(ancVisit.getPostTestCounseled()));
        setObservation(mrsObservations, registrationDate, HOUSE.getName(), ancVisit.getHouse());
        setObservation(mrsObservations, registrationDate, INSECTICIDE_TREATED_NET_USAGE.getName(), toBoolean(ancVisit.getItnUse()));
        setObservation(mrsObservations, registrationDate, MALE_INVOLVEMENT.getName(), ancVisit.getMaleInvolved());
        setObservation(mrsObservations, registrationDate, NEXT_ANC_DATE.getName(), ancVisit.getNextANCDate());
        setObservation(mrsObservations, registrationDate, PMTCT.getName(), toBoolean(ancVisit.getPmtct()));
        setObservation(mrsObservations, registrationDate, PMTCT_TREATMENT.getName(), toBoolean(ancVisit.getPmtctTreament()));
        setObservation(mrsObservations, registrationDate, REFERRED.getName(), toBoolean(ancVisit.getReferred()));
        setObservation(mrsObservations, registrationDate, SERIAL_NUMBER.getName(), ancVisit.getSerialNumber());
        setObservation(mrsObservations, registrationDate, SYSTOLIC_BLOOD_PRESSURE.getName(), ancVisit.getBpSystolic());
        setObservation(mrsObservations, registrationDate, URINE_GLUCOSE_TEST.getName(), getConceptForTest(ancVisit.getUrineTestGlucosePositive()));
        setObservation(mrsObservations, registrationDate, URINE_PROTEIN_TEST.getName(), getConceptForTest(ancVisit.getUrineTestProteinPositive()));
        setObservation(mrsObservations, registrationDate, VDRL.getName(), getConceptReactionResult(ancVisit.getVdrlReactive()));
        setObservation(mrsObservations, registrationDate, VDRL_TREATMENT.getName(), toBoolean(ancVisit.getVdrlTreatment()));
        setObservation(mrsObservations, registrationDate, VISIT_NUMBER.getName(), toInteger(ancVisit.getVisitNumber()));
        setObservation(mrsObservations, registrationDate, WEIGHT_KG.getName(), ancVisit.getWeight());
        return mrsObservations;
    }

    public Set<MRSObservation> createMRSObservations(PNCMotherRequest pncMotherRequest) {
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Date registrationDate = pncMotherRequest.getDate().toDate();

        setObservation(mrsObservations, registrationDate, TT.getName(), toInteger(pncMotherRequest.getTtDose()));
        setObservation(mrsObservations, registrationDate, VISIT_NUMBER.getName(), pncMotherRequest.getVisit().visitNumber());
        setObservation(mrsObservations, registrationDate, VITA.getName(), toBoolean(pncMotherRequest.getVitaminA()));
        setObservation(mrsObservations, registrationDate, COMMENTS.getName(), pncMotherRequest.getComments());
        setObservation(mrsObservations, registrationDate, COMMUNITY.getName(), pncMotherRequest.getCommunity());
        setObservation(mrsObservations, registrationDate, FHT.getName(), toInteger(pncMotherRequest.getFht()));
        setObservation(mrsObservations, registrationDate, HOUSE.getName(), pncMotherRequest.getHouse());
        setObservation(mrsObservations, registrationDate, MALE_INVOLVEMENT.getName(), pncMotherRequest.getMaleInvolved());
        setObservation(mrsObservations, registrationDate, REFERRED.getName(), pncMotherRequest.getReferred());
        setObservation(mrsObservations, registrationDate, LOCHIA_EXCESS_AMOUNT.getName(), pncMotherRequest.getLochiaAmountExcess());
        setObservation(mrsObservations, registrationDate, LOCHIA_COLOUR.getName(), toInteger(pncMotherRequest.getLochiaColour()));
        setObservation(mrsObservations, registrationDate, LOCHIA_FOUL_ODOUR.getName(), pncMotherRequest.getLochiaOdourFoul());
        setObservation(mrsObservations, registrationDate, TEMPERATURE.getName(), pncMotherRequest.getTemperature());
        setObservation(mrsObservations, registrationDate, ANC_PNC_LOCATION.getName(), toInteger(pncMotherRequest.getLocation()));
        return mrsObservations;
    }

    public Set<MRSObservation> createObservationsForIPT(final IPTVaccine iptVaccine) {
        final LocalDate observationDate = iptVaccine.getVaccinationDate();
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        setObservation(observations, observationDate.toDate(), Concept.IPT.getName(), iptVaccine.getIptDose());
        setObservation(observations, observationDate.toDate(), Concept.IPT_REACTION.getName(), new MRSConcept(iptVaccine.getIptReactionConceptName()));
        return observations;
    }

    public Set<MRSObservation> createObservationForTT(final TTVaccine ttVaccine) {
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        setObservation(observations, ttVaccine.getVaccinationDate().toDate(), Concept.TT.getName(), ttVaccine.getDosage().getDosage());
        return observations;
    }

    private MRSConcept getConceptReactionResult(String reading) {
        if (reading == null) return null;
        return (reading.equals(Constants.OBSERVATION_YES)) ? new MRSConcept(REACTIVE.getName()) : new MRSConcept(NON_REACTIVE.getName());
    }

    private MRSConcept getConceptForTest(String reading) {
        if (reading == null) return null;
        MRSConcept concept = null;
        switch (Integer.valueOf(reading)) {
            case 0:
                concept = new MRSConcept(NEGATIVE.getName());
                break;
            case 1:
                concept = new MRSConcept(POSITIVE.getName());
                break;
            case 2:
                concept = new MRSConcept(TRACE.getName());
                break;
        }
        return concept;
    }
}
