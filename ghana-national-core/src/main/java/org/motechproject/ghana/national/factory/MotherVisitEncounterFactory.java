package org.motechproject.ghana.national.factory;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.domain.IPTVaccine;
import org.motechproject.ghana.national.vo.ANCVisit;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_VISIT;

public class MotherVisitEncounterFactory extends BaseObservationFactory {


    public Encounter createEncounter(ANCVisit ancVisit, Set<MRSObservation> mrsObservations) {
        return new Encounter(ancVisit.getPatient().getMrsPatient(), ancVisit.getStaff(),
                ancVisit.getFacility(), ANC_VISIT, ancVisit.getDate(), mrsObservations);
    }

    public Set<MRSObservation> createMRSObservations(ANCVisit ancVisit) {
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Date registrationDate = ancVisit.getDate() == null ? DateUtil.today().toDate() : ancVisit.getDate();

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
        setObservation(mrsObservations, registrationDate, TT.getName(), toInteger(ancVisit.getTtdose()));
        setObservation(mrsObservations, registrationDate, URINE_GLUCOSE_TEST.getName(), getConceptForTest(ancVisit.getUrineTestGlucosePositive()));
        setObservation(mrsObservations, registrationDate, URINE_PROTEIN_TEST.getName(), getConceptForTest(ancVisit.getUrineTestProteinPositive()));
        setObservation(mrsObservations, registrationDate, VDRL.getName(), getConceptReactionResult(ancVisit.getVdrlReactive()));
        setObservation(mrsObservations, registrationDate, VDRL_TREATMENT.getName(), toBoolean(ancVisit.getVdrlTreatment()));
        setObservation(mrsObservations, registrationDate, VISIT_NUMBER.getName(), toInteger(ancVisit.getVisitNumber()));
        setObservation(mrsObservations, registrationDate, WEIGHT_KG.getName(), ancVisit.getWeight());
        return mrsObservations;
    }

    public Set<MRSObservation> createObservationsForIPT(final IPTVaccine iptVaccine) {
        final LocalDate observationDate = iptVaccine.getVaccinationDate();
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        setObservation(observations, observationDate.toDate(), Concept.IPT.getName(), iptVaccine.getIptDose());
        setObservation(observations, observationDate.toDate(), Concept.IPT_REACTION.getName(), new MRSConcept(iptVaccine.getIptReactionConceptName()));
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
