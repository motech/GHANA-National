package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.vo.*;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.services.OpenMRSConceptAdapter;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.openmrs.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CareService {
    @Autowired
    StaffService staffService;

    @Autowired
    PatientService patientService;

    @Autowired
    OpenMRSConceptAdapter openMRSConceptAdapter;

    @Autowired
    AllEncounters allEncounters;

    @Autowired
    ScheduleTrackingService scheduleTrackingService;

    public MRSEncounter getEncounter(String motechId, String encounterType) {
        return allEncounters.fetchLatest(motechId, encounterType);
    }

    public void enroll(CwcVO cwc) {
        Patient patient = patientService.getPatientByMotechId(cwc.getPatientMotechId());
        persistEncounter(patient, cwc.getStaffId(), cwc.getFacilityId(), Constants.ENCOUNTER_CWCREGVISIT, cwc.getRegistrationDate(),
                prepareObservations(cwc));
    }

    public void enroll(ANCVO ancVO) {
        Date registrationDate = (RegistrationToday.TODAY.equals(ancVO.getRegistrationToday())) ? DateUtil.now().toDate() : ancVO.getRegistrationDate();
        Patient patient = patientService.getPatientByMotechId(ancVO.getPatientMotechId());
        persistEncounter(patient, ancVO.getStaffId(), ancVO.getFacilityId(),
                Constants.ENCOUNTER_ANCREGVISIT, registrationDate, prepareObservations(ancVO));
        persistEncounter(patient, ancVO.getStaffId(), ancVO.getFacilityId(),
                Constants.ENCOUNTER_PREGREGVISIT, registrationDate, registerPregnancy(ancVO));

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
        Date observationDate = new Date();
        Date registrationDate = (RegistrationToday.TODAY.equals(ancVO.getRegistrationToday())) ? observationDate : ancVO.getRegistrationDate();
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        if (ancVO.getGravida() != null)
            mrsObservations.add(new MRSObservation<Integer>(observationDate, Constants.CONCEPT_GRAVIDA, ancVO.getGravida()));
        if (ancVO.getHeight() != null)
            mrsObservations.add(new MRSObservation<Double>(observationDate, Constants.CONCEPT_HEIGHT, ancVO.getHeight()));
        if (ancVO.getParity() != null)
            mrsObservations.add(new MRSObservation<Integer>(observationDate, Constants.CONCEPT_PARITY, ancVO.getParity()));
        if (ancVO.getSerialNumber() != null)
            mrsObservations.add(new MRSObservation<String>(registrationDate, Constants.CONCEPT_ANC_REG_NUM, ancVO.getSerialNumber()));
        if (ancVO.getAddHistory()) {
            Set<MRSObservation> historyObservations = addObservationsOnANCHistory(ancVO.getAncCareHistoryVO());
            mrsObservations.addAll(historyObservations);
        }
        return mrsObservations;
    }

    private HashSet<MRSObservation> registerPregnancy(ANCVO ancVO) {
        final Date date = new Date();
        final MRSObservation observation = new MRSObservation(date, Constants.CONCEPT_PREGNANCY, null);
        if (ancVO.getEstimatedDateOfDelivery() != null) {
            observation.addDependantObservation(new MRSObservation<Date>(date, Constants.CONCEPT_EDD, ancVO.getEstimatedDateOfDelivery()));
        }
        if (ancVO.getDeliveryDateConfirmed() != null) {
            observation.addDependantObservation(new MRSObservation<Boolean>(date, Constants.CONCEPT_CONFINEMENT_CONFIRMED, ancVO.getDeliveryDateConfirmed()));
        }
        observation.addDependantObservation(new MRSObservation<Boolean>(date, Constants.CONCEPT_PREGNANCY_STATUS, true));
        return new HashSet<MRSObservation>() {{
            add(observation);
        }};
    }

    Set<MRSObservation> addObservationsOnANCHistory(ANCCareHistoryVO ancCareHistoryVO) {
        List<ANCCareHistory> careHistories = ancCareHistoryVO.getCareHistory();
        Set<MRSObservation> historyObservations = new HashSet<MRSObservation>();
        if (careHistories != null && ancCareHistoryVO.getAddCareHistory()) {
            if (careHistories.contains(ANCCareHistory.IPT)) {
                historyObservations.add(new MRSObservation<Integer>(ancCareHistoryVO.getLastIPTDate(), Constants.CONCEPT_IPT,
                        Integer.parseInt(ancCareHistoryVO.getLastIPT())));
            }
            if (careHistories.contains(ANCCareHistory.TT)) {
                historyObservations.add(new MRSObservation<Integer>(ancCareHistoryVO.getLastTTDate(), Constants.CONCEPT_TT,
                        Integer.parseInt(ancCareHistoryVO.getLastTT())));
            }
        }
        return historyObservations;
    }

    private Set<MRSObservation> prepareObservations(CwcVO cwc) {
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Set<MRSObservation> historyObservations = addObservationsOnCWCHistory(cwc.getCWCCareHistoryVO());
        mrsObservations.addAll(historyObservations);
        mrsObservations.add(new MRSObservation<String>(cwc.getRegistrationDate(), Constants.CONCEPT_CWC_REG_NUMBER, cwc.getSerialNumber()));
        return mrsObservations;
    }

    Set<MRSObservation> addObservationsOnCWCHistory(CWCCareHistoryVO cwcCareHistoryVO) {
        List<CwcCareHistory> cwcCareHistory = cwcCareHistoryVO.getCwcCareHistories();
        Set<MRSObservation> historyObservations = new HashSet<MRSObservation>();
        if (cwcCareHistory != null && cwcCareHistoryVO.getAddCareHistory()) {
            if (cwcCareHistory.contains(CwcCareHistory.BCG)) {
                historyObservations.add(new MRSObservation<Concept>(cwcCareHistoryVO.getBcgDate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED,
                        openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_BCG)));
            }
            if (cwcCareHistory.contains(CwcCareHistory.VITA_A)) {
                historyObservations.add(new MRSObservation<Concept>(cwcCareHistoryVO.getVitADate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED,
                        openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_VITA)));
            }
            if (cwcCareHistory.contains(CwcCareHistory.MEASLES)) {
                historyObservations.add(new MRSObservation<Concept>(cwcCareHistoryVO.getMeaslesDate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED,
                        openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_MEASLES)));
            }
            if (cwcCareHistory.contains(CwcCareHistory.YF)) {
                historyObservations.add(new MRSObservation<Concept>(cwcCareHistoryVO.getYfDate(), Constants.CONCEPT_IMMUNIZATIONS_ORDERED,
                        openMRSConceptAdapter.getConceptByName(Constants.CONCEPT_YF)));
            }
            if (cwcCareHistory.contains(CwcCareHistory.PENTA)) {
                historyObservations.add(new MRSObservation<Integer>(cwcCareHistoryVO.getLastPentaDate(), Constants.CONCEPT_PENTA, cwcCareHistoryVO.getLastPenta()));
            }
            if (cwcCareHistory.contains(CwcCareHistory.OPV)) {
                historyObservations.add(new MRSObservation<Integer>(cwcCareHistoryVO.getLastOPVDate(), Constants.CONCEPT_OPV, cwcCareHistoryVO.getLastOPV()));
            }
            if (cwcCareHistory.contains(CwcCareHistory.IPTI)) {
                historyObservations.add(new MRSObservation<Integer>(cwcCareHistoryVO.getLastIPTiDate(), Constants.CONCEPT_IPTI, cwcCareHistoryVO.getLastIPTi()));
            }
        }
        return historyObservations;
    }

    private Set<MRSObservation> addObservationsForCareHistory(final CareHistoryVO careHistoryVO) {
        return new HashSet<MRSObservation>() {{
            addAll(addObservationsOnANCHistory(careHistoryVO.getAncCareHistoryVO()));
            addAll(addObservationsOnCWCHistory(careHistoryVO.getCwcCareHistoryVO()));
        }};
    }

    public MRSEncounter addCareHistory(CareHistoryVO careHistory) {
        Patient patient = patientService.getPatientByMotechId(careHistory.getPatientMotechId());
        return persistEncounter(patient, careHistory.getStaffId(), careHistory.getFacilityId(), Constants.ENCOUNTER_PATIENTHISTORY, careHistory.getDate(),
                addObservationsForCareHistory(careHistory));
    }
}
