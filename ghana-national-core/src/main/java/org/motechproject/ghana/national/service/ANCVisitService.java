package org.motechproject.ghana.national.service;

import org.apache.log4j.Logger;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllObservations;
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

import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_VISIT;

@Service
public class ANCVisitService {

    @Autowired
    EncounterService encounterService;

    @Autowired
    PatientService patientService;

    @Autowired
    AllObservations allObservations;

    @Autowired
    MotherVisitService motherVisitService;

    private Logger logger = Logger.getLogger(ANCVisitService.class);

    public MRSEncounter registerANCVisit(ANCVisit ancVisit) {
        Patient patient = patientService.getPatientByMotechId(ancVisit.getMotechId());
        Set<MRSObservation> mrsObservations = createMRSObservations(ancVisit);
        createEDDScheduleForANCVisit(ancVisit, patient, mrsObservations);
        return encounterService.persistEncounter(patient.getMrsPatient(), ancVisit.getStaffId(),
                ancVisit.getFacilityId(), ANC_VISIT.value(), ancVisit.getDate(), mrsObservations);
    }

    private void createEDDScheduleForANCVisit(final ANCVisit ancVisit, Patient patient, Set<MRSObservation> mrsObservations) {
        Date newEdd = ancVisit.getEstDeliveryDate();
        if (newEdd == null) {
            return;
        }

        MRSObservation activePregnancyObservation = allObservations.findObservation(ancVisit.getMotechId(), Concept.PREGNANCY.getName());
        if (activePregnancyObservation == null) {
            logger.warn("No active pregnancy found while checking for EDD. Patient ID :" + patient.getMrsPatient().getMotechId());
            return; //no active pregnancy
        }

        MRSObservation eddObservation = allObservations.findObservation(ancVisit.getMotechId(), Concept.EDD.getName());
        Date oldEdd = (eddObservation == null) ? null : (Date) eddObservation.getValue();

        if (oldEdd == null || !oldEdd.equals(newEdd)) {
            mrsObservations.add(createNewEddObservation(ancVisit, activePregnancyObservation, eddObservation));
            motherVisitService.createEDDScheduleForANCVisit(patient, ancVisit.getEstDeliveryDate());
        }
    }

    private MRSObservation createNewEddObservation(final ANCVisit ancVisit, MRSObservation activePregnancyObservation, MRSObservation eddObservation) {
        allObservations.voidObservation(eddObservation, "Replaced by new EDD value", ancVisit.getStaffId());
        activePregnancyObservation.setDependantObservations(new HashSet<MRSObservation>() {{
            add(new MRSObservation<Date>(new Date(), EDD.getName(), ancVisit.getEstDeliveryDate()));
        }});
        return activePregnancyObservation;
    }

    Set<MRSObservation> createMRSObservations(ANCVisit ancVisit) {
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Date registrationDate = ancVisit.getDate() == null ? DateUtil.today().toDate() : ancVisit.getDate();
        setObservation(mrsObservations, registrationDate, SERIAL_NUMBER.getName(), ancVisit.getSerialNumber());
        setObservation(mrsObservations, registrationDate, VISIT_NUMBER.getName(), toInteger(ancVisit.getVisitNumber()));
        setObservation(mrsObservations, registrationDate, MALE_INVOLVEMENT.getName(), ancVisit.getMaleInvolved());
        setObservation(mrsObservations, registrationDate, SYSTOLIC_BLOOD_PRESSURE.getName(), ancVisit.getBpSystolic());
        setObservation(mrsObservations, registrationDate, DIASTOLIC_BLOOD_PRESSURE.getName(), ancVisit.getBpDiastolic());
        setObservation(mrsObservations, registrationDate, WEIGHT_KG.getName(), ancVisit.getWeight());
        setObservation(mrsObservations, registrationDate, TT.getName(), toInteger(ancVisit.getTtdose()));
        setObservation(mrsObservations, registrationDate, IPT.getName(), toInteger(ancVisit.getIptdose()));
        setObservation(mrsObservations, registrationDate, FHR.getName(), ancVisit.getFhr());
        setObservation(mrsObservations, registrationDate, FHT.getName(), ancVisit.getFht());
        setObservation(mrsObservations, registrationDate, URINE_PROTEIN_TEST.getName(), getConceptForTest(ancVisit.getUrineTestProteinPositive()));
        setObservation(mrsObservations, registrationDate, URINE_GLUCOSE_TEST.getName(), getConceptForTest(ancVisit.getUrineTestGlucosePositive()));
        setObservation(mrsObservations, registrationDate, IPT_REACTION.getName(), getConceptReactionResult(ancVisit.getIptReactive()));
        setObservation(mrsObservations, registrationDate, HEMOGLOBIN.getName(), ancVisit.getHemoglobin());
        setObservation(mrsObservations, registrationDate, VDRL.getName(), getConceptReactionResult(ancVisit.getVdrlReactive()));
        setObservation(mrsObservations, registrationDate, HIV_TEST_RESULT.getName(), ancVisit.getHivTestResult());
        setObservation(mrsObservations, registrationDate, ANC_PNC_LOCATION.getName(), toInteger(ancVisit.getLocation()));
        setObservation(mrsObservations, registrationDate, HOUSE.getName(), ancVisit.getHouse());
        setObservation(mrsObservations, registrationDate, COMMUNITY.getName(), ancVisit.getCommunity());
        setObservation(mrsObservations, registrationDate, COMMENTS.getName(), ancVisit.getComments());
        setObservation(mrsObservations, registrationDate, NEXT_ANC_DATE.getName(), ancVisit.getNextANCDate());
        setObservation(mrsObservations, registrationDate, VDRL_TREATMENT.getName(), toBoolean(ancVisit.getVdrlTreatment()));
        setObservation(mrsObservations, registrationDate, DEWORMER.getName(), toBoolean(ancVisit.getDewormer()));
        setObservation(mrsObservations, registrationDate, PMTCT.getName(), toBoolean(ancVisit.getPmtct()));
        setObservation(mrsObservations, registrationDate, INSECTICIDE_TREATED_NET_USAGE.getName(), toBoolean(ancVisit.getItnUse()));
        setObservation(mrsObservations, registrationDate, PMTCT_TREATMENT.getName(), toBoolean(ancVisit.getPmtctTreament()));
        setObservation(mrsObservations, registrationDate, HIV_PRE_TEST_COUNSELING.getName(), toBoolean(ancVisit.getPreTestCounseled()));
        setObservation(mrsObservations, registrationDate, HIV_POST_TEST_COUNSELING.getName(), toBoolean(ancVisit.getPostTestCounseled()));
        setObservation(mrsObservations, registrationDate, REFERRED.getName(), toBoolean(ancVisit.getReferred()));
        return mrsObservations;
    }

    private Boolean toBoolean(String value) {
        return (value != null) ? value.equals(Constants.OBSERVATION_YES) : null;
    }

    private Integer toInteger(String value) {
        return (value != null && !value.equals(Constants.NOT_APPLICABLE)) ? Integer.parseInt(value) : null;
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

    public <T> void setObservation(HashSet<MRSObservation> mrsObservations, Date registrationDate, String conceptType, T value) {
        if (value != null)
            mrsObservations.add(new MRSObservation<T>(registrationDate, conceptType, value));
    }
}
