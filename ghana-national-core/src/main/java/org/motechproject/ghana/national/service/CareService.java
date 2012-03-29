package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.vo.*;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.*;
import static org.motechproject.ghana.national.domain.RegistrationToday.TODAY;
import static org.motechproject.ghana.national.tools.Utility.safeParseInteger;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.now;

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
        List<MRSObservation> capturedHistory = allObservations.findObservations(patient.getMotechId(), Concept.IMMUNIZATIONS_ORDERED.getName());
        List<PatientCare> patientCares = patient.cwcCareProgramToEnrollOnRegistration(newDate(cwcVO.getRegistrationDate()),
                refineCwcCareHistories(capturedHistory, cwcVO.getCWCCareHistoryVO().getCwcCareHistories()));

        for (PatientCare patientCare : patientCares) {
            allSchedules.enroll(new ScheduleEnrollmentMapper().map(patient, patientCare));
        }
    }

    List<CwcCareHistory> refineCwcCareHistories(List<MRSObservation> capturedHistory, List<CwcCareHistory> cwcCareHistories) {

        cwcCareHistories = cwcCareHistories == null ? new ArrayList<CwcCareHistory>() : new ArrayList<CwcCareHistory>(cwcCareHistories);


        for (MRSObservation mrsObservation : capturedHistory) {
            if (mrsObservation.getValue() instanceof MRSConcept) {
                MRSConcept mrsConcept = (MRSConcept) mrsObservation.getValue();
                if (Concept.BCG.getName().equals(mrsConcept.getName()))
                    cwcCareHistories.add(CwcCareHistory.BCG);
                else if (Concept.YF.getName().equals(mrsConcept.getName()))
                    cwcCareHistories.add(CwcCareHistory.YF);
                else if (Concept.MEASLES.getName().equals(mrsConcept.getName()))
                    cwcCareHistories.add(CwcCareHistory.MEASLES);
            }
        }

        return cwcCareHistories;
    }

    public void enroll(ANCVO ancVO) {
        Date registrationDate = (TODAY.equals(ancVO.getRegistrationToday())) ? now().toDate() : ancVO.getRegistrationDate();
        Patient patient = allPatients.getPatientByMotechId(ancVO.getPatientMotechId());
        LocalDate expectedDeliveryDate = newDate(ancVO.getEstimatedDateOfDelivery());

        allEncounters.persistEncounter(patient.getMrsPatient(), ancVO.getStaffId(), ancVO.getFacilityId(), ANC_REG_VISIT.value(), registrationDate, prepareObservations(ancVO));
        allEncounters.persistEncounter(patient.getMrsPatient(), ancVO.getStaffId(), ancVO.getFacilityId(), PREG_REG_VISIT.value(), registrationDate, registerPregnancy(ancVO, patient));
        enrollPatientCares(patient.ancCareProgramsToEnrollOnRegistration(expectedDeliveryDate, newDate(registrationDate), ancVO.getAncCareHistoryVO(), activeCareSchedules(patient)), patient);
    }

    public ActiveCareSchedules activeCareSchedules(Patient patient) {
        return new ActiveCareSchedules().setActiveCareSchedule(TT_VACCINATION, allSchedules.getActiveEnrollment(patient.getMRSPatientId(), TT_VACCINATION));
    }

    public void enrollMotherForPNC(Patient patient, DateTime deliveryDateTime) {
        enrollPatientCares(patient.pncMotherProgramsToEnrollOnRegistration(deliveryDateTime), patient);
    }

    void enrollPatientCares(List<PatientCare> patientCares, Patient patient) {
        for (PatientCare patientCare : patientCares) {
            allSchedules.enroll(new ScheduleEnrollmentMapper().map(patient, patientCare));
        }
    }

    public void enrollChildForPNCOnDelivery(Patient child) {
        enrollPatientCares(child.pncBabyProgramsToEnrollOnRegistration(), child);
    }

    private Set<MRSObservation> prepareObservations(ANCVO ancVO) {
        Date observationDate = DateUtil.today().toDate();
        Date registrationDate = (TODAY.equals(ancVO.getRegistrationToday())) ? observationDate : ancVO.getRegistrationDate();
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

    private Set<MRSObservation> registerPregnancy(ANCVO ancVO, Patient patient) {
        Date today = DateUtil.today().toDate();
        Set<MRSObservation> activePregnancyObservation = allObservations.updateEDD(ancVO.getEstimatedDateOfDelivery(), patient, ancVO.getStaffId());
        MRSObservation activePregnancy;
        if (!activePregnancyObservation.isEmpty()) {
            activePregnancy = activePregnancyObservation.iterator().next();

        } else {
            activePregnancy = new MRSObservation<Object>(today, PREGNANCY.getName(), null);
            addDependentObservation(activePregnancy, today, EDD.getName(), ancVO.getEstimatedDateOfDelivery());
        }

        addDependentObservation(activePregnancy, today, CONFINEMENT_CONFIRMED.getName(), ancVO.getDeliveryDateConfirmed());
        addDependentObservation(activePregnancy, today, PREGNANCY_STATUS.getName(), true);

        if (ancVO.getAddHistory()) {
            ANCCareHistoryVO ancCareHistoryVO = ancVO.getAncCareHistoryVO();
            addObservationIfWithinPregnancyPeriod(activePregnancy, IPT, ancCareHistoryVO.getLastIPTDate(), safeParseInteger(ancCareHistoryVO.getLastIPT()));
            addObservationIfWithinPregnancyPeriod(activePregnancy, TT, ancCareHistoryVO.getLastTTDate(), safeParseInteger(ancCareHistoryVO.getLastTT()));
        }
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        mrsObservations.add(activePregnancy);
        return mrsObservations;
    }

    private void addObservationIfWithinPregnancyPeriod(MRSObservation activePregnancy, Concept concept, Date observationDate, Integer observationValue) {
        Date edd = null;
        Set<MRSObservation> dependantObservations = activePregnancy.getDependantObservations();
        for (MRSObservation depObs : dependantObservations) {
            if (EDD.getName().equals(depObs.getConceptName())) {
                edd = depObs.getDate();
            }
        }
        LocalDate dateOfConception = Pregnancy.basedOnDeliveryDate(DateUtil.newDate(edd)).dateOfConception();

        if (observationDate != null && observationDate.after(dateOfConception.toDate())) {
            addDependentObservation(activePregnancy, observationDate, concept.getName(), observationValue);
        }
    }

    Set<MRSObservation> addObservationsOnANCHistory(ANCCareHistoryVO ancCareHistoryVO) {
        List<ANCCareHistory> capturedHistory = ancCareHistoryVO.getCareHistory();
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        addObservation(capturedHistory, ANCCareHistory.IPT, observations, ancCareHistoryVO.getLastIPTDate(), IPT.getName(), safeParseInteger(ancCareHistoryVO.getLastIPT()));
        addObservation(capturedHistory, ANCCareHistory.TT, observations, ancCareHistoryVO.getLastTTDate(), TT.getName(), safeParseInteger(ancCareHistoryVO.getLastTT()));
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

    public MRSEncounter addCareHistory(CareHistoryVO careHistory) {
        Patient patient = allPatients.getPatientByMotechId(careHistory.getPatientMotechId());
        ANCCareHistoryVO ancCareHistoryVO = careHistory.getAncCareHistoryVO();

        final MRSObservation activePregnancyObservation = allObservations.findObservation(patient.getMotechId(), PREGNANCY.getName());
        if (activePregnancyObservation != null) {
            allObservations.voidObservation(activePregnancyObservation,"Updated in " + ANC_VISIT.value() + " encounter", careHistory.getStaffId());

            addObservationIfWithinPregnancyPeriod(activePregnancyObservation, TT, ancCareHistoryVO.getLastTTDate(), safeParseInteger(ancCareHistoryVO.getLastTT()));
            addObservationIfWithinPregnancyPeriod(activePregnancyObservation, IPT, ancCareHistoryVO.getLastIPTDate(), safeParseInteger(ancCareHistoryVO.getLastIPT()));
            allEncounters.persistEncounter(patient.getMrsPatient(), careHistory.getStaffId(), careHistory.getFacilityId(), ANC_VISIT.value(), careHistory.getDate(), new HashSet<MRSObservation>() {{
                add(activePregnancyObservation);
            }});

        }
        return allEncounters.persistEncounter(patient.getMrsPatient(), careHistory.getStaffId(), careHistory.getFacilityId(), PATIENT_HISTORY.value(), careHistory.getDate(),
                addObservationsForCareHistory(careHistory));
    }
}
