package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.vo.*;
import org.motechproject.mrs.model.*;
import org.motechproject.openmrs.services.OpenMRSConceptAdaptor;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class CareServiceTest extends BaseUnitTest {
    CareService careService;

    @Mock
    StaffService mockStaffService;

    @Mock
    PatientService mockPatientService;

    @Mock
    AllEncounters mockAllEncounters;

    @Mock
    OpenMRSConceptAdaptor mockOpenMRSConceptAdaptor;

    @Before
    public void setUp() {
        careService = new CareService();
        initMocks(this);
        ReflectionTestUtils.setField(careService, "staffService", mockStaffService);
        ReflectionTestUtils.setField(careService, "patientService", mockPatientService);
        ReflectionTestUtils.setField(careService, "allEncounters", mockAllEncounters);
        ReflectionTestUtils.setField(careService, "openMRSConceptAdaptor", mockOpenMRSConceptAdaptor);
        DateTime currentDate = DateTime.now();
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

        assertEncounterDetails(staffId, staffPersonId, patientId, facilityId, registrationDate, encounterThatWasSaved, Constants.ENCOUNTER_CWCREGVISIT);
        assertThat(encounterThatWasSaved.getObservations().size(), is(8));
        final HashSet<MRSObservation> expected = new HashSet<MRSObservation>() {{
            add(new MRSObservation(lastBCGDate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, null));
            add(new MRSObservation(lastVitADate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, null));
            add(new MRSObservation(lastMeaslesDate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, null));
            add(new MRSObservation(lastYfDate, Constants.CONCEPT_IMMUNIZATIONS_ORDERED, null));
            add(new MRSObservation(lastPentaDate, Constants.CONCEPT_PENTA, lastPenta));
            add(new MRSObservation(registrationDate, Constants.CONCEPT_CWC_REG_NUMBER, serialNumber));
            add(new MRSObservation(lastOPVDate, Constants.CONCEPT_OPV, lastOPV));
            add(new MRSObservation(lastIPTiDate, Constants.CONCEPT_IPTI, lastIPTi));
        }};
        assertReflectionEquals(encounterThatWasSaved.getObservations(), expected, ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void shouldNotIncludeConceptsIfNotGivenWhileEnrollmentToCWC() {
        final Date registartionDate = new Date(2011, 9, 1);
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
    public void shouldEnrollANC() throws Exception {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        String staffPersonId = "staff person id";
        Date registrationDate = new Date(2012, 3, 1);
        final ANCVO ancvo = createTestANCVO("3", new Date(2011, 12, 9), "4", new Date(2011, 7, 5), RegistrationToday.IN_PAST, registrationDate, facilityId,
                staffUserId, patientMotechId, Arrays.asList(ANCCareHistory.values()));

        setupStaffAndPatient(patientId, patientMotechId, staffUserId, staffPersonId);

        careService.enroll(ancvo);

        ArgumentCaptor<MRSEncounter> mrsEncounterArgumentCaptor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters, times(2)).save(mrsEncounterArgumentCaptor.capture());

        final List<MRSEncounter> mrsEncounters = mrsEncounterArgumentCaptor.getAllValues();
        final MRSEncounter ancDetailsEncounter = mrsEncounters.get(0);

        assertEncounterDetails(staffUserId, staffPersonId, patientId, facilityId, registrationDate, ancDetailsEncounter, Constants.ENCOUNTER_ANCREGVISIT);

        assertEquals(6, ancDetailsEncounter.getObservations().size());
        assertEquals(registrationDate, ancDetailsEncounter.getDate());
        final Date today = new Date();
        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, Constants.CONCEPT_GRAVIDA, ancvo.getGravida()));
            add(new MRSObservation<Double>(today, Constants.CONCEPT_HEIGHT, ancvo.getHeight()));
            add(new MRSObservation<Integer>(today, Constants.CONCEPT_PARITY, ancvo.getParity()));
            add(new MRSObservation<String>(today, Constants.CONCEPT_ANC_REG_NUM, ancvo.getSerialNumber()));
            add(new MRSObservation<Integer>(ancvo.getAncCareHistoryVO().getLastIPTDate(), Constants.CONCEPT_IPT, Integer.valueOf(ancvo.getAncCareHistoryVO().getLastIPT())));
            add(new MRSObservation<Integer>(ancvo.getAncCareHistoryVO().getLastTTDate(), Constants.CONCEPT_TT, Integer.valueOf(ancvo.getAncCareHistoryVO().getLastTT())));
        }};

        assertReflectionEquals(ancDetailsEncounter.getObservations(), expectedObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);

        final MRSEncounter pregnancyDetailsEncounter = mrsEncounters.get(1);
        assertEquals(1, pregnancyDetailsEncounter.getObservations().size());

        final HashSet<MRSObservation> expectedPregnancyObservations = new HashSet<MRSObservation>() {{
            final MRSObservation<Date> eddObs = new MRSObservation<Date>(today, Constants.CONCEPT_EDD, ancvo.getEstimatedDateOfDelivery());
            final MRSObservation<Boolean> pregnancyStatusObs = new MRSObservation<Boolean>(today, Constants.CONCEPT_PREGNANCY_STATUS, true);
            final MRSObservation<Boolean> eddConfirmedObs = new MRSObservation<Boolean>(today, Constants.CONCEPT_CONFINEMENT_CONFIRMED, ancvo.getDeliveryDateConfirmed());
            final MRSObservation pregnancyObs = new MRSObservation(today, Constants.CONCEPT_PREGNANCY, null);
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
                new ArrayList<ANCCareHistory>());

        setupStaffAndPatient(patientId, patientMotechId, staffUserId, staffPersonId);

        careService.enroll(ancvo);

        final Date today = DateUtil.now().toDate();

        ArgumentCaptor<MRSEncounter> mrsEncounterArgumentCaptor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters, times(2)).save(mrsEncounterArgumentCaptor.capture());

        final List<MRSEncounter> mrsEncounters = mrsEncounterArgumentCaptor.getAllValues();
        final MRSEncounter encounterObjectPassedToSave = mrsEncounters.get(0);

        assertEncounterDetails(staffUserId, staffPersonId, patientId, facilityId, today, encounterObjectPassedToSave, Constants.ENCOUNTER_ANCREGVISIT);

        assertEquals(4, encounterObjectPassedToSave.getObservations().size());
        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, Constants.CONCEPT_GRAVIDA, ancvo.getGravida()));
            add(new MRSObservation<Double>(today, Constants.CONCEPT_HEIGHT, ancvo.getHeight()));
            add(new MRSObservation<Integer>(today, Constants.CONCEPT_PARITY, ancvo.getParity()));
            add(new MRSObservation<String>(today, Constants.CONCEPT_ANC_REG_NUM, ancvo.getSerialNumber()));
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
                patientMotechId, new ArrayList<ANCCareHistory>());

        setupStaffAndPatient(patientId, patientMotechId, staffUserId, staffPersonId);

        careService.enroll(ancvo);

        ArgumentCaptor<MRSEncounter> mrsEncounterArgumentCaptor = ArgumentCaptor.forClass(MRSEncounter.class);
        verify(mockAllEncounters, times(2)).save(mrsEncounterArgumentCaptor.capture());

        final List<MRSEncounter> mrsEncounters = mrsEncounterArgumentCaptor.getAllValues();
        final MRSEncounter encounterObjectPassedToSave = mrsEncounters.get(0);
        assertEncounterDetails(staffUserId, staffPersonId, patientId, facilityId, registrationDate, encounterObjectPassedToSave, Constants.ENCOUNTER_ANCREGVISIT);

        assertEquals(4, encounterObjectPassedToSave.getObservations().size());

        final Date today = DateUtil.now().toDate();
        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, Constants.CONCEPT_GRAVIDA, ancvo.getGravida()));
            add(new MRSObservation<Double>(today, Constants.CONCEPT_HEIGHT, ancvo.getHeight()));
            add(new MRSObservation<Integer>(today, Constants.CONCEPT_PARITY, ancvo.getParity()));
            add(new MRSObservation<String>(today, Constants.CONCEPT_ANC_REG_NUM, ancvo.getSerialNumber()));
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

        assertEncounterDetails(staffId, staffPersonId, patientId, facilityId, date, encounterObjectPassedToSave, Constants.ENCOUNTER_PATIENTHISTORY);

        final Date today = DateUtil.now().toDate();
        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(today, conceptOneName, conceptOneValue));
            add(new MRSObservation<String>(today, conceptTwoName, conceptTwoValue));
        }};

        assertReflectionEquals(encounterObjectPassedToSave.getObservations(), expectedObservations, ReflectionComparatorMode.LENIENT_DATES,
                ReflectionComparatorMode.LENIENT_ORDER);
    }

    private ANCVO createTestANCVO(String ipt, Date iptDate, String tt, Date ttDate, RegistrationToday registrationToday, Date registrationDate,
                                  String facilityId, String staffId, String patientMotechId, List<ANCCareHistory> careHistories) {
        return new ANCVO(staffId, facilityId, patientMotechId, registrationDate, registrationToday, "2321322", new Date(),
                12.34, 12, 34, true, true, careHistories, ipt, tt, iptDate, ttDate, true);
    }

    private void setupStaffAndPatient(String patientId, String patientMotechId, String staffId, String staffPersonId) {
        MRSUser mockMRSUser = mock(MRSUser.class);
        Patient mockPatient = mock(Patient.class);

        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        MRSPerson mockMRSPerson = mock(MRSPerson.class);

        when(mockPatientService.getPatientByMotechId(patientMotechId)).thenReturn(mockPatient);
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
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
