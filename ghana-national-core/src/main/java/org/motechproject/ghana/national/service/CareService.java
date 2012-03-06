package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.vo.*;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.*;
import static org.motechproject.ghana.national.domain.RegistrationToday.TODAY;
import static org.motechproject.ghana.national.tools.Utility.safeParseInteger;
import static org.motechproject.util.DateUtil.*;

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
        enrollToCWCCarePrograms(cwc.getRegistrationDate(), patient);
    }

    void enrollToCWCCarePrograms(Date registrationDate, Patient patient) {
        List<PatientCare> patientCares = patient.cwcCareProgramToEnrollOnRegistration();
        for (PatientCare patientCare : patientCares) {
            allSchedules.enroll(new ScheduleEnrollmentMapper().map(patient, patientCare, newDate(registrationDate)));
        }
    }

    public void enroll(ANCVO ancVO) {
        Date registrationDate = (TODAY.equals(ancVO.getRegistrationToday())) ? now().toDate() : ancVO.getRegistrationDate();
        Patient patient = allPatients.getPatientByMotechId(ancVO.getPatientMotechId());
        LocalDate expectedDeliveryDate = newDate(ancVO.getEstimatedDateOfDelivery());

        allEncounters.persistEncounter(patient.getMrsPatient(), ancVO.getStaffId(), ancVO.getFacilityId(), ANC_REG_VISIT.value(), registrationDate, prepareObservations(ancVO));
        allEncounters.persistEncounter(patient.getMrsPatient(), ancVO.getStaffId(), ancVO.getFacilityId(), PREG_REG_VISIT.value(), registrationDate, registerPregnancy(ancVO, patient));
        enrollPatientCares(patient.ancCareProgramsToEnrollOnRegistration(expectedDeliveryDate), patient, newDate(registrationDate), null);
    }

    public void enrollMotherForPNC(Patient patient, DateTime deliveryDateTime) {
        enrollPatientCares(patient.pncMotherProgramsToEnrollOnRegistration(), patient, deliveryDateTime.toLocalDate(), time(deliveryDateTime));
    }

    void enrollPatientCares(List<PatientCare> patientCares, Patient patient, LocalDate registrationDate, Time time) {
        for (PatientCare patientCare : patientCares) {
            if(time != null) allSchedules.enroll(new ScheduleEnrollmentMapper().map(patient, patientCare, newDateTime(registrationDate, time)));
            else allSchedules.enroll(new ScheduleEnrollmentMapper().map(patient, patientCare, registrationDate));
        }
    }

    public void enrollChildForPNCOnDelivery(Patient child) {
        DateTime registrationDate = child.dateOfBirth();
        enrollPatientCares(child.pncBabyProgramsToEnrollOnRegistration(), child, registrationDate.toLocalDate(), time(registrationDate));
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
        Set<MRSObservation> eddObservation = allObservations.updateEDD(ancVO.getEstimatedDateOfDelivery(), patient, ancVO.getStaffId());

        MRSObservation activePregnancy;
        if (!eddObservation.isEmpty()) {
            activePregnancy = eddObservation.iterator().next();
        } else {
            activePregnancy = new MRSObservation<Object>(today, PREGNANCY.getName(), null);
            addDependentObservation(activePregnancy, today, EDD.getName(), ancVO.getEstimatedDateOfDelivery());
        }
        addDependentObservation(activePregnancy, today, CONFINEMENT_CONFIRMED.getName(), ancVO.getDeliveryDateConfirmed());
        addDependentObservation(activePregnancy, today, PREGNANCY_STATUS.getName(), true);
        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        mrsObservations.add(activePregnancy);
        return mrsObservations;
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
        return allEncounters.persistEncounter(patient.getMrsPatient(), careHistory.getStaffId(), careHistory.getFacilityId(), PATIENT_HISTORY.value(), careHistory.getDate(),
                addObservationsForCareHistory(careHistory));
    }
}
