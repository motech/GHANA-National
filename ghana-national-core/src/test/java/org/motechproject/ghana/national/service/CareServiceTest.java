package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.vo.ANCCareHistoryVO;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.vo.CWCCareHistoryVO;
import org.motechproject.ghana.national.vo.CareHistoryVO;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
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

public class CareServiceTest extends BaseUnitTest {
    CareService careService;

    @Mock
    PatientService mockPatientService;

    @Mock
    EncounterService mockEncounterService;

    @Mock
    ScheduleTrackingService mockScheduleTrackingService;

    private DateTime currentDate;
    MRSUser mockMRSUser;
    Patient mockPatient;
    MRSPatient mockMRSPatient;
    MRSPerson mockMRSPerson;

    @Before
    public void setUp() {
        careService = new CareService();
        initMocks(this);
        ReflectionTestUtils.setField(careService, "patientService", mockPatientService);
        ReflectionTestUtils.setField(careService, "encounterService", mockEncounterService);
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
        careService.enroll(cwcVO);

        verify(mockEncounterService).persistEncounter(mockMRSPatient, staffId, facilityId, CWC_REG_VISIT.value(), registrationDate, expected);
    }

    @Test
    public void shouldNotIncludeConceptsIfNotGivenWhileEnrollmentToCWC() {
        final Date registartionDate = DateUtil.newDate(2011, 9, 1).toDate();
        final String patientId = "24324";
        final String patientMotechId = "1234567";
        final String staffId = "456";
        final String staffPersonId = "32";
        final String facilityId = "3232";
        final String serialNumber = "serial number";

        CwcVO cwcVO = new CwcVO(staffId, facilityId, registartionDate, patientMotechId, new ArrayList<CwcCareHistory>(), null, null,
                null, null, null, null, null, null, null, null, serialNumber, false);

        setupStaffAndPatient(patientId, patientMotechId, staffId, staffPersonId);

        careService.enroll(cwcVO);

        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(registartionDate, CWC_REG_NUMBER.getName(), serialNumber));
        }};
        verify(mockEncounterService).persistEncounter(mockMRSPatient, staffId, facilityId, CWC_REG_VISIT.value(), registartionDate, mrsObservations);
    }

    @Test
    public void shouldSetRegistrationDateAsCurrentDateIfRegistrationTodayForAncRegistration() {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        String staffPersonId = "staff person id";
        final Date today = DateUtil.today().toDate();
        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.IN_PAST, today, facilityId, staffUserId, patientMotechId,
                new ArrayList<ANCCareHistory>(), DateUtil.newDate(2012, 9, 1).toDate());

        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<Integer>(today, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<String>(today, ANC_REG_NUM.getName(), ancvo.getSerialNumber()));
            add(new MRSObservation<Double>(today, HEIGHT.getName(), ancvo.getHeight()));
        }};

        setupStaffAndPatient(patientId, patientMotechId, staffUserId, staffPersonId);
        careService.enroll(ancvo);

        verify(mockEncounterService).persistEncounter(mockMRSPatient, staffUserId, facilityId, ANC_REG_VISIT.value(), today, expectedObservations);
    }

    @Test
    public void shouldEnrollToANCProgram() throws Exception {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        String staffPersonId = "staff person id";
        final Date registrationDate = new Date(2012, 3, 1);
        final Date today = DateUtil.today().toDate();

        final ANCVO ancvo = createTestANCVO("3", new Date(2011, 12, 9), "4", new Date(2011, 7, 5), RegistrationToday.IN_PAST, registrationDate, facilityId,
                staffUserId, patientMotechId, Arrays.asList(ANCCareHistory.values()), new Date());

        setupStaffAndPatient(patientId, patientMotechId, staffUserId, staffPersonId);

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

        verify(mockEncounterService).persistEncounter(mockMRSPatient, staffUserId, facilityId, ANC_REG_VISIT.value(), registrationDate, expectedANCObservations);
        verify(mockEncounterService).persistEncounter(mockMRSPatient, staffUserId, facilityId, PREG_REG_VISIT.value(), registrationDate, expectedPregnancyObservations);

    }


    @Test
    public void shouldNotAddObsIfValueNotGiven() {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        String staffPersonId = "staff person id";
        final Date registrationDate = new Date(2012, 1, 1);
        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.IN_PAST_IN_OTHER_FACILITY, registrationDate, facilityId, staffUserId,
                patientMotechId, new ArrayList<ANCCareHistory>(), new Date());

        setupStaffAndPatient(patientId, patientMotechId, staffUserId, staffPersonId);

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

        verify(mockEncounterService).persistEncounter(mockMRSPatient, staffUserId, facilityId, ANC_REG_VISIT.value(), registrationDate, expectedANCObservations);
        verify(mockEncounterService).persistEncounter(mockMRSPatient, staffUserId, facilityId, PREG_REG_VISIT.value(), registrationDate, expectedPregnancyObservations);
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
        String staffPersonId = "staff person id";
        String facilityId = "facility id";
        String patientMotechId = "patient motech id";
        String patientId = "patient id";
        Date date = DateUtil.newDate(2011, 11, 11).toDate();

        setupStaffAndPatient(patientId, patientMotechId, staffId, staffPersonId);
        CareHistoryVO careHistory = new CareHistoryVO(staffId, facilityId, patientMotechId, date, ancCareHistory, cwcCareHistory);

        careServiceSpy.addCareHistory(careHistory);

        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(ancRegDate, conceptOneName, conceptOneValue));
            add(new MRSObservation<String>(cwcRegDate, conceptTwoName, conceptTwoValue));
        }};
        verify(mockEncounterService).persistEncounter(mockMRSPatient, staffId, facilityId, PATIENT_HISTORY.value(), date, expectedObservations);
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
        assertThat(requestPassedToScheduleTracker.getScheduleName(), is(equalTo(ScheduleNames.DELIVERY)));
        assertThat(requestPassedToScheduleTracker.getPreferredAlertTime(), is(equalTo(new Time(currentDate.toLocalTime()))));
        assertThat(requestPassedToScheduleTracker.getReferenceDate(), any(LocalDate.class));
    }

    private ANCVO createTestANCVO(String ipt, Date iptDate, String tt, Date ttDate, RegistrationToday registrationToday, Date registrationDate,
                                  String facilityId, String staffId, String patientMotechId, List<ANCCareHistory> careHistories, Date estimatedDateOfDelivery) {
        return new ANCVO(staffId, facilityId, patientMotechId, registrationDate, registrationToday, "2321322", estimatedDateOfDelivery,
                12.34, 12, 34, true, true, careHistories, ipt, tt, iptDate, ttDate, true);
    }

    private void setupStaffAndPatient(String patientId, String patientMotechId, String staffId, String staffPersonId) {
        mockMRSUser = mock(MRSUser.class);
        mockPatient = mock(Patient.class);
        mockMRSPatient = mock(MRSPatient.class);
        mockMRSPerson = mock(MRSPerson.class);

        when(mockPatientService.getPatientByMotechId(patientMotechId)).thenReturn(mockPatient);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockPatient.getMRSPatientId()).thenReturn(patientId);
        when(mockMRSPatient.getId()).thenReturn(patientId);
        when(mockMRSUser.getPerson()).thenReturn(mockMRSPerson);
        when(mockMRSUser.getId()).thenReturn(staffId);
        when(mockMRSPerson.getId()).thenReturn(staffPersonId);
    }

}
