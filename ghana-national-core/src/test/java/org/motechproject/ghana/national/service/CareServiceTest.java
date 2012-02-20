package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.configuration.CareScheduleNames;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.vo.*;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.*;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_REG_VISIT;
import static org.motechproject.ghana.national.domain.EncounterType.CWC_REG_VISIT;
import static org.motechproject.ghana.national.domain.EncounterType.PATIENT_HISTORY;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class CareServiceTest extends BaseUnitTest {
    CareService careService;

    @Mock
    StaffService mockStaffService;

    @Mock
    PatientService mockPatientService;

    @Mock
    AllEncounters mockAllEncounters;

    @Mock    ScheduleTrackingService mockScheduleTrackingService;

    private DateTime currentDate;

    @Before
    public void setUp() {
        careService = new CareService();
        initMocks(this);
        ReflectionTestUtils.setField(careService, "staffService", mockStaffService);
        ReflectionTestUtils.setField(careService, "patientService", mockPatientService);
        ReflectionTestUtils.setField(careService, "allEncounters", mockAllEncounters);
        ReflectionTestUtils.setField(careService, "scheduleTrackingService", mockScheduleTrackingService);

        currentDate = DateTime.now();
        mockCurrentDate(currentDate);
    }

    @Test
    public void shouldEnrollToCWCProgram() {
        final String staffId = "456";
        final String staffPersonId = "32";
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

        setupStaffAndPatient(patientId, patientMotechId, staffId, staffPersonId);

        careService.enroll(cwcVO);

        ArgumentCaptor<MRSEncounter> mrsEncounterArgumentCaptor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters).save(mrsEncounterArgumentCaptor.capture());

        MRSEncounter encounterThatWasSaved = mrsEncounterArgumentCaptor.getValue();

        assertEncounterDetails(staffId, staffPersonId, patientId, facilityId, registrationDate, encounterThatWasSaved, CWC_REG_VISIT.value());
        assertThat(encounterThatWasSaved.getObservations().size(), is(8));
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
        assertReflectionEquals(expected, encounterThatWasSaved.getObservations(), ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void shouldNotIncludeConceptsIfNotGivenWhileEnrollmentToCWC() {
        final Date registartionDate = DateUtil.newDate(2011, 9, 1).toDate();
        final String patientId = "24324";
        final String patientMotechId = "1234567";
        final String staffId = "456";
        final String staffPersonId = "32";
        final String facilityId = "3232";
        String serialNumber = "serial number";

        CwcVO cwcVO = new CwcVO(staffId, facilityId, registartionDate, patientMotechId, new ArrayList<CwcCareHistory>(), null, null,
                null, null, null, null, null, null, null, null, serialNumber, false);

        setupStaffAndPatient(patientId, patientMotechId, staffId, staffPersonId);

        careService.enroll(cwcVO);

        ArgumentCaptor<MRSEncounter> mrsEncounterArgumentCaptor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters).save(mrsEncounterArgumentCaptor.capture());
        MRSEncounter actualEncounter = mrsEncounterArgumentCaptor.getValue();
        assertThat(actualEncounter.getObservations().size(), is(1));
    }

    @Test
    public void shouldEnrollToANCProgram() throws Exception {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        String staffPersonId = "staff person id";
        Date registrationDate = new Date(2012, 3, 1);
        final ANCVO ancvo = createTestANCVO("3", new Date(2011, 12, 9), "4", new Date(2011, 7, 5), RegistrationToday.IN_PAST, registrationDate, facilityId,
                staffUserId, patientMotechId, Arrays.asList(ANCCareHistory.values()), new Date());

        setupStaffAndPatient(patientId, patientMotechId, staffUserId, staffPersonId);

        careService.enroll(ancvo);

        ArgumentCaptor<MRSEncounter> mrsEncounterArgumentCaptor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters, times(2)).save(mrsEncounterArgumentCaptor.capture());

        final List<MRSEncounter> mrsEncounters = mrsEncounterArgumentCaptor.getAllValues();
        final MRSEncounter ancDetailsEncounter = mrsEncounters.get(0);

        assertEncounterDetails(staffUserId, staffPersonId, patientId, facilityId, registrationDate, ancDetailsEncounter, ANC_REG_VISIT.value());

        assertEquals(6, ancDetailsEncounter.getObservations().size());
        assertEquals(registrationDate, ancDetailsEncounter.getDate());
        final Date today = new Date();
        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<Double>(today, HEIGHT.getName(), ancvo.getHeight()));
            add(new MRSObservation<Integer>(today, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<String>(today, ANC_REG_NUM.getName(), ancvo.getSerialNumber()));
            add(new MRSObservation<Integer>(ancvo.getAncCareHistoryVO().getLastIPTDate(), IPT.getName(), Integer.valueOf(ancvo.getAncCareHistoryVO().getLastIPT())));
            add(new MRSObservation<Integer>(ancvo.getAncCareHistoryVO().getLastTTDate(), TT.getName(), Integer.valueOf(ancvo.getAncCareHistoryVO().getLastTT())));
        }};

        assertReflectionEquals(expectedObservations, ancDetailsEncounter.getObservations(), ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);

        final MRSEncounter pregnancyDetailsEncounter = mrsEncounters.get(1);
        assertEquals(1, pregnancyDetailsEncounter.getObservations().size());

        final HashSet<MRSObservation> expectedPregnancyObservations = new HashSet<MRSObservation>() {{
            final MRSObservation<Date> eddObs = new MRSObservation<Date>(today, EDD.getName(), ancvo.getEstimatedDateOfDelivery());
            final MRSObservation<Boolean> pregnancyStatusObs = new MRSObservation<Boolean>(today, PREGNANCY_STATUS.getName(), true);
            final MRSObservation<Boolean> eddConfirmedObs = new MRSObservation<Boolean>(today, CONFINEMENT_CONFIRMED.getName(), ancvo.getDeliveryDateConfirmed());
            final MRSObservation pregnancyObs = new MRSObservation(today, PREGNANCY.getName(), null);
            pregnancyObs.addDependantObservation(eddObs);
            pregnancyObs.addDependantObservation(pregnancyStatusObs);
            pregnancyObs.addDependantObservation(eddConfirmedObs);
            add(pregnancyObs);
        }};

        assertReflectionEquals(pregnancyDetailsEncounter.getObservations(), expectedPregnancyObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void shouldSetRegistrationDateAsCurrentDateIfRegistrationTodayForAncRegistration() {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        String staffPersonId = "staff person id";
        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.TODAY, new Date(2012, 1, 1), facilityId, staffUserId, patientMotechId,
                new ArrayList<ANCCareHistory>(), new Date());

        setupStaffAndPatient(patientId, patientMotechId, staffUserId, staffPersonId);

        careService.enroll(ancvo);

        final Date today = DateUtil.now().toDate();

        ArgumentCaptor<MRSEncounter> mrsEncounterArgumentCaptor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters, times(2)).save(mrsEncounterArgumentCaptor.capture());

        final List<MRSEncounter> mrsEncounters = mrsEncounterArgumentCaptor.getAllValues();
        final MRSEncounter encounterObjectPassedToSave = mrsEncounters.get(0);

        assertEncounterDetails(staffUserId, staffPersonId, patientId, facilityId, today, encounterObjectPassedToSave, ANC_REG_VISIT.value());

        assertEquals(4, encounterObjectPassedToSave.getObservations().size());
        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<Double>(today, HEIGHT.getName(), ancvo.getHeight()));
            add(new MRSObservation<Integer>(today, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<String>(today, ANC_REG_NUM.getName(), ancvo.getSerialNumber()));
        }};

        assertReflectionEquals(encounterObjectPassedToSave.getObservations(), expectedObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void shouldNotAddObsIfValueNotGiven() {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        String staffPersonId = "staff person id";
        Date registrationDate = new Date(2012, 1, 1);
        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.IN_PAST_IN_OTHER_FACILITY, registrationDate, facilityId, staffUserId,
                patientMotechId, new ArrayList<ANCCareHistory>(), new Date());

        setupStaffAndPatient(patientId, patientMotechId, staffUserId, staffPersonId);

        careService.enroll(ancvo);

        ArgumentCaptor<MRSEncounter> mrsEncounterArgumentCaptor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters, times(2)).save(mrsEncounterArgumentCaptor.capture());

        final List<MRSEncounter> mrsEncounters = mrsEncounterArgumentCaptor.getAllValues();
        final MRSEncounter encounterObjectPassedToSave = mrsEncounters.get(0);
        assertEncounterDetails(staffUserId, staffPersonId, patientId, facilityId, registrationDate, encounterObjectPassedToSave, ANC_REG_VISIT.value());

        assertEquals(4, encounterObjectPassedToSave.getObservations().size());

        final Date today = DateUtil.now().toDate();
        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<Double>(today, HEIGHT.getName(), ancvo.getHeight()));
            add(new MRSObservation<Integer>(today, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<String>(today, ANC_REG_NUM.getName(), ancvo.getSerialNumber()));
        }};

        assertReflectionEquals(encounterObjectPassedToSave.getObservations(), expectedObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
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

        Set<MRSObservation> ancObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(DateUtil.newDate(2011, 12, 23).toDate(), conceptOneName, conceptOneValue));
        }};

        doReturn(ancObservations).when(careServiceSpy).addObservationsOnANCHistory(ancCareHistory);

        Set<MRSObservation> cwcObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(DateUtil.newDate(2011, 1, 2).toDate(), conceptTwoName, conceptTwoValue));
        }};

        doReturn(cwcObservations).when(careServiceSpy).addObservationsOnCWCHistory(cwcCareHistory);

        String staffId = "staff id";
        String staffPersonId = "staff person id";
        String facilityId = "facility id";
        String patientMotechId = "patient motech id";
        String patientId = "patient id";
        Date date = DateUtil.newDate(2011, 11, 11).toDate();

        setupStaffAndPatient(patientId, patientMotechId, staffId, staffPersonId);
        CareHistoryVO careHistory = new CareHistoryVO(staffId, facilityId, patientMotechId, date, ancCareHistory, cwcCareHistory);

        careServiceSpy.addCareHistory(careHistory);

        ArgumentCaptor<MRSEncounter> mrsEncounterArgumentCaptor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters).save(mrsEncounterArgumentCaptor.capture());
        MRSEncounter encounterObjectPassedToSave = mrsEncounterArgumentCaptor.getValue();

        assertEncounterDetails(staffId, staffPersonId, patientId, facilityId, date, encounterObjectPassedToSave, PATIENT_HISTORY.value());

        final Date today = DateUtil.now().toDate();
        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(today, conceptOneName, conceptOneValue));
            add(new MRSObservation<String>(today, conceptTwoName, conceptTwoValue));
        }};

        assertReflectionEquals(encounterObjectPassedToSave.getObservations(), expectedObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void shouldCreateAExpectedPregnancyScheduleWhileRegisteringAPatientToANCProgram() {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        String staffPersonId = "staff person id";
        Date estimatedDateOfDelivery = DateUtil.newDate(2000, 1, 1).toDate();
        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.IN_PAST_IN_OTHER_FACILITY, DateUtil.newDate(2000, 1, 1).toDate(), facilityId, staffUserId,
                patientMotechId, new ArrayList<ANCCareHistory>(), estimatedDateOfDelivery);

        setupStaffAndPatient(patientId, patientMotechId, staffUserId, staffPersonId);

        careService.enroll(ancvo);

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestArgumentCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockScheduleTrackingService).enroll(enrollmentRequestArgumentCaptor.capture());

        EnrollmentRequest requestPassedToScheduleTracker = enrollmentRequestArgumentCaptor.getValue();
        assertThat(requestPassedToScheduleTracker.getExternalId(), is(equalTo(patientId)));
        assertThat(requestPassedToScheduleTracker.getScheduleName(), is(equalTo(CareScheduleNames.DELIVERY)));
        assertThat(requestPassedToScheduleTracker.getPreferredAlertTime(), is(equalTo(new Time(currentDate.toLocalTime()))));
        assertThat(requestPassedToScheduleTracker.getReferenceDate(), any(LocalDate.class));
    }

    private ANCVO createTestANCVO(String ipt, Date iptDate, String tt, Date ttDate, RegistrationToday registrationToday, Date registrationDate,
                                  String facilityId, String staffId, String patientMotechId, List<ANCCareHistory> careHistories, Date estimatedDateOfDelivery) {
        return new ANCVO(staffId, facilityId, patientMotechId, registrationDate, registrationToday, "2321322", estimatedDateOfDelivery,
                12.34, 12, 34, true, true, careHistories, ipt, tt, iptDate, ttDate, true);
    }

    private void setupStaffAndPatient(String patientId, String patientMotechId, String staffId, String staffPersonId) {
        MRSUser mockMRSUser = mock(MRSUser.class);
        Patient mockPatient = mock(Patient.class);

        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        MRSPerson mockMRSPerson = mock(MRSPerson.class);

        when(mockPatientService.getPatientByMotechId(patientMotechId)).thenReturn(mockPatient);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockPatient.getMRSPatientId()).thenReturn(patientId);
        when(mockMRSPatient.getId()).thenReturn(patientId);

        when(mockStaffService.getUserByEmailIdOrMotechId(staffId)).thenReturn(mockMRSUser);
        when(mockMRSUser.getPerson()).thenReturn(mockMRSPerson);

        when(mockMRSUser.getId()).thenReturn(staffId);
        when(mockMRSPerson.getId()).thenReturn(staffPersonId);
    }

    private void assertEncounterDetails(String staffId, String staffPersonId, String patientId, String facilityId, Date registrationDate,
                                        MRSEncounter encounterThatWasSaved, String encounterType) {
        assertThat(encounterThatWasSaved.getProvider().getId(), is(equalTo(staffPersonId)));
        assertThat(encounterThatWasSaved.getCreator().getId(), is(equalTo(staffId)));
        assertThat(encounterThatWasSaved.getFacility().getId(), is(equalTo(facilityId)));
        assertThat(encounterThatWasSaved.getDate(), is(equalTo(registrationDate)));
        assertThat(encounterThatWasSaved.getPatient().getId(), is(equalTo(patientId)));
        assertThat(encounterThatWasSaved.getEncounterType(), is(equalTo(encounterType)));
    }

}
