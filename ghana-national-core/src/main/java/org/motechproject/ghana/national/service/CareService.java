package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.domain.care.*;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.vo.*;
import org.motechproject.model.Time;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.*;
import static org.motechproject.ghana.national.tools.Utility.*;
import static org.motechproject.util.DateUtil.newDate;

@Service
public class CareService {
    AllPatients allPatients;
    AllEncounters allEncounters;
    AllObservations allObservations;
    AllSchedules allSchedules;

    @Autowired
    public CareService(AllPatients allPatients, AllEncounters allEncounters, AllObservations allObservations, AllSchedules allSchedules) {
        this.allPatients = allPatients;
        this.allEncounters = allEncounters;
        this.allObservations = allObservations;
        this.allSchedules = allSchedules;
    }

    public void enroll(CwcVO cwc) {
        Patient patient = allPatients.getPatientByMotechId(cwc.getPatientMotechId());
        allEncounters.persistEncounter(patient.getMrsPatient(), cwc.getStaffId(), cwc.getFacilityId(), CWC_REG_VISIT.value(), cwc.getRegistrationDate(),
                prepareObservations(cwc));
        enrollToCWCCarePrograms(cwc, patient);

    }

    void enrollToCWCCarePrograms(CwcVO cwcVO, Patient patient) {
        List<MRSObservation> existingHistories = allObservations.findObservations(patient.getMotechId(), Concept.IMMUNIZATIONS_ORDERED.getName());
        existingHistories.addAll(allObservations.findObservations(patient.getMotechId(), Concept.PENTA.getName()));
        existingHistories.addAll(allObservations.findObservations(patient.getMotechId(), Concept.OPV.getName()));
        existingHistories.addAll(allObservations.findObservations(patient.getMotechId(), Concept.IPTI.getName()));
        final List<CwcCareHistory> mergedHistories = mergeNewHistoriesWithExisting(existingHistories, cwcVO.getCWCCareHistoryVO().getCwcCareHistories());

        Date lastPentaDate = getLastPentaDate(patient, cwcVO.getCWCCareHistoryVO());
        Date lastIPTiDate = getLastIPTiDate(patient, cwcVO.getCWCCareHistoryVO());
        List<PatientCare> patientCares = patient.cwcCareProgramToEnrollOnRegistration(newDate(cwcVO.getRegistrationDate()),
                mergedHistories, cwcVO.getCWCCareHistoryVO(), activeCareSchedules(patient, Arrays.asList(CWC_PENTA, CWC_IPT_VACCINE, CWC_OPV_OTHERS)), lastPentaDate,lastIPTiDate);

        for (PatientCare patientCare : patientCares) {
            allSchedules.enroll(new ScheduleEnrollmentMapper().map(patient, patientCare));
        }
        enrollChildForPNC(patient);
    }

    private Date getLastIPTiDate(Patient patient, CWCCareHistoryVO cwcVO) {
        Date lastIPTiDate = cwcVO.getLastPentaDate();
        IPTiDose nextMilestone = cwcVO.getLastIPTi()!=null ? getNextOf(IPTiDose.byValue(cwcVO.getLastIPTi())) : null;
        if(lastIPTiDate!=null && nextMilestone!=null)
            lastIPTiDate=getEnrollmentDateForChildCareSchedules(ScheduleNames.CWC_IPT_VACCINE,lastIPTiDate,nextMilestone.milestoneName(),patient.dateOfBirth().toLocalDate());
        return lastIPTiDate;
    }

    private Date getLastPentaDate(Patient patient, CWCCareHistoryVO cwcVO) {
        Date lastPentaDate = cwcVO.getLastPentaDate();
        PentaDose nextMilestone = cwcVO.getLastPenta()!=null ? getNextOf(PentaDose.byValue(cwcVO.getLastPenta())) : null;
        if(lastPentaDate!=null && nextMilestone!=null)
            lastPentaDate=getEnrollmentDateForChildCareSchedules(ScheduleNames.CWC_PENTA,lastPentaDate,nextMilestone.milestoneName(),patient.dateOfBirth().toLocalDate());
        return lastPentaDate;
    }

    List<CwcCareHistory> mergeNewHistoriesWithExisting(List<MRSObservation> existingHistory, List<CwcCareHistory> newHistory) {
        newHistory = (newHistory == null) ? new ArrayList<CwcCareHistory>() : new ArrayList<CwcCareHistory>(newHistory);

        final HashMap<String, CwcCareHistory> conceptToCareHistory = new HashMap<String, CwcCareHistory>() {{
            put(Concept.BCG.getName(), CwcCareHistory.BCG);
            put(Concept.YF.getName(), CwcCareHistory.YF);
            put(Concept.MEASLES.getName(), CwcCareHistory.MEASLES);
            put(Concept.VITA.getName(), CwcCareHistory.VITA_A);
            put(Concept.PENTA.getName(), CwcCareHistory.PENTA);
            put(Concept.IPTI.getName(), CwcCareHistory.IPTI);
            put(Concept.OPV.getName(), CwcCareHistory.OPV);
        }};

        for (MRSObservation mrsObservation : existingHistory) {
            String conceptName = null;
            if (mrsObservation.getValue() instanceof MRSConcept) {
                conceptName = ((MRSConcept) mrsObservation.getValue()).getName();
            } else {
                conceptName = mrsObservation.getConceptName();
            }
            if (conceptToCareHistory.containsKey(conceptName)) {
                newHistory.add(conceptToCareHistory.get(conceptName));
            }
        }

        return newHistory;
    }

    public void enroll(ANCVO ancVO) throws ObservationNotFoundException {
        final ANCCareHistoryVO ancCareHistoryVO = ancVO.getAncCareHistoryVO();
        Patient patient = allPatients.getPatientByMotechId(ancVO.getPatientMotechId());
        LocalDate expectedDeliveryDate = newDate(ancVO.getEstimatedDateOfDelivery());

        Set<MRSObservation> pregnancyObservations = registerPregnancy(ancVO, patient);
        allEncounters.persistEncounter(patient.getMrsPatient(), ancVO.getStaffId(), ancVO.getFacilityId(), ANC_REG_VISIT.value(), ancVO.getRegistrationDate(), prepareObservations(ancVO));
        allEncounters.persistEncounter(patient.getMrsPatient(), ancVO.getStaffId(), ancVO.getFacilityId(), PREG_REG_VISIT.value(), ancVO.getRegistrationDate(), pregnancyObservations);


        ActiveCareSchedules activeCareSchedules = activeCareSchedules(patient, Arrays.asList(TT_VACCINATION, ANC_IPT_VACCINE));

        Date lastTTDate = getLastTTDate(ancCareHistoryVO,expectedDeliveryDate);
        TTVaccineCare ttVaccineCare = new TTVaccineCare(patient, expectedDeliveryDate, newDate(ancVO.getRegistrationDate()),activeCareSchedules.hasActiveTTSchedule(), ancCareHistoryVO.getLastTT(), lastTTDate);

        Date lastIPTDate = getLastIPTDate(ancCareHistoryVO,expectedDeliveryDate);
        IPTVaccineCare iptVaccineCare = new IPTVaccineCare(patient, expectedDeliveryDate, activeCareSchedules.hasActiveIPTSchedule(), ancCareHistoryVO.getLastIPT(), lastIPTDate);

        List<PatientCare> patientCares = new ANCCareRegistration(ttVaccineCare, iptVaccineCare, patient, expectedDeliveryDate).allCares();
        enrollPatientCares(patientCares, patient);
    }

    private Date getLastIPTDate(ANCCareHistoryVO ancCareHistoryVO,LocalDate edd) {
        IPTDose nextIPTMilestone = ancCareHistoryVO.getLastIPT()!=null ? getNextOf(IPTDose.byValue(ancCareHistoryVO.getLastIPT())) : null;
        Date lastIPTDate = ancCareHistoryVO.getLastIPTDate();
        if(lastIPTDate!=null && nextIPTMilestone!=null) {
            lastIPTDate = getEnrollmentDateForPregnancySchedules(ANC_IPT_VACCINE, DateUtil.newDate(lastIPTDate), nextIPTMilestone.milestone(), edd).toDate();
        }
        return lastIPTDate;
    }

    private Date getLastTTDate(ANCCareHistoryVO ancCareHistoryVO,LocalDate edd) {
        Date lastTTDate = ancCareHistoryVO.getLastTTDate();
        TTVaccineDosage nextTTMilestone = (ancCareHistoryVO.getLastTT()!=null) ? getNextOf(TTVaccineDosage.byValue(Integer.parseInt(ancCareHistoryVO.getLastTT()))) : null;
        if(lastTTDate!=null && nextTTMilestone!=null) {
            lastTTDate= getEnrollmentDateForPregnancySchedules(TT_VACCINATION, DateUtil.newDate(lastTTDate), nextTTMilestone.getScheduleMilestoneName(), edd).toDate();
        }
        return lastTTDate;
    }

    ActiveCareSchedules activeCareSchedules(Patient patient, List<String> scheduleNames) {
        ActiveCareSchedules activeCareSchedules = new ActiveCareSchedules();
        for (String scheduleName : scheduleNames)
            activeCareSchedules.setActiveCareSchedule(scheduleName, allSchedules.getActiveEnrollment(patient.getMRSPatientId(), scheduleName));
        return activeCareSchedules;
    }

    public void enrollMotherForPNC(Patient patient, DateTime deliveryDateTime) {
        enrollPatientCares(patient.pncMotherProgramsToEnrollOnRegistration(deliveryDateTime), patient);
    }

    void enrollPatientCares(List<PatientCare> patientCares, Patient patient) {
        for (PatientCare patientCare : patientCares) {
            allSchedules.enroll(new ScheduleEnrollmentMapper().map(patient, patientCare));
        }
    }

    public void enrollChildForPNC(Patient child) {
        enrollPatientCares(child.pncBabyProgramsToEnrollOnRegistration(), child);
    }

    private Set<MRSObservation> prepareObservations(ANCVO ancVO) {
        Date registrationDate = ancVO.getRegistrationDate();
        HashSet<MRSObservation> observations = new HashSet<MRSObservation>();
        addObservation(observations, registrationDate, GRAVIDA.getName(), ancVO.getGravida());
        addObservation(observations, registrationDate, HEIGHT.getName(), ancVO.getHeight());
        addObservation(observations, registrationDate, PARITY.getName(), ancVO.getParity());
        addObservation(observations, registrationDate, SERIAL_NUMBER.getName(), ancVO.getSerialNumber());

        if (ancVO.getAddHistory()) {
            Set<MRSObservation> historyObservations = addObservationsOnANCHistory(ancVO.getAncCareHistoryVO());
            observations.addAll(historyObservations);
        }
        return observations;
    }

    Set<MRSObservation> registerPregnancy(ANCVO ancVO, Patient patient) throws ObservationNotFoundException {
        Set<MRSObservation> activePregnancyObservation = allObservations.updateEDD(ancVO.getEstimatedDateOfDelivery(), patient, ancVO.getStaffId(), ancVO.getRegistrationDate());
        MRSObservation activePregnancy;
        if (!activePregnancyObservation.isEmpty()) {
            activePregnancy = activePregnancyObservation.iterator().next();
        } else {
            activePregnancy = new MRSObservation<Object>(ancVO.getRegistrationDate(), PREGNANCY.getName(), null);
            addDependentObservation(activePregnancy, ancVO.getRegistrationDate(), EDD.getName(), ancVO.getEstimatedDateOfDelivery());
        }

        addDependentObservation(activePregnancy, ancVO.getRegistrationDate(), CONFINEMENT_CONFIRMED.getName(), ancVO.getDeliveryDateConfirmed());
        addDependentObservation(activePregnancy, ancVO.getRegistrationDate(), PREGNANCY_STATUS.getName(), true);

        if (ancVO.getAddHistory()) {
            ANCCareHistoryVO ancCareHistoryVO = ancVO.getAncCareHistoryVO();
            Date edd = getEDD(ancVO.getPatientMotechId());
            addObservationIfWithinPregnancyPeriod(activePregnancy, IPT, ancCareHistoryVO.getLastIPTDate(), safeParseDouble(ancCareHistoryVO.getLastIPT()), edd);
            addObservationIfWithinPregnancyPeriod(activePregnancy, TT, ancCareHistoryVO.getLastTTDate(), safeParseDouble(ancCareHistoryVO.getLastTT()), edd);
        }
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        mrsObservations.add(activePregnancy);
        return mrsObservations;
    }

    private void addObservationIfWithinPregnancyPeriod(MRSObservation activePregnancy, Concept concept, Date observationDate, Double observationValue, Date edd) {

        if (isWithinCurrentPregnancyPeriod(observationDate, edd)) {
            addDependentObservation(activePregnancy, observationDate, concept.getName(), observationValue);
        }
    }

    private boolean isWithinCurrentPregnancyPeriod(Date observationDate, Date edd) {
        if (edd != null) {
            LocalDate dateOfConception = Pregnancy.basedOnDeliveryDate(DateUtil.newDate(edd)).dateOfConception();
            LocalDate eddMaxDate = DateUtil.newDate(edd).plusWeeks(4);
            return observationDate != null && observationDate.after(dateOfConception.toDate()) && observationDate.before(eddMaxDate.toDate());
        }
        return false;
    }

    Set<MRSObservation> addObservationsOnANCHistory(ANCCareHistoryVO ancCareHistoryVO) {
        List<ANCCareHistory> capturedHistory = ancCareHistoryVO.getCareHistory();
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        addObservation(capturedHistory, ANCCareHistory.IPT_SP, observations, ancCareHistoryVO.getLastIPTDate(), IPT.getName(), safeParseDouble(ancCareHistoryVO.getLastIPT()));
        addObservation(capturedHistory, ANCCareHistory.TT, observations, ancCareHistoryVO.getLastTTDate(), TT.getName(), safeParseDouble(ancCareHistoryVO.getLastTT()));
        return observations;
    }

    private Set<MRSObservation> prepareObservations(CwcVO cwc) {
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Set<MRSObservation> historyObservations = addObservationsOnCWCHistory(cwc.getCWCCareHistoryVO());
        mrsObservations.addAll(historyObservations);
        mrsObservations.add(new MRSObservation<String>(cwc.getRegistrationDate(), SERIAL_NUMBER.getName(), cwc.getSerialNumber()));
        return mrsObservations;
    }

    Set<MRSObservation> addObservationsOnCWCHistory(CWCCareHistoryVO cwcCareHistoryVO) {
        List<CwcCareHistory> capturedHistory = cwcCareHistoryVO.getCwcCareHistories();
        Set<MRSObservation> observations = new HashSet<MRSObservation>();

        if (capturedHistory != null && cwcCareHistoryVO.getAddCareHistory()) {
            addObservation(capturedHistory, CwcCareHistory.BCG, observations, cwcCareHistoryVO.getBcgDate(), IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(BCG.getName()));
            addObservation(capturedHistory, CwcCareHistory.VITA_A, observations, cwcCareHistoryVO.getVitADate(), IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(VITA.getName()));
            addObservation(capturedHistory, CwcCareHistory.MEASLES, observations, cwcCareHistoryVO.getMeaslesDate(), IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(MEASLES.getName()));
            addObservation(capturedHistory, CwcCareHistory.YF, observations, cwcCareHistoryVO.getYfDate(), IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(YF.getName()));
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

    public MRSEncounter addCareHistory(CareHistoryVO careHistoryVO) {
        Patient patient = allPatients.getPatientByMotechId(careHistoryVO.getPatientMotechId());
        processANCHistories(careHistoryVO, patient);
        processCWCHistories(careHistoryVO, patient);
        return allEncounters.persistEncounter(patient.getMrsPatient(), careHistoryVO.getStaffId(), careHistoryVO.getFacilityId(), PATIENT_HISTORY.value(), careHistoryVO.getDate(),
                addObservationsForCareHistory(careHistoryVO));
    }

    private void processCWCHistories(CareHistoryVO careHistoryVO, Patient patient) {
        CWCCareHistoryVO cwcCareHistoryVO = careHistoryVO.getCwcCareHistoryVO();
        String opvVaccine = CWC_OPV_0;
        if(cwcCareHistoryVO.getLastOPV()!=null)
            opvVaccine = (cwcCareHistoryVO.getLastOPV() == 0) ? CWC_OPV_0 : CWC_OPV_OTHERS;
        ActiveCareSchedules activeCareSchedules = activeCareSchedules(patient, Arrays.asList(CWC_PENTA,CWC_IPT_VACCINE,opvVaccine));

        PentaVaccineCare pentaVaccineCare = new PentaVaccineCare(patient, newDate(careHistoryVO.getDate()), activeCareSchedules.hasActivePentaSchedule(), safeToString(cwcCareHistoryVO.getLastPenta()), cwcCareHistoryVO.getLastPentaDate());
        IPTiVaccineCare iptiVaccineCare = new IPTiVaccineCare(patient, newDate(careHistoryVO.getDate()), activeCareSchedules.hasActiveIPTiSchedule(), safeToString(cwcCareHistoryVO.getLastIPTi()), cwcCareHistoryVO.getLastIPTiDate());
        OPVVaccineCare opvVaccineCare = new OPVVaccineCare(patient, newDate(careHistoryVO.getDate()), activeCareSchedules.hasActiveOPVSchedule(), safeToString(cwcCareHistoryVO.getLastOPV()), cwcCareHistoryVO.getLastOPVDate(),opvVaccine);
        enrollPatientCares(CareHistory.forChildCare(pentaVaccineCare,iptiVaccineCare,opvVaccineCare).cares(), patient);
    }


    private void processANCHistories(CareHistoryVO careHistoryVO, Patient patient) {
        ANCCareHistoryVO ancCareHistoryVO = careHistoryVO.getAncCareHistoryVO();

        final MRSObservation activePregnancyObservation = allObservations.findLatestObservation(patient.getMotechId(), PREGNANCY.getName());

        if (activePregnancyObservation != null) {
            Date edd = getEDD(patient.getMotechId());
            LocalDate expectedDeliveryDate = newDate(edd);

            ActiveCareSchedules activeCareSchedules = activeCareSchedules(patient, Arrays.asList(TT_VACCINATION, ANC_IPT_VACCINE));
            Date lastTTDate = getLastTTDate(ancCareHistoryVO,expectedDeliveryDate);
            TTVaccineCare ttVaccineCare = new TTVaccineCare(patient, expectedDeliveryDate, newDate(careHistoryVO.getDate()), activeCareSchedules.hasActiveTTSchedule(), ancCareHistoryVO.getLastTT(), lastTTDate);

            Date lastIPTDate = getLastIPTDate(ancCareHistoryVO,expectedDeliveryDate);
            IPTVaccineCare iptVaccineCare = new IPTVaccineCare(patient, expectedDeliveryDate, activeCareSchedules.hasActiveIPTSchedule(), ancCareHistoryVO.getLastIPT(), lastIPTDate);

            final List<PatientCare> caresApplicableToTheCurrentPregnancy = CareHistory.forPregnancy(ttVaccineCare, iptVaccineCare).cares();

            if (caresApplicableToTheCurrentPregnancy.size() > 0) {
                allObservations.voidObservation(activePregnancyObservation, "Updated in " + ANC_VISIT.value() + " encounter", careHistoryVO.getStaffId());

                addObservationIfWithinPregnancyPeriod(activePregnancyObservation, TT, ancCareHistoryVO.getLastTTDate(), safeParseDouble(ancCareHistoryVO.getLastTT()), edd);
                addObservationIfWithinPregnancyPeriod(activePregnancyObservation, IPT, ancCareHistoryVO.getLastIPTDate(), safeParseDouble(ancCareHistoryVO.getLastIPT()), edd);
                allEncounters.persistEncounter(patient.getMrsPatient(), careHistoryVO.getStaffId(), careHistoryVO.getFacilityId(), ANC_VISIT.value(), careHistoryVO.getDate(), new HashSet<MRSObservation>() {{
                    add(activePregnancyObservation);
                }});
            }

            enrollPatientCares(caresApplicableToTheCurrentPregnancy, patient);
        }
    }

    public LocalDate getEnrollmentDateForPregnancySchedules(String scheduleName, LocalDate lastCareTakenDate, String startMilestoneName, LocalDate edd) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(null, scheduleName, null, null, null, lastCareTakenDate, new Time(0,0), startMilestoneName, null);
        List<DateTime> dueWindowAlertTimings = allSchedules.getDueWindowAlertTimings(enrollmentRequest);
        Pregnancy pregnancy = Pregnancy.basedOnDeliveryDate(edd);
        if (!dueWindowAlertTimings.isEmpty() && dueWindowAlertTimings.get(0).isBeforeNow() && lastCareTakenDate.toDate().after(pregnancy.dateOfConception().toDate()))
            return getDifferenceOfDates(dueWindowAlertTimings.get(0), DateUtil.newDateTime(lastCareTakenDate)).toLocalDate();
        return lastCareTakenDate;
    }
    
    public Date getEnrollmentDateForChildCareSchedules(String scheduleName, Date lastCareTakenDate, String startMilestoneName, LocalDate dateOfBirth) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(null, scheduleName, null, null, null, newDate(lastCareTakenDate), new Time(0,0), startMilestoneName, null);
        List<DateTime> dueWindowAlertTimings = allSchedules.getDueWindowAlertTimings(enrollmentRequest);
        if (!dueWindowAlertTimings.isEmpty() && dueWindowAlertTimings.get(0).isBeforeNow() && lastCareTakenDate.after(dateOfBirth.toDate()))
            return getDifferenceOfDates(dueWindowAlertTimings.get(0), DateUtil.newDateTime(lastCareTakenDate)).toDate();
        return lastCareTakenDate;
    }

    private DateTime getDifferenceOfDates(DateTime dueDate, DateTime lastCareTakenDate) {
        Period period = new Period(lastCareTakenDate,dueDate);
        return DateUtil.now().minus(period);
    }


    Date getEDD(String motechId) {
        MRSObservation eddObservation = allObservations.findObservation(motechId, EDD.getName());
        return eddObservation != null ? (Date) eddObservation.getValue() : null;
    }
}
