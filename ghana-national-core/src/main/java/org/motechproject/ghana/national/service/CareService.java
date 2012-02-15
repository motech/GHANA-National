package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.vo.*;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Constants.*;
import static org.motechproject.ghana.national.tools.Utility.safePareInteger;

@Service
public class CareService {
    @Autowired
    StaffService staffService;

    @Autowired
    PatientService patientService;

    @Autowired
    AllEncounters allEncounters;

    @Autowired
    ScheduleTrackingService scheduleTrackingService;

    public MRSEncounter getEncounter(String motechId, String encounterType) {
        return allEncounters.fetchLatest(motechId, encounterType);
    }

    public void enroll(CwcVO cwc) {
        Patient patient = patientService.getPatientByMotechId(cwc.getPatientMotechId());
        persistEncounter(patient, cwc.getStaffId(), cwc.getFacilityId(), ENCOUNTER_CWCREGVISIT, cwc.getRegistrationDate(),
                prepareObservations(cwc));
    }

    public void enroll(ANCVO ancVO) {
        Date registrationDate = (RegistrationToday.TODAY.equals(ancVO.getRegistrationToday())) ? DateUtil.now().toDate() : ancVO.getRegistrationDate();
        Patient patient = patientService.getPatientByMotechId(ancVO.getPatientMotechId());
        persistEncounter(patient, ancVO.getStaffId(), ancVO.getFacilityId(),
                ENCOUNTER_ANCREGVISIT, registrationDate, prepareObservations(ancVO));
        persistEncounter(patient, ancVO.getStaffId(), ancVO.getFacilityId(),
                ENCOUNTER_PREGREGVISIT, registrationDate, registerPregnancy(ancVO));

        EnrollmentRequest enrollmentRequest = new ScheduleEnrollmentMapper().map(patient, DateUtil.newDate(ancVO.getEstimatedDateOfDelivery()));
        scheduleTrackingService.enroll(enrollmentRequest);
    }

    public MRSEncounter persistEncounter(Patient patient, String staffId, String facilityId, String encounterType, Date registrationDate,
                                         Set<MRSObservation> mrsObservations) {
        String patientId = patient.getMrsPatient().getId();
        return persistEncounter(patientId, staffId, facilityId, encounterType, registrationDate, mrsObservations);
    }

    MRSEncounter persistEncounter(String patientMotechId, String staffId, String facilityId, String encounterType,
                                  Date registrationDate, Set<MRSObservation> mrsObservations) {
        MRSUser user = staffService.getUserByEmailIdOrMotechId(staffId);
        String staffProviderId = user.getPerson().getId();
        String staffUserId = user.getId();
        MRSEncounter mrsEncounter = new MRSEncounter(staffProviderId, staffUserId, facilityId,
                registrationDate, patientMotechId, mrsObservations, encounterType);
        return allEncounters.save(mrsEncounter);
    }

    private Set<MRSObservation> prepareObservations(ANCVO ancVO) {
        Date observationDate = DateUtil.today().toDate();
        Date registrationDate = (RegistrationToday.TODAY.equals(ancVO.getRegistrationToday())) ? observationDate : ancVO.getRegistrationDate();
        HashSet<MRSObservation> observations = new HashSet<MRSObservation>();
        addObservation(observations, observationDate, CONCEPT_GRAVIDA, ancVO.getGravida());
        addObservation(observations, observationDate, CONCEPT_HEIGHT, ancVO.getHeight());
        addObservation(observations, observationDate, CONCEPT_PARITY, ancVO.getParity());
        addObservation(observations, registrationDate, CONCEPT_ANC_REG_NUM, ancVO.getSerialNumber());

        if (ancVO.getAddHistory()) {
            Set<MRSObservation> historyObservations = addObservationsOnANCHistory(ancVO.getAncCareHistoryVO());
            observations.addAll(historyObservations);
        }
        return observations;
    }

    private HashSet<MRSObservation> registerPregnancy(final ANCVO ancVO) {
        return new HashSet<MRSObservation>() {{
            final Date today = DateUtil.today().toDate();
            final MRSObservation observation = new MRSObservation(today, CONCEPT_PREGNANCY, null);
            addDependentObservation(observation, today, CONCEPT_EDD, ancVO.getEstimatedDateOfDelivery());
            addDependentObservation(observation, today, CONCEPT_CONFINEMENT_CONFIRMED, ancVO.getDeliveryDateConfirmed());
            observation.addDependantObservation(new MRSObservation<Boolean>(today, CONCEPT_PREGNANCY_STATUS, true));
            add(observation);
        }};
    }

    Set<MRSObservation> addObservationsOnANCHistory(ANCCareHistoryVO ancCareHistoryVO) {
        List<ANCCareHistory> capturedHistory = ancCareHistoryVO.getCareHistory();
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        addObservation(capturedHistory, ANCCareHistory.IPT, observations, ancCareHistoryVO.getLastIPTDate(), CONCEPT_IPT, safePareInteger(ancCareHistoryVO.getLastIPT()));
        addObservation(capturedHistory, ANCCareHistory.TT, observations, ancCareHistoryVO.getLastTTDate(), CONCEPT_TT, safePareInteger(ancCareHistoryVO.getLastTT()));
        return observations;
    }

    private Set<MRSObservation> prepareObservations(CwcVO cwc) {
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Set<MRSObservation> historyObservations = addObservationsOnCWCHistory(cwc.getCWCCareHistoryVO());
        mrsObservations.addAll(historyObservations);
        mrsObservations.add(new MRSObservation<String>(cwc.getRegistrationDate(), CONCEPT_CWC_REG_NUMBER, cwc.getSerialNumber()));
        return mrsObservations;
    }

    Set<MRSObservation> addObservationsOnCWCHistory(CWCCareHistoryVO cwcCareHistoryVO) {
        List<CwcCareHistory> capturedHistory = cwcCareHistoryVO.getCwcCareHistories();
        Set<MRSObservation> observations = new HashSet<MRSObservation>();

        if (capturedHistory != null && cwcCareHistoryVO.getAddCareHistory()) {
            addObservation(capturedHistory, CwcCareHistory.BCG, observations, cwcCareHistoryVO.getBcgDate(),
                    CONCEPT_IMMUNIZATIONS_ORDERED, new MRSConcept(CONCEPT_BCG));
            addObservation(capturedHistory, CwcCareHistory.VITA_A, observations, cwcCareHistoryVO.getVitADate(),
                    CONCEPT_IMMUNIZATIONS_ORDERED, new MRSConcept(CONCEPT_VITA));
            addObservation(capturedHistory, CwcCareHistory.MEASLES, observations, cwcCareHistoryVO.getMeaslesDate(),
                    CONCEPT_IMMUNIZATIONS_ORDERED, new MRSConcept(CONCEPT_MEASLES));
            addObservation(capturedHistory, CwcCareHistory.YF, observations, cwcCareHistoryVO.getYfDate(),
                    CONCEPT_IMMUNIZATIONS_ORDERED, new MRSConcept(CONCEPT_YF));
            addObservation(capturedHistory, CwcCareHistory.PENTA, observations, cwcCareHistoryVO.getLastPentaDate(), CONCEPT_PENTA, cwcCareHistoryVO.getLastPenta());
            addObservation(capturedHistory, CwcCareHistory.OPV, observations, cwcCareHistoryVO.getLastOPVDate(), CONCEPT_OPV, cwcCareHistoryVO.getLastOPV());
            addObservation(capturedHistory, CwcCareHistory.IPTI, observations, cwcCareHistoryVO.getLastIPTiDate(), CONCEPT_IPTI, cwcCareHistoryVO.getLastIPTi());
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
        return persistEncounter(patient, careHistory.getStaffId(), careHistory.getFacilityId(), ENCOUNTER_PATIENTHISTORY, careHistory.getDate(),
                addObservationsForCareHistory(careHistory));
    }
}
