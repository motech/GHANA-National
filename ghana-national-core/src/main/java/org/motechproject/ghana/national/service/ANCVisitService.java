package org.motechproject.ghana.national.service;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.ANCVisit;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.joda.time.PeriodType.weeks;

@Service
public class ANCVisitService {

    @Autowired
    EncounterService encounterService;
    @Autowired
    PatientService patientService;

    public MRSEncounter registerANCVisit(ANCVisit ancVisit) {
        Patient patientByMotechId = patientService.getPatientByMotechId(ancVisit.getMotechId());
        return encounterService.persistEncounter(patientByMotechId.getMrsPatient(), ancVisit.getStaffId(),
                ancVisit.getFacilityId(), Constants.ENCOUNTER_ANCVISIT, ancVisit.getDate(), createMRSObservations(ancVisit));
    }

    Set<MRSObservation> createMRSObservations(ANCVisit ancVisit) {
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Date registrationDate = ancVisit.getDate() == null ? DateUtil.today().toDate() : ancVisit.getDate();

        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_SERIAL_NUMBER, ancVisit.getSerialNumber());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_VISIT_NUMBER, ancVisit.getVisitNumber());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_MALE_INVOLVEMENT, ancVisit.getMaleInvolved());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_EDD, ancVisit.getEstDeliveryDate());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_SYSTOLIC_BLOOD_PRESSURE, ancVisit.getBpSystolic());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_DIASTOLIC_BLOOD_PRESSURE, ancVisit.getBpDiastolic());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_WEIGHT_KG, ancVisit.getWeight());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_TT, ancVisit.getTtdose());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_IPT, ancVisit.getIptdose());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_INSECTICIDE_TREATED_NET_USAGE, ancVisit.getItnUse());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_FHR, ancVisit.getFhr());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_FHT, ancVisit.getFht());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_URINE_PROTEIN_TEST, getConceptForTest(ancVisit.getUrineTestProteinPositive()));
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_URINE_GLUCOSE_TEST, getConceptForTest(ancVisit.getUrineTestGlucosePositive()));
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_IPT_REACTION, getConceptReactionResult(ancVisit.getIptReactive()));
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_HEMOGLOBIN, ancVisit.getHemoglobin());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_VDRL, getConceptReactionResult(ancVisit.getVdrlReactive()));
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_HIV_TEST_RESULT, ancVisit.getHivTestResult());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_ANC_PNC_LOCATION, Integer.parseInt(ancVisit.getLocation()));
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_HOUSE, ancVisit.getHouse());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_COMMUNITY, ancVisit.getCommunity());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_COMMENTS, ancVisit.getComments());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_NEXT_ANC_DATE, ancVisit.getNextANCDate());
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_VDRL_TREATMENT, toBoolean(ancVisit.getVdrlTreatment()));
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_DEWORMER, toBoolean(ancVisit.getDewormer()));
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_PMTCT, toBoolean(ancVisit.getPmtct()));
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_PMTCT_TREATMENT, toBoolean(ancVisit.getPmtctTreament()));
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_HIV_PRE_TEST_COUNSELING, toBoolean(ancVisit.getPreTestCounseled()));
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_HIV_POST_TEST_COUNSELING, toBoolean(ancVisit.getPostTestCounseled()));
        setObservation(mrsObservations, registrationDate, Constants.CONCEPT_REFERRED, toBoolean(ancVisit.getReferred()));
        return mrsObservations;
    }

    private Boolean toBoolean(String value) {
        return (value != null) ? value.equals(Constants.OBSERVATION_YES) : null;
    }

    private MRSConcept getConceptReactionResult(String reading) {
        if (reading == null) return null;
        return (reading.equals(Constants.OBSERVATION_YES)) ? new MRSConcept(Constants.CONCEPT_REACTIVE) : new MRSConcept(Constants.CONCEPT_NON_REACTIVE);
    }

    private MRSConcept getConceptForTest(String reading) {
        if (reading == null) return null;
        MRSConcept concept = null;
        switch (Integer.valueOf(reading)) {
            case 0:
                concept = new MRSConcept(Constants.CONCEPT_NEGATIVE);
                break;
            case 1:
                concept = new MRSConcept(Constants.CONCEPT_POSITIVE);
                break;
            case 2:
                concept = new MRSConcept(Constants.CONCEPT_TRACE);
                break;
        }
        return concept;
    }

    public int currentWeekOfPregnancy(LocalDate expectedDeliveryDate) {
        LocalDate today = DateUtil.today();
        LocalDate dateOfConception = expectedDeliveryDate.minusWeeks(40).minusDays(6);
        if (!dateOfConception.isBefore(today)) return -1;
        return new Period(dateOfConception.toDate().getTime(), today.toDate().getTime(), weeks()).getWeeks();
    }

    public <T> void setObservation(HashSet<MRSObservation> mrsObservations, Date registrationDate, String conceptType, T value) {
        if (value != null)
            mrsObservations.add(new MRSObservation<T>(registrationDate, conceptType, value));
    }
}
