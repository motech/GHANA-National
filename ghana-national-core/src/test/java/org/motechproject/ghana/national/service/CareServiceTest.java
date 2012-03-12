package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.vo.*;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_MOTHER_1;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.*;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.time;

public class CareServiceTest extends BaseUnitTest {
    CareService careService;

    @Mock
    AllPatients mockAllPatients;

    @Mock
    AllEncounters mockAllEncounters;

    @Mock
    AllSchedules mockAllSchedules;

    @Mock
    AllObservations mockAllObservations;

    private DateTime currentDate;
    Patient mockPatient;
    MRSPatient mockMRSPatient;

    @Before
    public void setUp() {
        initMocks(this);
        careService = new CareService(mockAllPatients, mockAllEncounters, mockAllObservations, mockAllSchedules);

        currentDate = DateTime.now();
        mockCurrentDate(currentDate);
    }

    @Test
    public void shouldEnrollToCWCProgram() {
        final String staffId = "456";
        final String patientId = "24324";
        final String patientMotechId = "1234567";
        final String facilityId = "3232";

        final Date registrationDate = new Date(2011, 9, 1);
        final Date lastBCGDate = new Date(2011, 10, 1);
        final Date lastVitADate = new Date(2011, 11, 1);
        final Date lastMeaslesDate = new Date(2011, 9, 2);
        final Date lastYfDate = new Date(2011, 9, 3);
        final Date lastPentaDate = new Date(2011, 9, 4);
        final Date lastOPVDate = new Date(2011, 9, 5);
        final Date lastIPTiDate = new Date(2011, 9, 6);
        final int lastIPTi = 1;
        final int lastPenta = 1;
        final int lastOPV = 0;
        final String serialNumber = "wewew";
        CwcVO cwcVO = new CwcVO(staffId, facilityId, registrationDate, patientMotechId, Arrays.asList(CwcCareHistory.values()), lastBCGDate, lastVitADate,
                lastMeaslesDate, lastYfDate, lastPentaDate, lastPenta, lastOPVDate, lastOPV, lastIPTiDate, lastIPTi, serialNumber, true);

        setupPatient(patientId, patientMotechId);

        final HashSet<MRSObservation> expected = new HashSet<MRSObservation>() {{
            add(new MRSObservation<MRSConcept>(lastBCGDate, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(BCG.getName())));
            add(new MRSObservation<MRSConcept>(lastVitADate, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(VITA.getName())));
            add(new MRSObservation<MRSConcept>(lastMeaslesDate, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(MEASLES.getName())));
            add(new MRSObservation<MRSConcept>(lastYfDate, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(YF.getName())));
            add(new MRSObservation<Integer>(lastPentaDate, PENTA.getName(), lastPenta));
            add(new MRSObservation<Integer>(lastOPVDate, OPV.getName(), lastOPV));
            add(new MRSObservation<Integer>(lastIPTiDate, IPTI.getName(), lastIPTi));
            add(new MRSObservation<String>(registrationDate, CWC_REG_NUMBER.getName(), serialNumber));
        }};
        CareService careServiceSpy = spy(careService);
        careServiceSpy.enroll(cwcVO);
        doNothing().when(careServiceSpy).enrollToCWCCarePrograms(registrationDate, mockPatient);

        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffId, facilityId, CWC_REG_VISIT.value(), registrationDate, expected);
        verify(careServiceSpy).enrollToCWCCarePrograms(registrationDate, mockPatient);
    }

    @Test
    public void shouldNotIncludeConceptsIfNotGivenWhileEnrollmentToCWC() {
        final Date registartionDate = DateUtil.newDate(2011, 9, 1).toDate();
        final String patientId = "24324";
        final String patientMotechId = "1234567";
        final String staffId = "456";
        final String facilityId = "3232";
        final String serialNumber = "serial number";

        CwcVO cwcVO = new CwcVO(staffId, facilityId, registartionDate, patientMotechId, new ArrayList<CwcCareHistory>(), null, null,
                null, null, null, null, null, null, null, null, serialNumber, false);

        setupPatient(patientId, patientMotechId);

        careService.enroll(cwcVO);

        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(registartionDate, CWC_REG_NUMBER.getName(), serialNumber));
        }};
        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffId, facilityId, CWC_REG_VISIT.value(), registartionDate, mrsObservations);
    }

    @Test
    public void shouldSetRegistrationDateAsCurrentDateIfRegistrationTodayForAncRegistration() {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        final Date today = DateUtil.today().toDate();
        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.IN_PAST, today, facilityId, staffUserId, patientMotechId,
                new ArrayList<ANCCareHistory>(), DateUtil.newDate(2012, 9, 1).toDate());

        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<Integer>(today, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<String>(today, ANC_REG_NUM.getName(), ancvo.getSerialNumber()));
            add(new MRSObservation<Double>(today, HEIGHT.getName(), ancvo.getHeight()));
        }};

        setupPatient(patientId, patientMotechId);
        careService.enroll(ancvo);

        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, ANC_REG_VISIT.value(), today, expectedObservations);
    }

    @Test
    public void shouldEnrollToANCProgram() throws Exception {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        final Date registrationDate = new Date(2012, 3, 1);
        final Date today = DateUtil.today().toDate();

        final ANCVO ancvo = createTestANCVO("3", new Date(2011, 12, 9), "4", new Date(2011, 7, 5), RegistrationToday.IN_PAST, registrationDate, facilityId,
                staffUserId, patientMotechId, Arrays.asList(ANCCareHistory.values()), new Date());

        setupPatient(patientId, patientMotechId);

        careService.enroll(ancvo);

        final MRSObservation pregnancyObs = new MRSObservation(today, PREGNANCY.getName(), null);
        pregnancyObs.addDependantObservation(new MRSObservation<Date>(today, EDD.getName(), ancvo.getEstimatedDateOfDelivery()));
        pregnancyObs.addDependantObservation(new MRSObservation<Boolean>(today, CONFINEMENT_CONFIRMED.getName(), ancvo.getDeliveryDateConfirmed()));
        pregnancyObs.addDependantObservation(new MRSObservation<Boolean>(today, PREGNANCY_STATUS.getName(), true));

        final HashSet<MRSObservation> expectedPregnancyObservations = new HashSet<MRSObservation>() {{
            add(pregnancyObs);
        }};

        final HashSet<MRSObservation> expectedANCObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<Double>(today, HEIGHT.getName(), ancvo.getHeight()));
            add(new MRSObservation<Integer>(today, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<String>(registrationDate, ANC_REG_NUM.getName(), ancvo.getSerialNumber()));
            add(new MRSObservation<Integer>(ancvo.getAncCareHistoryVO().getLastIPTDate(), IPT.getName(), Integer.valueOf(ancvo.getAncCareHistoryVO().getLastIPT())));
            add(new MRSObservation<Integer>(ancvo.getAncCareHistoryVO().getLastTTDate(), TT.getName(), Integer.valueOf(ancvo.getAncCareHistoryVO().getLastTT())));
        }};

        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, ANC_REG_VISIT.value(), registrationDate, expectedANCObservations);
        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, PREG_REG_VISIT.value(), registrationDate, expectedPregnancyObservations);

    }

    @Test
    public void shouldEnrollToPNCMotherCareSchedules() {
        CareService careServiceSpy = spy(careService);
        Patient patient = mock(Patient.class);
        List<PatientCare> patientCares = asList(new PatientCare(PNC_MOTHER_1, DateUtil.today()));
        when(patient.pncMotherProgramsToEnrollOnRegistration()).thenReturn(patientCares);
        DateTime registrationTime = DateUtil.now();

        careServiceSpy.enrollMotherForPNC(patient, registrationTime);

        verify(careServiceSpy).enrollPatientCares(patientCares, patient, registrationTime.toLocalDate(), time(registrationTime));
    }

    @Test
    public void shouldEnrollPatientCares() {
        List<PatientCare> patientCares = asList(new PatientCare(PNC_MOTHER_1, DateUtil.today()));
        Patient patient = mock(Patient.class);
        when(patient.getMRSPatientId()).thenReturn("mrsPatientId");
        DateTime registrationTime = DateUtil.now();

        careService.enrollPatientCares(patientCares, patient, registrationTime.toLocalDate(), time(registrationTime));

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedules).enroll(enrollmentRequestCaptor.capture());
        EnrollmentRequest request = enrollmentRequestCaptor.getValue();
        assertThat(request.getScheduleName(), is(PNC_MOTHER_1));
        assertThat(request.getExternalId(), is(patient.getMRSPatientId()));
    }

    @Test
    public void shoulUpdateEddObservationIfFound() throws Exception {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        final Date registrationDate = new Date(2012, 3, 1);
        final Date today = DateUtil.today().toDate();

        final ANCVO ancvo = createTestANCVO("3", new Date(2011, 12, 9), "4", new Date(2011, 7, 5), RegistrationToday.IN_PAST, registrationDate, facilityId,
                staffUserId, patientMotechId, Arrays.asList(ANCCareHistory.values()), new Date());

        setupPatient(patientId, patientMotechId);

        final MRSObservation activePregnancy = new MRSObservation<Object>(today, PREGNANCY.getName(), null);
        activePregnancy.addDependantObservation(new MRSObservation<Boolean>(today, CONFINEMENT_CONFIRMED.getName(), ancvo.getDeliveryDateConfirmed()));
        activePregnancy.addDependantObservation(new MRSObservation<Boolean>(today, PREGNANCY_STATUS.getName(), true));
        Set<MRSObservation> updatedEddObservations = new HashSet<MRSObservation>() {{
            add(activePregnancy);
        }};
        when(mockAllObservations.updateEDD(ancvo.getEstimatedDateOfDelivery(), mockPatient,
                ancvo.getStaffId())).thenReturn(updatedEddObservations);

        careService.enroll(ancvo);

        final HashSet<MRSObservation> expectedANCObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<Double>(today, HEIGHT.getName(), ancvo.getHeight()));
            add(new MRSObservation<Integer>(today, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<String>(registrationDate, ANC_REG_NUM.getName(), ancvo.getSerialNumber()));
            add(new MRSObservation<Integer>(ancvo.getAncCareHistoryVO().getLastIPTDate(), IPT.getName(), Integer.valueOf(ancvo.getAncCareHistoryVO().getLastIPT())));
            add(new MRSObservation<Integer>(ancvo.getAncCareHistoryVO().getLastTTDate(), TT.getName(), Integer.valueOf(ancvo.getAncCareHistoryVO().getLastTT())));
        }};

        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, ANC_REG_VISIT.value(), registrationDate, expectedANCObservations);
        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, PREG_REG_VISIT.value(), registrationDate, updatedEddObservations);
    }


    @Test
    public void shouldNotAddObsIfValueNotGiven() {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        final Date registrationDate = new Date(2012, 1, 1);
        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.IN_PAST_IN_OTHER_FACILITY, registrationDate, facilityId, staffUserId,
                patientMotechId, new ArrayList<ANCCareHistory>(), new Date());

        setupPatient(patientId, patientMotechId);

        careService.enroll(ancvo);
        final Date today = DateUtil.today().toDate();
        final HashSet<MRSObservation> expectedANCObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<Double>(today, HEIGHT.getName(), ancvo.getHeight()));
            add(new MRSObservation<Integer>(today, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<String>(registrationDate, ANC_REG_NUM.getName(), ancvo.getSerialNumber()));
        }};

        final MRSObservation pregnancyObs = new MRSObservation(today, PREGNANCY.getName(), null);
        pregnancyObs.addDependantObservation(new MRSObservation<Date>(today, EDD.getName(), ancvo.getEstimatedDateOfDelivery()));
        pregnancyObs.addDependantObservation(new MRSObservation<Boolean>(today, CONFINEMENT_CONFIRMED.getName(), ancvo.getDeliveryDateConfirmed()));
        pregnancyObs.addDependantObservation(new MRSObservation<Boolean>(today, PREGNANCY_STATUS.getName(), true));

        final HashSet<MRSObservation> expectedPregnancyObservations = new HashSet<MRSObservation>() {{
            add(pregnancyObs);
        }};

        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, ANC_REG_VISIT.value(), registrationDate, expectedANCObservations);
        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, PREG_REG_VISIT.value(), registrationDate, expectedPregnancyObservations);
    }

    @Test
    public void shouldSaveCareHistoryDetails() {
        CareService careServiceSpy = spy(careService);

        ANCCareHistoryVO ancCareHistory = mock(ANCCareHistoryVO.class);
        CWCCareHistoryVO cwcCareHistory = mock(CWCCareHistoryVO.class);

        final String conceptOneValue = "concept one value";
        final String conceptTwoValue = "concept two value";
        final String conceptOneName = "concept one name";
        final String conceptTwoName = "concept two name";
        final Date ancRegDate = DateUtil.newDate(2011, 12, 23).toDate();
        final Date cwcRegDate = DateUtil.newDate(2011, 1, 2).toDate();

        Set<MRSObservation> ancObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(ancRegDate, conceptOneName, conceptOneValue));
        }};

        doReturn(ancObservations).when(careServiceSpy).addObservationsOnANCHistory(ancCareHistory);

        Set<MRSObservation> cwcObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(cwcRegDate, conceptTwoName, conceptTwoValue));
        }};

        doReturn(cwcObservations).when(careServiceSpy).addObservationsOnCWCHistory(cwcCareHistory);

        String staffId = "staff id";
        String facilityId = "facility id";
        String patientMotechId = "patient motech id";
        String patientId = "patient id";
        Date date = DateUtil.newDate(2011, 11, 11).toDate();

        setupPatient(patientId, patientMotechId);
        CareHistoryVO careHistory = new CareHistoryVO(staffId, facilityId, patientMotechId, date, ancCareHistory, cwcCareHistory);

        careServiceSpy.addCareHistory(careHistory);

        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(ancRegDate, conceptOneName, conceptOneValue));
            add(new MRSObservation<String>(cwcRegDate, conceptTwoName, conceptTwoValue));
        }};
        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffId, facilityId, PATIENT_HISTORY.value(), date, expectedObservations);
    }

    @Test
    public void shouldCreateSchedulesForANCProgramRegistration() {

        String patientId = "Id";
        String patientMotechId = "motechId";
        Pregnancy pregnancy = basedOnDeliveryDate(new LocalDate(2000, 1, 1));

        setupPatient(patientId, patientMotechId);
        PatientCare patientCare = new PatientCare("test", new LocalDate());
        when(mockPatient.ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery())).thenReturn(asList(patientCare));
        DateTime registrationDateTime = newDateTime(2000, 1, 1, 0, 0, 0);
        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.IN_PAST, registrationDateTime.toDate(), "facilityId", null,
                patientMotechId, new ArrayList<ANCCareHistory>(), pregnancy.dateOfDelivery().toDate());

        careService.enroll(ancvo);
        verify(mockPatient).ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery());
        verifyIfScheduleEnrolled(0, patientId, patientCare.startingOn(), registrationDateTime.toLocalDate(), null, patientCare.name());
    }

    @Test
    public void shouldCreateSchedulesForCWCProgramRegistration() {
        String patientId = "Id";
        String patientMotechId = "motechId";
        DateTime registrationDateTime = newDateTime(2012, 12, 2, 0, 0, 0);


        setupPatient(patientId, patientMotechId);
        when(mockPatient.getMotechId()).thenReturn(patientMotechId);

        PatientCare patientCare = new PatientCare("test", new LocalDate());
        when(mockPatient.cwcCareProgramToEnrollOnRegistration()).thenReturn(asList(patientCare));
        careService.enrollToCWCCarePrograms(registrationDateTime.toDate(), mockPatient);

        verify(mockPatient).cwcCareProgramToEnrollOnRegistration();
        verifyIfScheduleEnrolled(0, patientId, patientCare.startingOn(), registrationDateTime.toLocalDate(), null, patientCare.name());
    }

    @Test
    public void shouldCreatePNCSchedulesForChild() {
        String patientId = "Id";
        LocalDate birthDate = newDate(2012, 12, 21);
        Time birthTime = new Time(2, 3);
        PatientCare pnc1 = new PatientCare("PNC1", birthDate, birthTime);
        PatientCare pnc2 = new PatientCare("PNC2", birthDate, birthTime);
        setupPatient(patientId, null);
        when(mockPatient.pncBabyProgramsToEnrollOnRegistration()).thenReturn(asList(pnc1, pnc2));
        when(mockPatient.dateOfBirth()).thenReturn(newDateTime(birthDate, birthTime));

        careService.enrollChildForPNC(mockPatient);
        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedules, times(2)).enroll(requestCaptor.capture());
        List<EnrollmentRequest> requests = requestCaptor.getAllValues();
        assertScheduleEnrollmentRequest(requests.get(0), expectedRequest(patientId, pnc1, birthDate, birthTime, null));
        assertScheduleEnrollmentRequest(requests.get(1), expectedRequest(patientId, pnc2, birthDate, birthTime, null));
    }

    private void verifyIfScheduleEnrolled(int indexForSchedule, String patientId, LocalDate startingOn, LocalDate enrollmentDate, Time enrollmentTime, String enrollmentName) {
        ArgumentCaptor<EnrollmentRequest> deliveryEnrollmentRequestCaptor = forClass(EnrollmentRequest.class);
        verify(mockAllSchedules).enroll(deliveryEnrollmentRequestCaptor.capture());
        assertScheduleEnrollmentRequest(deliveryEnrollmentRequestCaptor.getAllValues().get(indexForSchedule),
                expectedRequest(patientId, new PatientCare(enrollmentName, startingOn), enrollmentDate, enrollmentTime, null));
    }

    private EnrollmentRequest expectedRequest(String externalId, PatientCare patientCare, LocalDate enrollmentDate, Time enrollmentTime, String startingMilestoneName) {
        return new EnrollmentRequest(externalId, patientCare.name(),
                new Time(currentDate.toLocalTime()), patientCare.startingOn(),
                patientCare.referenceTime(), enrollmentDate, enrollmentTime, startingMilestoneName);
    }

    private void assertScheduleEnrollmentRequest(EnrollmentRequest actualRequest, EnrollmentRequest expectedRequest) {
        assertThat(actualRequest.getExternalId(), is(equalTo(expectedRequest.getExternalId())));
        assertThat(actualRequest.getScheduleName(), is(equalTo(expectedRequest.getScheduleName())));
        assertThat(actualRequest.getPreferredAlertTime(), is(equalTo(expectedRequest.getPreferredAlertTime())));
        assertThat(actualRequest.getReferenceDateTime(), is(expectedRequest.getReferenceDateTime()));
        assertThat(actualRequest.getStartingMilestoneName(), is(expectedRequest.getStartingMilestoneName()));
        assertThat(actualRequest.getEnrollmentDateTime(), is(expectedRequest.getEnrollmentDateTime()));
    }

    private ANCVO createTestANCVO(String ipt, Date iptDate, String tt, Date ttDate, RegistrationToday registrationToday, Date registrationDate,
                                  String facilityId, String staffId, String patientMotechId, List<ANCCareHistory> careHistories, Date estimatedDateOfDelivery) {
        return new ANCVO(staffId, facilityId, patientMotechId, registrationDate, registrationToday, "2321322", estimatedDateOfDelivery,
                12.34, 12, 34, true, true, careHistories, ipt, tt, iptDate, ttDate, true);
    }

    private void setupPatient(String patientId, String patientMotechId) {
        mockPatient = mock(Patient.class);
        mockMRSPatient = mock(MRSPatient.class);

        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockPatient.getMRSPatientId()).thenReturn(patientId);
        when(mockPatient.getMotechId()).thenReturn(patientMotechId);
        when(mockMRSPatient.getId()).thenReturn(patientId);
        mockPatientService(mockPatient);
    }

    private void mockPatientService(Patient patient) {
        when(mockAllPatients.getPatientByMotechId(patient.getMotechId())).thenReturn(patient);
    }
}
