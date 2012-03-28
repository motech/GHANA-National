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
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PNC_MOTHER_1;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.*;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.newDateTime;

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
        doNothing().when(careServiceSpy).enrollToCWCCarePrograms(cwcVO, mockPatient);

        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffId, facilityId, CWC_REG_VISIT.value(), registrationDate, expected);
        verify(careServiceSpy).enrollToCWCCarePrograms(cwcVO, mockPatient);
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
    public void shouldIncludeCareHistoriesThatAreCapturedInHistoryEncounter() {
        final LocalDate registartionDate = DateUtil.newDate(2011, 9, 1);
        MRSEncounter mockHistroyEncounter = mock(MRSEncounter.class);
        List<MRSObservation> mrsObservations = new ArrayList<MRSObservation>() {{
            add(new MRSObservation<MRSConcept>(registartionDate.minusMonths(2).toDate(), Concept.IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(BCG.getName())));
            add(new MRSObservation<MRSConcept>(registartionDate.minusMonths(2).toDate(), Concept.IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(YF.getName())));
            add(new MRSObservation<MRSConcept>(registartionDate.minusMonths(2).toDate(), Concept.IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(MEASLES.getName())));

        }};


        List<CwcCareHistory> cwcCareHistories = careService.refineCwcCareHistories(mrsObservations, null);

        assertThat(cwcCareHistories, hasItem(CwcCareHistory.BCG));
        assertThat(cwcCareHistories, hasItem(CwcCareHistory.YF));
        assertThat(cwcCareHistories, hasItem(CwcCareHistory.MEASLES));
    }

    @Test
    public void shouldNotIncludeCareHistoriesThatAreNotCapturedInHistoryEncounter() {
        final LocalDate registartionDate = DateUtil.newDate(2011, 9, 1);
        List<MRSObservation> mrsObservations = new ArrayList<MRSObservation>();
        List<CwcCareHistory> cwcCareHistories = careService.refineCwcCareHistories(mrsObservations, null);

        assertThat(cwcCareHistories, not(hasItem(CwcCareHistory.BCG)));
        assertThat(cwcCareHistories, not(hasItem(CwcCareHistory.YF)));
        assertThat(cwcCareHistories, not(hasItem(CwcCareHistory.MEASLES)));
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
        DateTime deliveryTime = DateUtil.now();
        List<PatientCare> patientCares = asList(mock(PatientCare.class));
        when(patient.pncMotherProgramsToEnrollOnRegistration(deliveryTime)).thenReturn(patientCares);

        careServiceSpy.enrollMotherForPNC(patient, deliveryTime);

        verify(careServiceSpy).enrollPatientCares(patientCares, patient);
    }

    @Test
    public void shouldEnrollPatientCares() {
        Patient patient = mock(Patient.class);
        when(patient.getMRSPatientId()).thenReturn("mrsPatientId");
        DateTime registrationTime = DateUtil.now();
        List<PatientCare> patientCares = asList(new PatientCare(PNC_MOTHER_1, registrationTime, registrationTime, new HashMap<String, String>()));

        careService.enrollPatientCares(patientCares, patient);

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
        String patientId = "Id", patientMotechId = "motechId";
        Pregnancy pregnancy = basedOnDeliveryDate(new LocalDate(2000, 1, 1));

        currentDate = newDateTime(2000, 1, 1, 12, 2, 0);
        mockCurrentDate(currentDate);
        setupPatient(patientId, patientMotechId);
        PatientCare patientCare = new PatientCare("test", new LocalDate(), null, null);
        LocalDate registrationDate = newDate(2000, 1, 1);
        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.IN_PAST, registrationDate.toDate(), "facilityId", null,
                patientMotechId, new ArrayList<ANCCareHistory>(), pregnancy.dateOfDelivery().toDate());

        EnrollmentRecord mockEnrollmentRecord = mock(EnrollmentRecord.class);
        when(mockAllSchedules.getActiveEnrollment(patientId, TT_VACCINATION)).thenReturn(mockEnrollmentRecord);

        ActiveCareSchedules activeCareSchedules = new ActiveCareSchedules().setActiveCareSchedule(TT_VACCINATION, mockEnrollmentRecord);
        when(mockPatient.ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery(), registrationDate, ancvo.getAncCareHistoryVO(), activeCareSchedules)).thenReturn(asList(patientCare));

        careService.enroll(ancvo);
        verify(mockPatient).ancCareProgramsToEnrollOnRegistration(pregnancy.dateOfDelivery(), registrationDate, ancvo.getAncCareHistoryVO(), activeCareSchedules);
        verifyIfScheduleEnrolled(0, expectedRequest(patientId, new PatientCare(patientCare.name(), patientCare.startingOn(), registrationDate, null), null));
    }

    @Test
    public void shouldGetActiveCareSchedulesForAPatient() {
        String patientId = "patientId";
        setupPatient(patientId, "patientMotechId");
        when(mockAllSchedules.getActiveEnrollment(patientId, TT_VACCINATION)).thenReturn(null);
        assertThat(careService.activeCareSchedules(mockPatient).hasActiveTTSchedule(), is(equalTo(false)));

        EnrollmentRecord enrollmentRecord = mock(EnrollmentRecord.class);
        when(mockAllSchedules.getActiveEnrollment(patientId, TT_VACCINATION)).thenReturn(enrollmentRecord);
        assertThat(careService.activeCareSchedules(mockPatient).hasActiveTTSchedule(), is(equalTo(true)));
    }

    @Test
    public void shouldCreateSchedulesForCWCProgramRegistration() {
        String patientId = "Id";
        String patientMotechId = "motechId";
        DateTime registrationDateTime = newDateTime(2012, 12, 2, 0, 0, 0);

        setupPatient(patientId, patientMotechId);
        when(mockPatient.getMotechId()).thenReturn(patientMotechId);

        PatientCare patientCare = new PatientCare("test", new LocalDate(), registrationDateTime.toLocalDate(), null);
        when(mockPatient.cwcCareProgramToEnrollOnRegistration(registrationDateTime.toLocalDate(), new ArrayList<CwcCareHistory>())).thenReturn(asList(patientCare));

        CwcVO cwcVO = new CwcVO(null, null, registrationDateTime.toDate(), patientMotechId, null, null, null,
                null, null, null, null, null, null, null, null, null, true);
        careService.enrollToCWCCarePrograms(cwcVO, mockPatient);

        verify(mockPatient).cwcCareProgramToEnrollOnRegistration(registrationDateTime.toLocalDate(), new ArrayList<CwcCareHistory>());
        verifyIfScheduleEnrolled(0, expectedRequest(patientId, new PatientCare(patientCare.name(), patientCare.startingOn(), registrationDateTime.toLocalDate(), null), null));
    }

    @Test
    public void shouldCreatePNCSchedulesForChild() {
        String patientId = "Id";
        DateTime birthDateTime = newDateTime(2012, 12, 21, 2, 3, 0);
        PatientCare pnc1 = new PatientCare("PNC1", birthDateTime, birthDateTime, null);
        PatientCare pnc2 = new PatientCare("PNC2", birthDateTime, birthDateTime, null);
        setupPatient(patientId, null);
        when(mockPatient.pncBabyProgramsToEnrollOnRegistration()).thenReturn(asList(pnc1, pnc2));
        when(mockPatient.dateOfBirth()).thenReturn(birthDateTime);

        careService.enrollChildForPNCOnDelivery(mockPatient);
        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedules, times(2)).enroll(requestCaptor.capture());
        List<EnrollmentRequest> requests = requestCaptor.getAllValues();
        assertScheduleEnrollmentRequest(requests.get(0), expectedRequest(patientId, pnc1, null));
        assertScheduleEnrollmentRequest(requests.get(1), expectedRequest(patientId, pnc2, null));
    }

    private void verifyIfScheduleEnrolled(int indexForSchedule, EnrollmentRequest expectedRequest) {
        ArgumentCaptor<EnrollmentRequest> requestCaptor = forClass(EnrollmentRequest.class);
        verify(mockAllSchedules).enroll(requestCaptor.capture());
        assertScheduleEnrollmentRequest(requestCaptor.getAllValues().get(indexForSchedule),
                expectedRequest);
    }

    private EnrollmentRequest expectedRequest(String externalId, PatientCare patientCare, String startingMilestoneName) {
        return new EnrollmentRequest(externalId, patientCare.name(),
                patientCare.preferredTime(), patientCare.startingOn(),
                patientCare.referenceTime(), patientCare.enrollmentDate(), patientCare.enrollmentTime(), startingMilestoneName, patientCare.metaData());
    }

    private void assertScheduleEnrollmentRequest(EnrollmentRequest actualRequest, EnrollmentRequest expectedRequest) {
        assertThat(actualRequest.getExternalId(), is(equalTo(expectedRequest.getExternalId())));
        assertThat(actualRequest.getScheduleName(), is(equalTo(expectedRequest.getScheduleName())));
        assertThat("preferredAlertTime", actualRequest.getPreferredAlertTime(), is(equalTo(expectedRequest.getPreferredAlertTime())));
        assertThat("referenceTime", actualRequest.getReferenceDateTime(), is(expectedRequest.getReferenceDateTime()));
        assertThat(actualRequest.getStartingMilestoneName(), is(expectedRequest.getStartingMilestoneName()));
        assertThat("enrollmentTime", actualRequest.getEnrollmentDateTime(), is(expectedRequest.getEnrollmentDateTime()));
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

    private ANCCareHistoryVO noANCHistory() {
        return new ANCCareHistoryVO(false, new ArrayList<ANCCareHistory>(), null, null, null, null);
    }
}
