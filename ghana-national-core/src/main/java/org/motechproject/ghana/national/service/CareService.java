package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.vo.ANCCareHistoryVO;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.vo.CWCCareHistoryVO;
import org.motechproject.ghana.national.vo.CareHistoryVO;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.ANC_REG_NUM;
import static org.motechproject.ghana.national.domain.Concept.BCG;
import static org.motechproject.ghana.national.domain.Concept.CONFINEMENT_CONFIRMED;
import static org.motechproject.ghana.national.domain.Concept.CWC_REG_NUMBER;
import static org.motechproject.ghana.national.domain.Concept.EDD;
import static org.motechproject.ghana.national.domain.Concept.GRAVIDA;
import static org.motechproject.ghana.national.domain.Concept.HEIGHT;
import static org.motechproject.ghana.national.domain.Concept.IMMUNIZATIONS_ORDERED;
import static org.motechproject.ghana.national.domain.Concept.IPT;
import static org.motechproject.ghana.national.domain.Concept.IPTI;
import static org.motechproject.ghana.national.domain.Concept.MEASLES;
import static org.motechproject.ghana.national.domain.Concept.OPV;
import static org.motechproject.ghana.national.domain.Concept.PARITY;
import static org.motechproject.ghana.national.domain.Concept.PENTA;
import static org.motechproject.ghana.national.domain.Concept.PREGNANCY;
import static org.motechproject.ghana.national.domain.Concept.PREGNANCY_STATUS;
import static org.motechproject.ghana.national.domain.Concept.TT;
import static org.motechproject.ghana.national.domain.Concept.VITA;
import static org.motechproject.ghana.national.domain.Concept.YF;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_REG_VISIT;
import static org.motechproject.ghana.national.domain.EncounterType.CWC_REG_VISIT;
import static org.motechproject.ghana.national.domain.EncounterType.PATIENT_HISTORY;
import static org.motechproject.ghana.national.domain.EncounterType.PREG_REG_VISIT;
import static org.motechproject.ghana.national.tools.Utility.safePareInteger;

@Service
public class CareService {
    @Autowired
    PatientService patientService;

    @Autowired
    EncounterService encounterService;

    @Autowired
    ScheduleTrackingService scheduleTrackingService;

    public void enroll(CwcVO cwc) {
        Patient patient = patientService.getPatientByMotechId(cwc.getPatientMotechId());
        encounterService.persistEncounter(patient.getMrsPatient(), cwc.getStaffId(), cwc.getFacilityId(), CWC_REG_VISIT.value(), cwc.getRegistrationDate(),
                prepareObservations(cwc));
    }

    public void enroll(ANCVO ancVO) {
        Date registrationDate = (RegistrationToday.TODAY.equals(ancVO.getRegistrationToday())) ? DateUtil.now().toDate() : ancVO.getRegistrationDate();
        Patient patient = patientService.getPatientByMotechId(ancVO.getPatientMotechId());

        encounterService.persistEncounter(patient.getMrsPatient(), ancVO.getStaffId(), ancVO.getFacilityId(), ANC_REG_VISIT.value(), registrationDate, prepareObservations(ancVO));
        encounterService.persistEncounter(patient.getMrsPatient(), ancVO.getStaffId(), ancVO.getFacilityId(), PREG_REG_VISIT.value(), registrationDate, registerPregnancy(ancVO));
        EnrollmentRequest enrollmentRequest = new ScheduleEnrollmentMapper().mapForDelivery(patient, DateUtil.newDate(ancVO.getEstimatedDateOfDelivery()));
        scheduleTrackingService.enroll(enrollmentRequest);
    }

    private Set<MRSObservation> prepareObservations(ANCVO ancVO) {
        Date observationDate = DateUtil.today().toDate();
        Date registrationDate = (RegistrationToday.TODAY.equals(ancVO.getRegistrationToday())) ? observationDate : ancVO.getRegistrationDate();
        HashSet<MRSObservation> observations = new HashSet<MRSObservation>();
        addObservation(observations, observationDate, GRAVIDA.getName(), ancVO.getGravida());
        addObservation(observations, observationDate, HEIGHT.getName(), ancVO.getHeight());
        addObservation(observations, observationDate, PARITY.getName(), ancVO.getParity());
        addObservation(observations, registrationDate, ANC_REG_NUM.getName(), ancVO.getSerialNumber());

        if (ancVO.getAddHistory()) {
            Set<MRSObservation> historyObservations = addObservationsOnANCHistory(ancVO.getAncCareHistoryVO());
            observations.addAll(historyObservations);
        }
        return observations;
    }

    private HashSet<MRSObservation> registerPregnancy(ANCVO ancVO) {
        Date today = DateUtil.today().toDate();
        final MRSObservation observation = new MRSObservation(today, PREGNANCY.getName(), null);
        addDependentObservation(observation, today, EDD.getName(), ancVO.getEstimatedDateOfDelivery());
        addDependentObservation(observation, today, CONFINEMENT_CONFIRMED.getName(), ancVO.getDeliveryDateConfirmed());
        addDependentObservation(observation, today, PREGNANCY_STATUS.getName(), true);
        return new HashSet<MRSObservation>() {{
            add(observation);
        }};
    }

    Set<MRSObservation> addObservationsOnANCHistory(ANCCareHistoryVO ancCareHistoryVO) {
        List<ANCCareHistory> capturedHistory = ancCareHistoryVO.getCareHistory();
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        addObservation(capturedHistory, ANCCareHistory.IPT, observations, ancCareHistoryVO.getLastIPTDate(), IPT.getName(), safePareInteger(ancCareHistoryVO.getLastIPT()));
        addObservation(capturedHistory, ANCCareHistory.TT, observations, ancCareHistoryVO.getLastTTDate(), TT.getName(), safePareInteger(ancCareHistoryVO.getLastTT()));
        return observations;
    }

    private Set<MRSObservation> prepareObservations(CwcVO cwc) {
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Set<MRSObservation> historyObservations = addObservationsOnCWCHistory(cwc.getCWCCareHistoryVO());
        mrsObservations.addAll(historyObservations);
        mrsObservations.add(new MRSObservation<String>(cwc.getRegistrationDate(), CWC_REG_NUMBER.getName(), cwc.getSerialNumber()));
        return mrsObservations;
    }

    Set<MRSObservation> addObservationsOnCWCHistory(CWCCareHistoryVO cwcCareHistoryVO) {
        List<CwcCareHistory> capturedHistory = cwcCareHistoryVO.getCwcCareHistories();
        Set<MRSObservation> observations = new HashSet<MRSObservation>();

        if (capturedHistory != null && cwcCareHistoryVO.getAddCareHistory()) {
            addObservation(capturedHistory, CwcCareHistory.BCG, observations, cwcCareHistoryVO.getBcgDate(),
                    IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(BCG.getName()));
            addObservation(capturedHistory, CwcCareHistory.VITA_A, observations, cwcCareHistoryVO.getVitADate(),
                    IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(VITA.getName()));
            addObservation(capturedHistory, CwcCareHistory.MEASLES, observations, cwcCareHistoryVO.getMeaslesDate(),
                    IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(MEASLES.getName()));
            addObservation(capturedHistory, CwcCareHistory.YF, observations, cwcCareHistoryVO.getYfDate(),
                    IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(YF.getName()));
            addObservation(capturedHistory, CwcCareHistory.PENTA, observations, cwcCareHistoryVO.getLastPentaDate(), PENTA.getName(), cwcCareHistoryVO.getLastPenta());
            addObservation(capturedHistory, CwcCareHistory.OPV, observations, cwcCareHistoryVO.getLastOPVDate(), OPV.getName(), cwcCareHistoryVO.getLastOPV());
            addObservation(capturedHistory, CwcCareHistory.IPTI, observations, cwcCareHistoryVO.getLastIPTiDate(), IPTI.getName(), cwcCareHistoryVO.getLastIPTi());
        }
        return observations;
    }

    private <T, W> void addObservation(List<W> capturedHistory, W observationType, Set<MRSObservation> observations,
                                       Date observationDate, String observationName, T observationValue) {
        if (capturedHistory.contains(observationType)) {
            observations.add(new MRSObservation<T>(observationDate, observationName, observationValue));
        }
    }

    private <T> void addObservation(Set<MRSObservation> observations, Date observationDate, String observationName, T observationValue) {
        if (observationValue != null) {
            observations.add(new MRSObservation<T>(observationDate, observationName, observationValue));
        }
    }

    private <T> void addDependentObservation(MRSObservation observation, Date observationDate, String observationName, T observationValue) {
        if (observationValue != null) {
            observation.addDependantObservation(new MRSObservation<T>(observationDate, observationName, observationValue));
        }
    }

    private Set<MRSObservation> addObservationsForCareHistory(final CareHistoryVO careHistoryVO) {
        return new HashSet<MRSObservation>() {{
            addAll(addObservationsOnANCHistory(careHistoryVO.getAncCareHistoryVO()));
            addAll(addObservationsOnCWCHistory(careHistoryVO.getCwcCareHistoryVO()));
        }};
    }

    public MRSEncounter addCareHistory(CareHistoryVO careHistory) {
        Patient patient = patientService.getPatientByMotechId(careHistory.getPatientMotechId());
        return encounterService.persistEncounter(patient.getMrsPatient(), careHistory.getStaffId(), careHistory.getFacilityId(), PATIENT_HISTORY.value(), careHistory.getDate(),
                addObservationsForCareHistory(careHistory));
    }
}
