package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.ghana.national.vo.*;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.model.*;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.*;

import static java.lang.Double.parseDouble;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.*;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnConceptionDate;
import static org.motechproject.util.DateUtil.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class CareServiceTest extends BaseUnitTest {
    CareService careService;

    @Mock
    AllPatients mockAllPatients;

    @Mock
    AllEncounters mockAllEncounters;

    @Mock
    AllCareSchedules mockAllCareSchedules;

    @Mock
    AllObservations mockAllObservations;

    Patient mockPatient;
    MRSPatient mockMRSPatient;

    @Before
    public void setUp() {
        initMocks(this);
        careService = new CareService(mockAllPatients, mockAllEncounters, mockAllObservations, mockAllCareSchedules);

        DateTime currentDate = DateTime.now();
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
        final Integer lastRotavirus = 1;
        final String lastVitA = "blue";
        final Integer lastPneumococcal = 2;
        final Date lastRotavirusDate = new Date(2011, 9, 6);
        final Date lastPneumococcalDate = new Date(2011, 9, 3);
        final int lastIPTi = 1;
        final int lastPenta = 1;
        final int lastOPV = 0;
        final String serialNumber = "wewew";
        final Integer lastMeasles = 1;
        CwcVO cwcVO = new CwcVO(staffId, facilityId, registrationDate, patientMotechId, Arrays.asList(CwcCareHistory.values()),
                lastBCGDate, lastVitADate, lastVitA, lastMeaslesDate, lastMeasles, lastYfDate, lastPentaDate, lastPenta,
                lastOPVDate, lastOPV, lastIPTiDate, lastIPTi, lastRotavirusDate, lastRotavirus, lastPneumococcal,
                lastPneumococcalDate, serialNumber, true);

        ArgumentCaptor<Set> obsCaptor = ArgumentCaptor.forClass(Set.class);
        setupPatient(patientId, patientMotechId);

        final HashSet<MRSObservation> expected = new HashSet<MRSObservation>() {{
            add(new MRSObservation<MRSConcept>(lastBCGDate, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(BCG.getName())));
            add(new MRSObservation<MRSConcept>(lastMeaslesDate, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(MEASLES_1.getName())));
            add(new MRSObservation<MRSConcept>(lastYfDate, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(YF.getName())));
            add(new MRSObservation<MRSConcept>(lastVitADate, IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(VITAMIN_A_BLUE.getName())));
            add(new MRSObservation<Integer>(lastRotavirusDate, ROTAVIRUS.getName(), lastRotavirus));
            add(new MRSObservation<Integer>(lastPentaDate, PENTA.getName(), lastPenta));
            add(new MRSObservation<Integer>(lastOPVDate, OPV.getName(), lastOPV));
            add(new MRSObservation<Integer>(lastIPTiDate, IPTI.getName(), lastIPTi));
            add(new MRSObservation<Integer>(lastPneumococcalDate, PNEUMOCOCCAL.getName(), lastPneumococcal));
            add(new MRSObservation<String>(registrationDate, SERIAL_NUMBER.getName(), serialNumber));
        }};
        CareService careServiceSpy = spy(careService);
        careServiceSpy.enroll(cwcVO);
        doNothing().when(careServiceSpy).enrollToCWCCarePrograms(cwcVO, mockPatient);

        verify(mockAllEncounters).persistEncounter(eq(mockMRSPatient), eq(staffId), eq(facilityId), eq(CWC_REG_VISIT.value()), eq(registrationDate), obsCaptor.capture());
        verify(careServiceSpy).enrollToCWCCarePrograms(cwcVO, mockPatient);
        assertReflectionEquals(expected, obsCaptor.getValue(), ReflectionComparatorMode.LENIENT_ORDER);

    }

    @Test
    public void shouldNotIncludeConceptsIfNotGivenWhileEnrollmentToCWC() {
        final Date registartionDate = DateUtil.newDate(2011, 9, 1).toDate();
        final String patientId = "24324";
        final String patientMotechId = "1234567";
        final String staffId = "456";
        final String facilityId = "3232";
        final String serialNumber = "serial number";

        CwcVO cwcVO = new CwcVO(staffId, facilityId, registartionDate, patientMotechId, new ArrayList<CwcCareHistory>(), null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, serialNumber, false);

        setupPatient(patientId, patientMotechId);

        careService.enroll(cwcVO);

        HashSet<MRSObservation> mrsObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(registartionDate, SERIAL_NUMBER.getName(), serialNumber));
        }};
        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffId, facilityId, CWC_REG_VISIT.value(), registartionDate, mrsObservations);
    }

    @Test
    public void shouldAddCareHistoriesThatAreCapturedInCareHistoryForm() {
        final LocalDate registartionDate = DateUtil.newDate(2011, 9, 1);
        List<MRSObservation> mrsObsCapturedViaHistoryForm = new ArrayList<MRSObservation>() {{
            add(new MRSObservation<MRSConcept>(registartionDate.minusMonths(2).toDate(), Concept.IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(BCG.getName())));
            add(new MRSObservation<MRSConcept>(registartionDate.minusMonths(2).toDate(), Concept.IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(YF.getName())));
            add(new MRSObservation<MRSConcept>(registartionDate.minusMonths(2).toDate(), Concept.IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(MEASLES.getName())));
            add(new MRSObservation<MRSConcept>(registartionDate.minusMonths(3).toDate(), Concept.IMMUNIZATIONS_ORDERED.getName(), new MRSConcept(VITA.getName())));
            add(new MRSObservation<Integer>(registartionDate.minusMonths(3).toDate(), Concept.IPTI.getName(), 1));
            add(new MRSObservation<Integer>(registartionDate.minusMonths(3).toDate(), Concept.PENTA.getName(), 1));
            add(new MRSObservation<Integer>(registartionDate.minusMonths(3).toDate(), Concept.OPV.getName(), 1));
            add(new MRSObservation<Integer>(registartionDate.minusMonths(3).toDate(), Concept.ROTAVIRUS.getName(), 1));
            add(new MRSObservation<Integer>(registartionDate.minusMonths(3).toDate(), Concept.PNEUMOCOCCAL.getName(), 2));
        }};

        List<CwcCareHistory> cwcCareHistories = careService.mergeNewHistoriesWithExisting(mrsObsCapturedViaHistoryForm, null);

        assertThat(cwcCareHistories, hasItem(CwcCareHistory.BCG));
        assertThat(cwcCareHistories, hasItem(CwcCareHistory.YF));
        assertThat(cwcCareHistories, hasItem(CwcCareHistory.MEASLES));
        assertThat(cwcCareHistories, hasItem(CwcCareHistory.VITA_A));
        assertThat(cwcCareHistories, hasItem(CwcCareHistory.PENTA));
        assertThat(cwcCareHistories, hasItem(CwcCareHistory.OPV));
        assertThat(cwcCareHistories, hasItem(CwcCareHistory.IPTI));
        assertThat(cwcCareHistories, hasItem(CwcCareHistory.ROTAVIRUS));
        assertThat(cwcCareHistories, hasItem(CwcCareHistory.PNEUMOCOCCAL));
    }

    @Test
    public void shouldNotAddCareHistoriesThatAreNotCapturedInCareHistoryForm() {
        List<MRSObservation> mrsObsCapturedViaHistoryForm = new ArrayList<MRSObservation>();
        List<CwcCareHistory> cwcCareHistories = careService.mergeNewHistoriesWithExisting(mrsObsCapturedViaHistoryForm, null);

        assertThat(cwcCareHistories, not(hasItem(CwcCareHistory.BCG)));
        assertThat(cwcCareHistories, not(hasItem(CwcCareHistory.YF)));
        assertThat(cwcCareHistories, not(hasItem(CwcCareHistory.MEASLES)));
    }

    @Test
    public void shouldSetRegistrationDateAsCurrentDateIfRegistrationTodayForAncRegistration() throws ObservationNotFoundException {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        final Date today = DateUtil.today().toDate();
        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.IN_PAST, today, facilityId, staffUserId, patientMotechId,
                new ArrayList<ANCCareHistory>(), DateUtil.newDate(2012, 9, 1).toDate(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(today, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<Integer>(today, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<String>(today, SERIAL_NUMBER.getName(), ancvo.getSerialNumber()));
            add(new MRSObservation<Double>(today, HEIGHT.getName(), ancvo.getHeight()));
        }};

        setupPatient(patientId, patientMotechId);
        careService.enroll(ancvo);

        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, ANC_REG_VISIT.value(), today, expectedObservations);
    }

    @Test
    public void shouldEnrollToANCWithObservationsRecordedAgainstActivePregnancy_IfTTAndIPTVaccinesTakenDuringActivePregnancyPeriod() throws Exception {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        final Date registrationDate = newDate(2012, 4, 4).toDate();


        Date estimatedDateOfDelivery = new Date();
        Date iptDate = DateUtil.newDate(estimatedDateOfDelivery).minusWeeks(10).toDate();
        Date ttDate = DateUtil.newDate(estimatedDateOfDelivery).plusWeeks(3).toDate();
        Integer tt = 4;
        Integer ipt = 3;

        String lastHbLevels = "12";
        String lastMotherVitaminA = "Blue";
        String lastIronOrFolate = "Yes";
        String lastSyphilis = "Yes";
        String lastMalaria = "Yes";
        String lastDiarrhea = "Yes";
        String lastPnuemonia = "No";
        Date lastHbDate = DateUtil.newDate(2013, 4, 29).toDate();
        Date lastMotherVitaminADate  = DateUtil.newDate(2013, 4, 29).toDate();
        Date lastIronOrFolateDate  = DateUtil.newDate(2013, 4, 29).toDate();
        Date lastSyphilisDate  = DateUtil.newDate(2013, 4, 29).toDate();
        Date lastMalariaDate  = DateUtil.newDate(2013, 4, 29).toDate();
        Date lastDiarrheaDate  = DateUtil.newDate(2013, 4, 29).toDate();
        Date lastPnuemoniaDate  = DateUtil.newDate(2013, 4, 29).toDate();
        String staffId = "staff id";

        final ANCVO ancvo = createTestANCVO(ipt.toString(), iptDate, tt.toString(), ttDate, RegistrationToday.IN_PAST, registrationDate, facilityId,
                staffUserId, patientMotechId, Arrays.asList(ANCCareHistory.values()), estimatedDateOfDelivery, lastHbLevels.toString(),lastMotherVitaminA.toString(), lastIronOrFolate.toString(),lastSyphilis.toString(), lastMalaria.toString(), lastDiarrhea.toString(), lastPnuemonia.toString(), lastHbDate, lastMotherVitaminADate, lastIronOrFolateDate, lastSyphilisDate, lastMalariaDate, lastDiarrheaDate, lastPnuemoniaDate, true);

        setupPatient(patientId, patientMotechId);
        MRSObservation<Date> eddObservation = new MRSObservation<Date>(registrationDate, EDD.getName(), ancvo.getEstimatedDateOfDelivery());
        when(mockAllObservations.findObservation(patientMotechId, EDD.getName())).thenReturn(eddObservation);

        careService.enroll(ancvo);

        final MRSObservation pregnancyObs = new MRSObservation(registrationDate, PREGNANCY.getName(), null);
        pregnancyObs.addDependantObservation(eddObservation);
        pregnancyObs.addDependantObservation(new MRSObservation<Boolean>(registrationDate, CONFINEMENT_CONFIRMED.getName(), ancvo.getDeliveryDateConfirmed()));
        pregnancyObs.addDependantObservation(new MRSObservation<Boolean>(registrationDate, PREGNANCY_STATUS.getName(), true));
        pregnancyObs.addDependantObservation(new MRSObservation<Double>(iptDate, IPT.getName(), ipt.doubleValue()));
        pregnancyObs.addDependantObservation(new MRSObservation<Double>(ttDate, TT.getName(), tt.doubleValue()));
        pregnancyObs.addDependantObservation(new MRSObservation<String>(lastHbDate, HEMOGLOBIN.getName(), lastHbLevels.toString()));
        pregnancyObs.addDependantObservation(new MRSObservation<String>(lastMotherVitaminADate, VITA.getName(), lastMotherVitaminA.toString()));
        pregnancyObs.addDependantObservation(new MRSObservation<String>(lastIronOrFolateDate, IRON_OR_FOLATE.getName(), lastIronOrFolate.toString()));
        pregnancyObs.addDependantObservation(new MRSObservation<String>(lastSyphilisDate, SYPHILIS.getName(), lastSyphilis.toString()));
        pregnancyObs.addDependantObservation(new MRSObservation<String>(lastMalariaDate, MALARIA_RAPID_TEST.getName(), lastMalaria.toString()));
        pregnancyObs.addDependantObservation(new MRSObservation<String>(lastDiarrheaDate, DIARRHEA.getName(), lastDiarrhea.toString()));
        pregnancyObs.addDependantObservation(new MRSObservation<String>(lastPnuemoniaDate, PNEUMOCOCCAL.getName(), lastPnuemonia.toString()));

        final HashSet<MRSObservation> expectedPregnancyObservations = new HashSet<MRSObservation>() {{
            add(pregnancyObs);
        }};

        final Set<MRSObservation> expectedANCObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(registrationDate, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<Double>(registrationDate, HEIGHT.getName(), ancvo.getHeight()));
            add(new MRSObservation<Integer>(registrationDate, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<String>(registrationDate, SERIAL_NUMBER.getName(), ancvo.getSerialNumber()));
            add(new MRSObservation<Double>(ancvo.getAncCareHistoryVO().getLastIPTDate(), IPT.getName(), parseDouble(ancvo.getAncCareHistoryVO().getLastIPT())));
            add(new MRSObservation<Double>(ancvo.getAncCareHistoryVO().getLastTTDate(), TT.getName(), parseDouble(ancvo.getAncCareHistoryVO().getLastTT())));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastHbDate(), HEMOGLOBIN.getName(), ancvo.getAncCareHistoryVO().getLastHbLevels()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastMotherVitaminADate(), VITA.getName(), ancvo.getAncCareHistoryVO().getLastMotherVitaminA()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastIronOrFolateDate(), IRON_OR_FOLATE.getName(), ancvo.getAncCareHistoryVO().getLastIronOrFolate()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastSyphilisDate(), SYPHILIS.getName(),ancvo.getAncCareHistoryVO().getLastSyphilis()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastMalariaDate(), MALARIA_RAPID_TEST.getName(), ancvo.getAncCareHistoryVO().getLastMalaria()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastSyphilisDate(), SYPHILIS.getName(), ancvo.getAncCareHistoryVO().getLastSyphilis()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastDiarrheaDate(), DIARRHEA.getName(), ancvo.getAncCareHistoryVO().getLastDiarrhea()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastPnuemoniaDate(), PNEUMOCOCCAL.getName(), ancvo.getAncCareHistoryVO().getLastPnuemonia()));
        }};

        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, ANC_REG_VISIT.value(), registrationDate, expectedANCObservations);
        ArgumentCaptor<Set> obsCaptor = ArgumentCaptor.forClass(Set.class);
        verify(mockAllEncounters).persistEncounter(eq(mockMRSPatient), eq(staffUserId), eq(facilityId), eq(PREG_REG_VISIT.value()), eq(registrationDate), obsCaptor.capture());
        assertReflectionEquals(expectedPregnancyObservations, obsCaptor.getValue(), ReflectionComparatorMode.LENIENT_ORDER);

    }

    @Test
    public void shouldEnrollToANCProgramIfNoVaccinesTakenDuringActivePregnancyPeriod() throws Exception {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        final Date registrationDate = new Date(2012, 3, 1);

        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.IN_PAST, registrationDate, facilityId,
                staffUserId, patientMotechId, new ArrayList<ANCCareHistory>(), new Date(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, true);

        setupPatient(patientId, patientMotechId);

        careService.enroll(ancvo);

        final MRSObservation pregnancyObs = new MRSObservation(registrationDate, PREGNANCY.getName(), null);
        pregnancyObs.addDependantObservation(new MRSObservation<Date>(registrationDate, EDD.getName(), ancvo.getEstimatedDateOfDelivery()));
        pregnancyObs.addDependantObservation(new MRSObservation<Boolean>(registrationDate, CONFINEMENT_CONFIRMED.getName(), ancvo.getDeliveryDateConfirmed()));
        pregnancyObs.addDependantObservation(new MRSObservation<Boolean>(registrationDate, PREGNANCY_STATUS.getName(), true));

        final HashSet<MRSObservation> expectedPregnancyObservations = new HashSet<MRSObservation>() {{
            add(pregnancyObs);
        }};

        final Set<MRSObservation> expectedANCObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(registrationDate, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<Double>(registrationDate, HEIGHT.getName(), ancvo.getHeight()));
            add(new MRSObservation<Integer>(registrationDate, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<String>(registrationDate, SERIAL_NUMBER.getName(), ancvo.getSerialNumber()));
        }};

        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, ANC_REG_VISIT.value(), registrationDate, expectedANCObservations);
        ArgumentCaptor<Set> obsCaptor = ArgumentCaptor.forClass(Set.class);
        verify(mockAllEncounters).persistEncounter(eq(mockMRSPatient), eq(staffUserId), eq(facilityId), eq(PREG_REG_VISIT.value()), eq(registrationDate), obsCaptor.capture());
        assertEquals(expectedPregnancyObservations, obsCaptor.getValue());

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
        List<PatientCare> patientCares = asList(new PatientCare(PNC_MOTHER_1.getName(), registrationTime, registrationTime, null, new HashMap<String, String>()));

        careService.enrollPatientCares(patientCares, patient);

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllCareSchedules).enroll(enrollmentRequestCaptor.capture());
        EnrollmentRequest request = enrollmentRequestCaptor.getValue();
        assertThat(request.getScheduleName(), is(PNC_MOTHER_1.getName()));
        assertThat(request.getExternalId(), is(patient.getMRSPatientId()));
    }

    @Test
    public void shoulUpdateEddObservationIfFound() throws Exception {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        final Date registrationDate = new Date(2012, 3, 1);


        Date estimatedDateOfDelivery = new Date();
        final ANCVO ancvo = createTestANCVO("3", new Date(2011, 12, 9), "4", new Date(2011, 7, 5), RegistrationToday.IN_PAST, registrationDate, facilityId,
                staffUserId, patientMotechId, Arrays.asList(ANCCareHistory.values()), estimatedDateOfDelivery, null, null, null, null, null, null, null, null, null, null, null, null, null, null, true);

        setupPatient(patientId, patientMotechId);

        final MRSObservation activePregnancy = new MRSObservation<Object>(registrationDate, PREGNANCY.getName(), null);

        activePregnancy.addDependantObservation(new MRSObservation<Date>(registrationDate, EDD.getName(), estimatedDateOfDelivery));
        activePregnancy.addDependantObservation(new MRSObservation<Boolean>(registrationDate, CONFINEMENT_CONFIRMED.getName(), ancvo.getDeliveryDateConfirmed()));
        activePregnancy.addDependantObservation(new MRSObservation<Boolean>(registrationDate, PREGNANCY_STATUS.getName(), true));

        final Set<MRSObservation> updatedEddObservations = new HashSet<MRSObservation>();
        updatedEddObservations.add(activePregnancy);

        when(mockAllObservations.updateEDD(ancvo.getEstimatedDateOfDelivery(), mockPatient,
                ancvo.getStaffId(), ancvo.getRegistrationDate())).thenReturn(updatedEddObservations);

        careService.enroll(ancvo);

        HashSet<MRSObservation> expectedANCObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(registrationDate, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<Double>(registrationDate, HEIGHT.getName(), ancvo.getHeight()));
            add(new MRSObservation<Integer>(registrationDate, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<String>(registrationDate, SERIAL_NUMBER.getName(), ancvo.getSerialNumber()));
            add(new MRSObservation<Double>(ancvo.getAncCareHistoryVO().getLastIPTDate(), IPT.getName(), Double.valueOf(ancvo.getAncCareHistoryVO().getLastIPT())));
            add(new MRSObservation<Double>(ancvo.getAncCareHistoryVO().getLastTTDate(), TT.getName(), Double.valueOf(ancvo.getAncCareHistoryVO().getLastTT())));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastHbDate(), HEMOGLOBIN.getName(), ancvo.getAncCareHistoryVO().getLastHbLevels()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastMotherVitaminADate(), VITA.getName(), ancvo.getAncCareHistoryVO().getLastMotherVitaminA()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastIronOrFolateDate(), IRON_OR_FOLATE.getName(), ancvo.getAncCareHistoryVO().getLastIronOrFolate()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastSyphilisDate(), SYPHILIS.getName(),ancvo.getAncCareHistoryVO().getLastSyphilis()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastMalariaDate(), MALARIA_RAPID_TEST.getName(), ancvo.getAncCareHistoryVO().getLastMalaria()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastSyphilisDate(), SYPHILIS.getName(), ancvo.getAncCareHistoryVO().getLastSyphilis()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastDiarrheaDate(), DIARRHEA.getName(), ancvo.getAncCareHistoryVO().getLastDiarrhea()));
            add(new MRSObservation<String>(ancvo.getAncCareHistoryVO().getLastPnuemoniaDate(), PNEUMOCOCCAL.getName(), ancvo.getAncCareHistoryVO().getLastPnuemonia()));
        }};

        ArgumentCaptor<Set> observationCaptor = ArgumentCaptor.forClass(Set.class);
        ArgumentCaptor<String> encounterTypeCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockAllEncounters, times(2)).persistEncounter(eq(mockMRSPatient), eq(staffUserId), eq(facilityId), encounterTypeCaptor.capture(), eq(registrationDate), observationCaptor.capture());

        assertEquals(expectedANCObservations, observationCaptor.getAllValues().get(0));
        assertReflectionEquals(updatedEddObservations, observationCaptor.getAllValues().get(1), ReflectionComparatorMode.LENIENT_ORDER);

        assertEquals(ANC_REG_VISIT.value(), encounterTypeCaptor.getAllValues().get(0));
        assertEquals(PREG_REG_VISIT.value(), encounterTypeCaptor.getAllValues().get(1));
    }

    @Test
    public void shouldNotAddObsIfValueNotGiven() throws ObservationNotFoundException {
        String facilityId = "facility id";
        String patientId = "patient id";
        String patientMotechId = "patient motech id";
        String staffUserId = "staff user id";
        final Date registrationDate = new Date(2012, 1, 1);
        final ANCVO ancvo = createTestANCVO(null, null, null, null, RegistrationToday.IN_PAST_IN_OTHER_FACILITY, registrationDate, facilityId, staffUserId,
                patientMotechId, new ArrayList<ANCCareHistory>(), new Date(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        setupPatient(patientId, patientMotechId);

        careService.enroll(ancvo);

        final HashSet<MRSObservation> expectedANCObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Integer>(registrationDate, GRAVIDA.getName(), ancvo.getGravida()));
            add(new MRSObservation<Double>(registrationDate, HEIGHT.getName(), ancvo.getHeight()));
            add(new MRSObservation<Integer>(registrationDate, PARITY.getName(), ancvo.getParity()));
            add(new MRSObservation<String>(registrationDate, SERIAL_NUMBER.getName(), ancvo.getSerialNumber()));
        }};

        final MRSObservation pregnancyObs = new MRSObservation(registrationDate, PREGNANCY.getName(), null);
        pregnancyObs.addDependantObservation(new MRSObservation<Date>(registrationDate, EDD.getName(), ancvo.getEstimatedDateOfDelivery()));
        pregnancyObs.addDependantObservation(new MRSObservation<Boolean>(registrationDate, CONFINEMENT_CONFIRMED.getName(), ancvo.getDeliveryDateConfirmed()));
        pregnancyObs.addDependantObservation(new MRSObservation<Boolean>(registrationDate, PREGNANCY_STATUS.getName(), true));

        final HashSet<MRSObservation> expectedPregnancyObservations = new HashSet<MRSObservation>() {{
            add(pregnancyObs);
        }};

        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, ANC_REG_VISIT.value(), registrationDate, expectedANCObservations);
        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffUserId, facilityId, PREG_REG_VISIT.value(), registrationDate, expectedPregnancyObservations);
    }

    @Test
    public void shouldSaveEncounterForCareHistoryDetails() throws ObservationNotFoundException {
        CareService careServiceSpy = spy(careService);

        ANCCareHistoryVO ancCareHistory = new ANCCareHistoryVO(null, new ArrayList<ANCCareHistory>(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        CWCCareHistoryVO cwcCareHistory = new CWCCareHistoryVO();

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
        MRSEncounter mrsEncounter;
        Set<MRSObservation> pregnancyObservations = new HashSet<MRSObservation>();
        MRSObservation<Boolean> pregObs = new MRSObservation<Boolean>(new Date(), PREGNANCY.getName(), Boolean.TRUE);
        pregObs.addDependantObservation(new MRSObservation(new Date(), EDD.getName(), DateUtil.newDate(2012, 12, 1).toDate()));
        pregnancyObservations.add(pregObs);
        mrsEncounter = new MRSEncounter("providerId", "creator", "facility", new Date(), patientId, pregnancyObservations, EncounterType.PREG_REG_VISIT.value());

        when(mockAllEncounters.getLatest(patientMotechId, EncounterType.PREG_REG_VISIT.value())).thenReturn(mrsEncounter);
        CareHistoryVO careHistory = new CareHistoryVO(staffId, facilityId, patientMotechId, date, ancCareHistory, cwcCareHistory);

        careServiceSpy.addCareHistory(careHistory);

        final HashSet<MRSObservation> expectedObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<String>(ancRegDate, conceptOneName, conceptOneValue));
            add(new MRSObservation<String>(cwcRegDate, conceptTwoName, conceptTwoValue));
        }};
        verify(mockAllEncounters).persistEncounter(mockMRSPatient, staffId, facilityId, PATIENT_HISTORY.value(), date, expectedObservations);
    }

    @Test
    public void shouldSaveCareHistoryDetailsWithTTandIPTIfThereIsAnActivePregnancyAndCreateCareScheduleIfNotAlreadyExists() throws ObservationNotFoundException {
        final TTVaccineDosage ttDose = TTVaccineDosage.byValue(Integer.parseInt("1"));
        final IPTDose iptDose = IPTDose.byValue("2");
        final Date ttDate = DateUtil.newDate(2011, 12, 23).toDate();
        final Date iptDate = DateUtil.newDate(2011, 1, 2).toDate();

        final String lastHbLevels = "12";
        final String lastMotherVitaminA = "Blue";
        final String lastIronOrFolate = "Yes";
        final String lastSyphilis = "Yes";
        final String lastMalaria = "Yes";
        final String lastDiarrhea = "Yes";
        final String lastPnuemonia = "No";
        final Date lastHbDate = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastMotherVitaminADate  = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastIronOrFolateDate  = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastSyphilisDate  = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastMalariaDate  = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastDiarrheaDate  = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastPnuemoniaDate  = DateUtil.newDate(2013, 4, 29).toDate();

        String staffId = "staff id";
        String facilityId = "facility id";
        String patientMotechId = "patient motech id";
        String patientId = "patient id";
        LocalDate careHistoryCapturedDate = DateUtil.newDate(2011, 11, 11);
        LocalDate ancRegDate = careHistoryCapturedDate.minusWeeks(1);
        LocalDate edd = careHistoryCapturedDate.plusMonths(9);

        ANCCareHistoryVO ancCareHistory = new ANCCareHistoryVO(true, Arrays.asList(ANCCareHistory.values()), iptDose.value().toString(), ttDose.getDosage().toString(), null, null, null, null, null, null, null, iptDate, ttDate, null, null, null, null, null, null, null);

        setupPatient(patientId, patientMotechId);
        CareHistoryVO careHistory = new CareHistoryVO(staffId, facilityId, patientMotechId, careHistoryCapturedDate.toDate(), ancCareHistory, new CWCCareHistoryVO(false, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));

        MRSObservation<Date> eddObservation = new MRSObservation<Date>(ancRegDate.toDate(), EDD.getName(), edd.toDate());
        final MRSObservation activePregnancyObservation = new MRSObservation<Boolean>(ancRegDate.toDate(), PREGNANCY.getName(), true);
        activePregnancyObservation.addDependantObservation(eddObservation);
        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(ancRegDate.toDate(), PREGNANCY_STATUS.getName(), true));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(ancRegDate.toDate(), CONFINEMENT_CONFIRMED.getName(), true));

        when(mockAllObservations.findLatestObservation(patientMotechId, PREGNANCY.getName())).thenReturn(activePregnancyObservation);
        when(mockAllObservations.findObservation(patientMotechId, EDD.getName())).thenReturn(eddObservation);

        careService.addCareHistory(careHistory);

        final Set<MRSObservation> expectedHistoryObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Double>(ttDate, TT.getName(), ttDose.getDosageAsDouble()));
            add(new MRSObservation<Double>(iptDate, IPT.getName(), parseDouble(iptDose.value().toString())));
            add(new MRSObservation<String>(lastHbDate, HEMOGLOBIN.getName(), lastHbLevels.toString()));
            add(new MRSObservation<String>(lastMotherVitaminADate, VITA.getName(), lastMotherVitaminA.toString()));
            add(new MRSObservation<String>(lastIronOrFolateDate, IRON_OR_FOLATE.getName(), lastIronOrFolate.toString()));
            add(new MRSObservation<String>(lastSyphilisDate, SYPHILIS.getName(), lastSyphilis.toString()));
            add(new MRSObservation<String>(lastMalariaDate, MALARIA_RAPID_TEST.getName(), lastMalaria.toString()));
            add(new MRSObservation<String>(lastDiarrheaDate, DIARRHEA.getName(), lastDiarrhea.toString()));
            add(new MRSObservation<String>(lastPnuemoniaDate, PNEUMOCOCCAL.getName(), lastPnuemonia.toString()));

        }};

        ArgumentCaptor<Set> observationCaptor = ArgumentCaptor.forClass(Set.class);
        ArgumentCaptor<String> encounterTypeCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockAllEncounters, times(2)).persistEncounter(eq(mockMRSPatient), eq(staffId), eq(facilityId), encounterTypeCaptor.capture(), eq(careHistoryCapturedDate.toDate()), observationCaptor.capture());

        activePregnancyObservation.addDependantObservation(new MRSObservation<Double>(ttDate, TT.getName(), parseDouble(ttDose.getDosage().toString())));
        HashSet<MRSObservation> expectedActivePregnancyObs = new HashSet<MRSObservation>() {{
            add(activePregnancyObservation);
        }};

        assertEquals(expectedActivePregnancyObs, observationCaptor.getAllValues().get(0));
        assertEquals(expectedHistoryObservations, observationCaptor.getAllValues().get(1));

        assertEquals(ANC_VISIT.value(), encounterTypeCaptor.getAllValues().get(0));
        assertEquals(PATIENT_HISTORY.value(), encounterTypeCaptor.getAllValues().get(1));
        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);

        verify(mockAllCareSchedules, times(1)).enroll(enrollmentRequestCaptor.capture());
        List<EnrollmentRequest> allValues = enrollmentRequestCaptor.getAllValues();
        TTVaccineDosage nextTTDose = Utility.getNextOf(ttDose);
        PatientCare expectedTTCare = PatientCare.forEnrollmentInBetweenProgram(TT_VACCINATION.getName(), newDate(ttDate), nextTTDose.getScheduleMilestoneName(), PatientTest.facilityMetaData(facilityId));
        assertEnrollmentRequests(allValues, asList(expectedRequest(patientId, expectedTTCare)));
    }

    @Test
    public void shouldNotCreateScheduleForCareHistoryIfNotActivePregancyObservationExists() throws ObservationNotFoundException {

        final TTVaccineDosage ttDose = TTVaccineDosage.byValue(Integer.parseInt("1"));
        final IPTDose iptDose = IPTDose.byValue("2");
        final Date ttDate = DateUtil.newDate(2011, 12, 23).toDate();
        final Date iptDate = DateUtil.newDate(2011, 1, 2).toDate();
        String patientId = "patient id";
        String patientMotechId = "patientMotechId";
        ANCCareHistoryVO ancCareHistory = new ANCCareHistoryVO(true, Arrays.asList(ANCCareHistory.values()), iptDose.value().toString(), ttDose.getDosage().toString(), null, null, null, null, null, null, null, iptDate, ttDate, null, null, null, null, null, null, null);

        setupPatient(patientId, patientMotechId);
        when(mockAllObservations.findLatestObservation(patientMotechId, Concept.PREGNANCY.getName())).thenReturn(null);

        careService.addCareHistory(new CareHistoryVO("staffId", "facilityId", patientMotechId, newDate(2012, 2, 2).toDate(),
                ancCareHistory, new CWCCareHistoryVO(false, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)));

        verify(mockAllCareSchedules, never()).enrollIfNotActive((EnrollmentRequest) any());
    }

    @Test
    public void shouldNotSaveCareHistoryDetailsWithTTandIPTIfOutsideRangeOfActivePregnancy() throws ObservationNotFoundException {
        final String ttDose = "1";
        final String iptDose = "2";
        final Date ttDate = DateUtil.newDate(2010, 12, 23).toDate();
        final Date iptDate = DateUtil.newDate(2011, 1, 2).toDate();
        String staffId = "staff id";
        String facilityId = "facility id";
        String patientMotechId = "patient motech id";
        String patientId = "patient id";
        LocalDate careHistoryCapturedDate = DateUtil.newDate(2011, 11, 11);
        LocalDate ancRegDate = careHistoryCapturedDate.minusWeeks(1);
        LocalDate edd = careHistoryCapturedDate.plusMonths(9);

        ANCCareHistoryVO ancCareHistory = new ANCCareHistoryVO(true, Arrays.asList(ANCCareHistory.values()), iptDose, ttDose, null, null, null, null, null, null, null, iptDate, ttDate, null, null, null, null, null, null, null);

        setupPatient(patientId, patientMotechId);
        CareHistoryVO careHistory = new CareHistoryVO(staffId, facilityId, patientMotechId, careHistoryCapturedDate.toDate(), ancCareHistory, new CWCCareHistoryVO(false, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));

        final MRSObservation activePregnancyObservation = new MRSObservation<Boolean>(ancRegDate.toDate(), PREGNANCY.getName(), true);
        activePregnancyObservation.addDependantObservation(new MRSObservation<Date>(ancRegDate.toDate(), EDD.getName(), edd.toDate()));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(ancRegDate.toDate(), CONFINEMENT_CONFIRMED.getName(), true));

        when(mockAllObservations.findLatestObservation(patientMotechId, Concept.PREGNANCY.getName())).thenReturn(activePregnancyObservation);

        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(ancRegDate.toDate(), PREGNANCY_STATUS.getName(), true));
        careService.addCareHistory(careHistory);

        final Set<MRSObservation> expectedHistoryObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Double>(ttDate, TT.getName(), Double.parseDouble(ttDose)));
            add(new MRSObservation<Double>(iptDate, IPT.getName(), Double.parseDouble(iptDose)));
        }};

        ArgumentCaptor<Set> observationCaptor = ArgumentCaptor.forClass(Set.class);
        ArgumentCaptor<String> encounterTypeCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockAllEncounters).persistEncounter(eq(mockMRSPatient), eq(staffId), eq(facilityId), encounterTypeCaptor.capture(), eq(careHistoryCapturedDate.toDate()), observationCaptor.capture());

        assertEquals(expectedHistoryObservations, observationCaptor.getAllValues().get(0));

        List<String> encounters = encounterTypeCaptor.getAllValues();
        assertEquals(PATIENT_HISTORY.value(), encounters.get(0));

        assertThat(encounters, not(hasItem(ANC_VISIT.value())));
    }

    @Test
    public void shouldSaveCareHistoryDetailsWithTTOrIPTIfAnyOneIsWithinTheRangeOfActivePregnancy() throws ObservationNotFoundException {
        final String ttDose = "1";
        final String iptDose = "2";
        final String lastHbLevels = "12";
        final String lastMotherVitaminA = "Blue";
        final String lastIronOrFolate = "Yes";
        final String lastSyphilis = "Yes";
        final String lastMalaria = "Yes";
        final String lastDiarrhea = "Yes";
        final String lastPnuemonia = "No";
        final Date ttDate = DateUtil.newDate(2011, 12, 23).toDate();
        final Date iptDate = DateUtil.newDate(2011, 1, 2).toDate();
        final Date lastHbDate = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastMotherVitaminADate  = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastIronOrFolateDate  = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastSyphilisDate  = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastMalariaDate  = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastDiarrheaDate  = DateUtil.newDate(2013, 4, 29).toDate();
        final Date lastPnuemoniaDate  = DateUtil.newDate(2013, 4, 29).toDate();
        String staffId = "staff id";
        String facilityId = "facility id";
        String patientMotechId = "patient motech id";
        String patientId = "patient id";
        LocalDate careHistoryCapturedDate = DateUtil.newDate(2011, 11, 11);
        LocalDate ancRegDate = careHistoryCapturedDate.minusWeeks(1);
        LocalDate edd = careHistoryCapturedDate.plusMonths(9);

        ANCCareHistoryVO ancCareHistory = new ANCCareHistoryVO(true, Arrays.asList(ANCCareHistory.values()), iptDose, ttDose, lastHbLevels, lastMotherVitaminA, lastIronOrFolate, lastSyphilis, lastMalaria, lastDiarrhea, lastPnuemonia,
                iptDate,  ttDate, lastHbDate, lastMotherVitaminADate, lastIronOrFolateDate, lastSyphilisDate, lastMalariaDate, lastDiarrheaDate, lastPnuemoniaDate );

        setupPatient(patientId, patientMotechId);
        CareHistoryVO careHistory = new CareHistoryVO(staffId, facilityId, patientMotechId, careHistoryCapturedDate.toDate(), ancCareHistory, new CWCCareHistoryVO(false, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));

        final MRSObservation activePregnancyObservation = new MRSObservation<Boolean>(ancRegDate.toDate(), PREGNANCY.getName(), true);
        MRSObservation<Date> eddObservation = new MRSObservation<Date>(ancRegDate.toDate(), EDD.getName(), edd.toDate());
        activePregnancyObservation.addDependantObservation(eddObservation);
        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(ancRegDate.toDate(), PREGNANCY_STATUS.getName(), true));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(ancRegDate.toDate(), CONFINEMENT_CONFIRMED.getName(), true));

        when(mockAllObservations.findLatestObservation(patientMotechId, PREGNANCY.getName())).thenReturn(activePregnancyObservation);
        when(mockAllObservations.findObservation(patientMotechId, EDD.getName())).thenReturn(eddObservation);


        careService.addCareHistory(careHistory);

        activePregnancyObservation.addDependantObservation(new MRSObservation<String>(ancRegDate.toDate(), TT.getName(), ttDose));

        final Set<MRSObservation> expectedHistoryObservations = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Double>(ttDate, TT.getName(), Double.parseDouble(ttDose)));
            add(new MRSObservation<Double>(iptDate, IPT.getName(), Double.parseDouble(iptDose)));
            add(new MRSObservation<String>(lastHbDate, HEMOGLOBIN.getName(), lastHbLevels.toString()));
            add(new MRSObservation<String>(lastMotherVitaminADate, VITA.getName(), lastMotherVitaminA.toString()));
            add(new MRSObservation<String>(lastIronOrFolateDate, IRON_OR_FOLATE.getName(), lastIronOrFolate.toString()));
            add(new MRSObservation<String>(lastSyphilisDate, SYPHILIS.getName(), lastSyphilis.toString()));
            add(new MRSObservation<String>(lastMalariaDate, MALARIA_RAPID_TEST.getName(), lastMalaria.toString()));
            add(new MRSObservation<String>(lastDiarrheaDate, DIARRHEA.getName(), lastDiarrhea.toString()));
            add(new MRSObservation<String>(lastPnuemoniaDate, PNEUMOCOCCAL.getName(), lastPnuemonia.toString()));

        }};

        ArgumentCaptor<Set> observationCaptor = ArgumentCaptor.forClass(Set.class);
        ArgumentCaptor<String> encounterTypeCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockAllEncounters, times(2)).persistEncounter(eq(mockMRSPatient), eq(staffId), eq(facilityId), encounterTypeCaptor.capture(), eq(careHistoryCapturedDate.toDate()), observationCaptor.capture());

        HashSet<MRSObservation> expectedActivePregnancyObs = new HashSet<MRSObservation>() {{
            add(activePregnancyObservation);
        }};

        assertEquals(expectedActivePregnancyObs, observationCaptor.getAllValues().get(0));
        assertEquals(expectedHistoryObservations, observationCaptor.getAllValues().get(1));

        assertEquals(ANC_VISIT.value(), encounterTypeCaptor.getAllValues().get(0));
        assertEquals(PATIENT_HISTORY.value(), encounterTypeCaptor.getAllValues().get(1));
    }

    @Test
    public void shouldCreateSchedulesForANCProgramRegistration() throws ObservationNotFoundException {
        String patientId = "Id", patientMotechId = "motechId";
        LocalDate registrationDate = newDate(2000, 1, 1);
        mockCurrentDate(registrationDate);
        LocalDate dateOfConception = registrationDate.minusMonths(4);
        Pregnancy pregnancy = basedOnConceptionDate(dateOfConception);
        final String ttDose = "1";
        final String iptDose = "2";
        final String lastHbLevels = "12";
        final String lastMotherVitaminA = "Blue";
        final String lastIronOrFolate = "Yes";
        final String lastSyphilis = "Yes";
        final String lastMalaria = "Yes";
        final String lastDiarrhea = "Yes";
        final String lastPnuemonia = "No";
        final Date ttDate = dateOfConception.plusMonths(1).toDate();
        final Date iptDate = dateOfConception.plusMonths(2).toDate();
        final Date lastHbDate = dateOfConception.plusMonths(2).toDate();
        final Date lastMotherVitaminADate  = dateOfConception.plusMonths(2).toDate();
        final Date lastIronOrFolateDate  = dateOfConception.plusMonths(2).toDate();
        final Date lastSyphilisDate  = dateOfConception.plusMonths(2).toDate();
        final Date lastMalariaDate  = dateOfConception.plusMonths(2).toDate();
        final Date lastDiarrheaDate  = dateOfConception.plusMonths(2).toDate();
        final Date lastPnuemoniaDate  = dateOfConception.plusMonths(2).toDate();
        CareService careServiceSpy = spy(careService);

        setupPatient(patientId, patientMotechId);
        final ANCVO ancvo = createTestANCVO(iptDose, iptDate, ttDose, ttDate, RegistrationToday.IN_PAST, registrationDate.toDate(), "facilityId", null,
                patientMotechId, Arrays.asList(ANCCareHistory.values()), pregnancy.dateOfDelivery().toDate(), lastHbLevels, lastMotherVitaminA, lastIronOrFolate, lastSyphilis, lastMalaria,
                lastDiarrhea, lastPnuemonia, lastHbDate, lastMotherVitaminADate, lastIronOrFolateDate, lastSyphilisDate, lastMalariaDate, lastDiarrheaDate, lastPnuemoniaDate, true );

        final MRSObservation activePregnancyObservation = new MRSObservation<Boolean>(registrationDate.toDate(), PREGNANCY.getName(), true);
        activePregnancyObservation.addDependantObservation(new MRSObservation<Date>(registrationDate.toDate(), EDD.getName(), pregnancy.dateOfDelivery().toDate()));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(registrationDate.toDate(), PREGNANCY_STATUS.getName(), true));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(registrationDate.toDate(), CONFINEMENT_CONFIRMED.getName(), true));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Double>(ttDate, TT.getName(), parseDouble(ttDose)));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Double>(iptDate, IPT.getName(), parseDouble(iptDose)));
        Set<MRSObservation> pregnancyObs = new HashSet<MRSObservation>() {{
            add(activePregnancyObservation);
        }};

        doReturn(pregnancyObs).when(careServiceSpy).registerPregnancy(ancvo, mockPatient);

        careServiceSpy.enroll(ancvo);

        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllCareSchedules, times(3)).enroll(requestCaptor.capture());
        List<EnrollmentRequest> requests = requestCaptor.getAllValues();
        assertScheduleEnrollmentRequest(requests.get(0), expectedRequest(patientId, PatientCare.forEnrollmentFromStart(ScheduleNames.ANC_DELIVERY.getName(), dateOfConception, mockPatient.facilityMetaData())));
        assertScheduleEnrollmentRequest(requests.get(1), expectedRequest(patientId, PatientCare.forEnrollmentInBetweenProgram(ScheduleNames.TT_VACCINATION.getName(), newDate(ttDate), TTVaccineDosage.TT2.getScheduleMilestoneName(), mockPatient.facilityMetaData())));
        assertScheduleEnrollmentRequest(requests.get(2), expectedRequest(patientId, PatientCare.forEnrollmentInBetweenProgram(ScheduleNames.ANC_IPT_VACCINE.getName(), newDate(iptDate), IPTDose.SP3.milestone(), mockPatient.facilityMetaData())));
    }

    @Test
    public void shouldCreateSchedulesForANCProgramRegistrationWhenDueDateForHistoryIsInPast() throws ObservationNotFoundException {
        String patientId = "Id", patientMotechId = "motechId";
        LocalDate registrationDate = newDate(2012, 3, 1);
        mockCurrentDate(registrationDate);
        LocalDate dateOfConception = registrationDate.minusMonths(6);
        Pregnancy pregnancy = basedOnConceptionDate(dateOfConception);
        final String ttDose = "1";
        final String iptDose = "2";
        final String lastHbLevels = "12";
        final String lastMotherVitaminA = "Blue";
        final String lastIronOrFolate = "Yes";
        final String lastSyphilis = "Yes";
        final String lastMalaria = "Yes";
        final String lastDiarrhea = "Yes";
        final String lastPnuemonia = "No";
        final Date ttDate = dateOfConception.plusMonths(1).toDate();
        final Date iptDate = dateOfConception.plusMonths(2).toDate();
        final Date lastHbDate = dateOfConception.plusMonths(2).toDate();
        final Date lastMotherVitaminADate  = dateOfConception.plusMonths(2).toDate();
        final Date lastIronOrFolateDate  = dateOfConception.plusMonths(2).toDate();
        final Date lastSyphilisDate  = dateOfConception.plusMonths(2).toDate();
        final Date lastMalariaDate  = dateOfConception.plusMonths(2).toDate();
        final Date lastDiarrheaDate  = dateOfConception.plusMonths(2).toDate();
        final Date lastPnuemoniaDate  = dateOfConception.plusMonths(2).toDate();
        CareService careServiceSpy = spy(careService);

        setupPatient(patientId, patientMotechId);
        final ANCVO ancvo = createTestANCVO(iptDose, iptDate, ttDose, ttDate, RegistrationToday.IN_PAST, registrationDate.toDate(), "facilityId", null,
                patientMotechId, Arrays.asList(ANCCareHistory.values()), pregnancy.dateOfDelivery().toDate(), lastHbLevels, lastMotherVitaminA, lastIronOrFolate, lastSyphilis, lastMalaria,
                lastDiarrhea, lastPnuemonia, lastHbDate, lastMotherVitaminADate, lastIronOrFolateDate, lastSyphilisDate, lastMalariaDate, lastDiarrheaDate, lastPnuemoniaDate, true);

        final MRSObservation activePregnancyObservation = new MRSObservation<Boolean>(registrationDate.toDate(), PREGNANCY.getName(), true);
        activePregnancyObservation.addDependantObservation(new MRSObservation<Date>(registrationDate.toDate(), EDD.getName(), pregnancy.dateOfDelivery().toDate()));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(registrationDate.toDate(), PREGNANCY_STATUS.getName(), true));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(registrationDate.toDate(), CONFINEMENT_CONFIRMED.getName(), true));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Double>(ttDate, TT.getName(), parseDouble(ttDose)));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Double>(iptDate, IPT.getName(), parseDouble(iptDose)));
        Set<MRSObservation> pregnancyObs = new HashSet<MRSObservation>() {{
            add(activePregnancyObservation);
        }};

        doReturn(pregnancyObs).when(careServiceSpy).registerPregnancy(ancvo, mockPatient);

        when(mockAllCareSchedules.getDueWindowAlertTimings(Matchers.<EnrollmentRequest>any())).thenReturn(Arrays.<DateTime>asList(newDateTime(dateOfConception.plusWeeks(10))));
        careServiceSpy.enroll(ancvo);

        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllCareSchedules, times(3)).enroll(requestCaptor.capture());
        List<EnrollmentRequest> requests = requestCaptor.getAllValues();
        assertScheduleEnrollmentRequest(requests.get(0), expectedRequest(patientId, PatientCare.forEnrollmentFromStart(ScheduleNames.ANC_DELIVERY.getName(), dateOfConception, mockPatient.facilityMetaData())));
        assertScheduleEnrollmentRequest(requests.get(1), expectedRequest(patientId, PatientCare.forEnrollmentInBetweenProgram(ScheduleNames.TT_VACCINATION.getName(), newDate(2012, 1, 23), TTVaccineDosage.TT2.getScheduleMilestoneName(), mockPatient.facilityMetaData())));
        assertScheduleEnrollmentRequest(requests.get(2), expectedRequest(patientId, PatientCare.forEnrollmentInBetweenProgram(ScheduleNames.ANC_IPT_VACCINE.getName(), newDate(2012, 2, 21), IPTDose.SP3.milestone(), mockPatient.facilityMetaData())));
    }

    @Test
    public void shouldCreateSchedulesForANCProgramRegistrationFromStartWhenDueDateForHistoryIsIrrelevantToCurrentPregnancy() throws ObservationNotFoundException {
        String patientId = "Id", patientMotechId = "motechId";
        LocalDate registrationDate = newDate(2012, 3, 1);
        mockCurrentDate(registrationDate);
        LocalDate dateOfConception = registrationDate.minusMonths(3);
        Pregnancy pregnancy = basedOnConceptionDate(dateOfConception);
        final String ttDose = "1";
        final String iptDose = "2";
        final String lastHbLevels = "12";
        final String lastMotherVitaminA = "Blue";
        final String lastIronOrFolate = "Yes";
        final String lastSyphilis = "Yes";
        final String lastMalaria = "Yes";
        final String lastDiarrhea = "Yes";
        final String lastPnuemonia = "No";
        final Date ttDate = dateOfConception.minusWeeks(1).toDate();
        final Date iptDate = dateOfConception.minusDays(3).toDate();
        final Date lastHbDate = dateOfConception.minusWeeks(1).toDate();
        final Date lastMotherVitaminADate  = dateOfConception.minusWeeks(1).toDate();
        final Date lastIronOrFolateDate  = dateOfConception.minusWeeks(1).toDate();
        final Date lastSyphilisDate  = dateOfConception.minusWeeks(1).toDate();
        final Date lastMalariaDate  = dateOfConception.minusWeeks(1).toDate();
        final Date lastDiarrheaDate  = dateOfConception.minusWeeks(1).toDate();
        final Date lastPnuemoniaDate  = dateOfConception.minusWeeks(1).toDate();
        CareService careServiceSpy = spy(careService);

        setupPatient(patientId, patientMotechId);
        final ANCVO ancvo = createTestANCVO(iptDose, iptDate, ttDose, ttDate, RegistrationToday.IN_PAST, registrationDate.toDate(), "facilityId", null,
                patientMotechId, Arrays.asList(ANCCareHistory.values()), pregnancy.dateOfDelivery().toDate(), lastHbLevels, lastMotherVitaminA, lastIronOrFolate, lastSyphilis, lastMalaria,
                lastDiarrhea, lastPnuemonia, lastHbDate, lastMotherVitaminADate, lastIronOrFolateDate, lastSyphilisDate, lastMalariaDate, lastDiarrheaDate, lastPnuemoniaDate, true);

        final MRSObservation activePregnancyObservation = new MRSObservation<Boolean>(registrationDate.toDate(), PREGNANCY.getName(), true);
        activePregnancyObservation.addDependantObservation(new MRSObservation<Date>(registrationDate.toDate(), EDD.getName(), pregnancy.dateOfDelivery().toDate()));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(registrationDate.toDate(), PREGNANCY_STATUS.getName(), true));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Boolean>(registrationDate.toDate(), CONFINEMENT_CONFIRMED.getName(), true));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Double>(ttDate, TT.getName(), parseDouble(ttDose)));
        activePregnancyObservation.addDependantObservation(new MRSObservation<Double>(iptDate, IPT.getName(), parseDouble(iptDose)));
        Set<MRSObservation> pregnancyObs = new HashSet<MRSObservation>() {{
            add(activePregnancyObservation);
        }};

        doReturn(pregnancyObs).when(careServiceSpy).registerPregnancy(ancvo, mockPatient);

        when(mockAllCareSchedules.getDueWindowAlertTimings(Matchers.<EnrollmentRequest>any())).thenReturn(Arrays.<DateTime>asList(newDateTime(dateOfConception.plusWeeks(10))));
        careServiceSpy.enroll(ancvo);

        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllCareSchedules, atLeastOnce()).enroll(requestCaptor.capture());
        List<EnrollmentRequest> requests = requestCaptor.getAllValues();
        assertScheduleEnrollmentRequest(requests.get(0), expectedRequest(patientId, PatientCare.forEnrollmentFromStart(ScheduleNames.ANC_DELIVERY.getName(), dateOfConception, mockPatient.facilityMetaData())));
        assertScheduleEnrollmentRequest(requests.get(1), expectedRequest(patientId, PatientCare.forEnrollmentFromStart(ScheduleNames.TT_VACCINATION.getName(), registrationDate, mockPatient.facilityMetaData())));
        assertScheduleEnrollmentRequest(requests.get(2), expectedRequest(patientId, PatientCare.forEnrollmentFromStart(ScheduleNames.ANC_IPT_VACCINE.getName(), dateOfConception, mockPatient.facilityMetaData())));
    }

    @Test
    public void shouldGetActiveCareSchedulesForAPatient() {
        String patientId = "patientId";
        setupPatient(patientId, "patientMotechId");
        when(mockAllCareSchedules.getActiveEnrollment(patientId, TT_VACCINATION.getName())).thenReturn(null);
        assertThat(careService.activeCareSchedules(mockPatient, Arrays.asList(TT_VACCINATION.getName())).hasActiveTTSchedule(), is(equalTo(false)));

        EnrollmentRecord enrollmentRecord = mock(EnrollmentRecord.class);
        when(mockAllCareSchedules.getActiveEnrollment(patientId, TT_VACCINATION.getName())).thenReturn(enrollmentRecord);
        assertThat(careService.activeCareSchedules(mockPatient, Arrays.asList(TT_VACCINATION.getName())).hasActiveTTSchedule(), is(equalTo(true)));
    }

    @Test
    public void shouldCreateSchedulesForCWCPentaProgramRegistration() {
        String patientId = "Id";
        String patientMotechId = "motechId";
        DateTime registrationDateTime = newDateTime(2012, 12, 2, 0, 0, 0);

        setupPatient(patientId, patientMotechId);
        when(mockPatient.getMotechId()).thenReturn(patientMotechId);

        PatientCare patientCare = new PatientCare(CWC_PENTA.getName(), new LocalDate(), registrationDateTime.toLocalDate(), null, null);

        CwcVO cwcVO = new CwcVO(null, null, registrationDateTime.toDate(), patientMotechId, asList(CwcCareHistory.PENTA), null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, true);
        ActiveCareSchedules activeCareSchedules = careService.activeCareSchedules(mockPatient, asList(CWC_PNEUMOCOCCAL.getName(), CWC_ROTAVIRUS.getName(), CWC_PENTA.getName(), CWC_IPT_VACCINE.getName(), CWC_OPV_OTHERS.getName()));
        when(mockPatient.cwcCareProgramToEnrollOnRegistration(registrationDateTime.toLocalDate(), asList(CwcCareHistory.PENTA), cwcVO.getCWCCareHistoryVO(), activeCareSchedules, cwcVO.getCWCCareHistoryVO().getLastPentaDate(), cwcVO.getCWCCareHistoryVO().getLastIPTiDate(), cwcVO.getCWCCareHistoryVO().getLastOPVDate(), cwcVO.getCWCCareHistoryVO().getLastRotavirusDate(), cwcVO.getCWCCareHistoryVO().getLastPneumococcalDate())).thenReturn(asList(patientCare));

        careService.enrollToCWCCarePrograms(cwcVO, mockPatient);

        verify(mockAllObservations).findObservations(patientMotechId, Concept.IMMUNIZATIONS_ORDERED.getName());
        verify(mockAllObservations).findObservations(patientMotechId, Concept.PENTA.getName());
        verify(mockAllObservations).findObservations(patientMotechId, Concept.IPTI.getName());
        verify(mockPatient).cwcCareProgramToEnrollOnRegistration(registrationDateTime.toLocalDate(), new ArrayList<CwcCareHistory>() {{
            add(CwcCareHistory.PENTA);
        }}, cwcVO.getCWCCareHistoryVO(), activeCareSchedules, cwcVO.getCWCCareHistoryVO().getLastPentaDate(), cwcVO.getCWCCareHistoryVO().getLastIPTiDate(), cwcVO.getCWCCareHistoryVO().getLastOPVDate(), cwcVO.getCWCCareHistoryVO().getLastRotavirusDate(), cwcVO.getCWCCareHistoryVO().getLastPneumococcalDate());
        verifyIfScheduleEnrolled(0, expectedRequest(patientId, new PatientCare(patientCare.name(), patientCare.startingOn(), registrationDateTime.toLocalDate(), null, null)));
    }

    @Test
    public void shouldCreateSchedulesForCWCPentaOnHistoryFormSubmit() {
        String staffId = "staffid";
        String facilityId = "facilityid";
        String patientId = "patientid";
        Date date = DateUtil.today().toDate();
        final String patientMotechId = "patientMotechId";
        final Patient patient = new Patient(new MRSPatient(patientId, patientMotechId, new MRSPerson().dateOfBirth(today().minusWeeks(10).toDate()), new MRSFacility(facilityId)));
        when(mockAllPatients.getPatientByMotechId(patientId)).thenReturn(patient);
        final LocalDate lastIPTiDate = today().minusWeeks(5);
        final LocalDate lastOPVDate = today().minusDays(5);
        CWCCareHistoryVO cwcCareHistoryVO = new CWCCareHistoryVO(true, Arrays.asList(CwcCareHistory.IPTI, CwcCareHistory.OPV), null, null, null, null, null, null, null, null, lastOPVDate.toDate(), 1, 1, lastIPTiDate.toDate(), null, null, null, null);
        final ANCCareHistoryVO ancCareHistoryVO = new ANCCareHistoryVO(false, Collections.<ANCCareHistory>emptyList(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        CareHistoryVO careHistoryVO = new CareHistoryVO(staffId, facilityId, patientId, date, ancCareHistoryVO, cwcCareHistoryVO);
        careService.addCareHistory(careHistoryVO);
        ArgumentCaptor<EnrollmentRequest> enrollmentArgumentCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllCareSchedules, atLeastOnce()).enroll(enrollmentArgumentCaptor.capture());
        assertThat(enrollmentArgumentCaptor.getValue().getExternalId(), is(equalTo(patientId)));
        assertScheduleCreated(lastIPTiDate, enrollmentArgumentCaptor, CWC_IPT_VACCINE.getName());
        assertScheduleCreated(lastOPVDate, enrollmentArgumentCaptor, CWC_OPV_OTHERS.getName());
    }

    private void assertScheduleCreated(LocalDate lastTakenDate, ArgumentCaptor<EnrollmentRequest> enrollmentArgumentCaptor, String scheduleName) {
        EnrollmentRequest enrollmentRequest = null;
        for (EnrollmentRequest request : enrollmentArgumentCaptor.getAllValues()) {
            if (request.getScheduleName().equals(scheduleName)) {
                enrollmentRequest = request;
                assertThat(enrollmentRequest.getEnrollmentDateTime(), is(equalTo(DateUtil.newDateTime(lastTakenDate))));
            }
        }
    }

    @Test
    public void shouldCreateSchedulesForCWCPentaOnHistoryFormSubmitWhenDueDateIsInPast() {
        String staffId = "staffId";
        String facilityId = "facilityid";
        String patientId = "patientid";
        Date date = DateUtil.today().toDate();
        Date dateOfBirth = DateUtil.today().minusMonths(2).toDate();
        final String patientMotechId = "patientMotechId";
        CareService careServiceSpy = spy(careService);
        final LocalDate lastPentaDate = today().minusWeeks(7);
        CwcVO cwcVO = new CwcVO(staffId, facilityId, date, patientMotechId, asList(CwcCareHistory.PENTA), null, null, null,
                null, null, null, lastPentaDate.toDate(), 1, null, null, null, null, null, null, null, null, null, true);
        PatientCare patientCare = new PatientCare(CWC_PENTA.getName(), new LocalDate(), newDate(date), null, null);
        setupPatient(patientId, patientMotechId);
        when(mockAllCareSchedules.getDueWindowAlertTimings(Matchers.<EnrollmentRequest>any())).thenReturn(Arrays.<DateTime>asList(newDate(dateOfBirth).plusMonths(3).toDateTimeAtCurrentTime()));
        when(mockPatient.cwcCareProgramToEnrollOnRegistration(Matchers.<LocalDate>any(), Matchers.anyList(), Matchers.<CWCCareHistoryVO>any(), Matchers.<ActiveCareSchedules>any(), Matchers.<Date>any(), Matchers.<Date>any(), Matchers.<Date>any(), Matchers.<Date>any(), Matchers.<Date>any())).thenReturn(asList(patientCare));
        careServiceSpy.enroll(cwcVO);

        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllCareSchedules, atLeastOnce()).enroll(requestCaptor.capture());
        List<EnrollmentRequest> requests = requestCaptor.getAllValues();

        assertScheduleEnrollmentRequest(requests.get(0), expectedRequest(patientId, patientCare));
    }

    @Test
    public void shouldCreateSchedulesForCWCIPTiOnHistoryFormSubmitWhenDueDateIsInPast() {
        String staffId = "staffId";
        String facilityId = "facilityid";
        String patientId = "patientid";
        Date date = DateUtil.today().toDate();
        Date dateOfBirth = DateUtil.today().minusMonths(2).toDate();
        final String patientMotechId = "patientMotechId";
        CareService careServiceSpy = spy(careService);
        final LocalDate lastIPTiDate = today().minusWeeks(7);
        CwcVO cwcVO = new CwcVO(staffId, facilityId, date, patientMotechId, asList(CwcCareHistory.PENTA), null, null,
                null, null, null, null, null, null, null, null, lastIPTiDate.toDate(), 1, null, null, null, null, null, true);
        PatientCare patientCare = new PatientCare(CWC_IPT_VACCINE.getName(), new LocalDate(), newDate(date), null, null);
        setupPatient(patientId, patientMotechId);
        when(mockAllCareSchedules.getDueWindowAlertTimings(Matchers.<EnrollmentRequest>any())).thenReturn(Arrays.<DateTime>asList(newDate(dateOfBirth).plusMonths(3).toDateTimeAtCurrentTime()));
        when(mockPatient.cwcCareProgramToEnrollOnRegistration(Matchers.<LocalDate>any(), Matchers.anyList(), Matchers.<CWCCareHistoryVO>any(), Matchers.<ActiveCareSchedules>any(), Matchers.<Date>any(), Matchers.<Date>any(), Matchers.<Date>any(), Matchers.<Date>any(), Matchers.<Date>any())).thenReturn(asList(patientCare));
        careServiceSpy.enroll(cwcVO);

        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllCareSchedules, atLeastOnce()).enroll(requestCaptor.capture());
        List<EnrollmentRequest> requests = requestCaptor.getAllValues();

        assertScheduleEnrollmentRequest(requests.get(0), expectedRequest(patientId, patientCare));
    }

    @Test
    public void shouldCreatePNCSchedulesForChild() {
        String patientId = "Id";
        DateTime birthDateTime = newDateTime(2012, 12, 21, 2, 3, 0);
        PatientCare pnc1 = new PatientCare("PNC1", birthDateTime, birthDateTime, null, null);
        PatientCare pnc2 = new PatientCare("PNC2", birthDateTime, birthDateTime, null, null);
        setupPatient(patientId, null);
        when(mockPatient.pncBabyProgramsToEnrollOnRegistration()).thenReturn(asList(pnc1, pnc2));
        when(mockPatient.dateOfBirth()).thenReturn(birthDateTime);

        careService.enrollChildForPNC(mockPatient);
        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllCareSchedules, times(2)).enroll(requestCaptor.capture());
        List<EnrollmentRequest> requests = requestCaptor.getAllValues();
        assertScheduleEnrollmentRequest(requests.get(0), expectedRequest(patientId, pnc1));
        assertScheduleEnrollmentRequest(requests.get(1), expectedRequest(patientId, pnc2));
    }

    private void verifyIfScheduleEnrolled(int indexForSchedule, EnrollmentRequest expectedRequest) {
        ArgumentCaptor<EnrollmentRequest> requestCaptor = forClass(EnrollmentRequest.class);
        verify(mockAllCareSchedules).enroll(requestCaptor.capture());
        assertScheduleEnrollmentRequest(requestCaptor.getAllValues().get(indexForSchedule),
                expectedRequest);
    }

    private EnrollmentRequest expectedRequest(String externalId, PatientCare patientCare) {
        return new EnrollmentRequest(externalId, patientCare.name(),
                patientCare.preferredTime(), patientCare.startingOn(),
                patientCare.referenceTime(), patientCare.enrollmentDate(), patientCare.enrollmentTime(), patientCare.milestoneName(), patientCare.metaData());
    }

    private void assertEnrollmentRequests(List<EnrollmentRequest> actualRequests, List<EnrollmentRequest> expectedRequests) {
        Assert.assertThat(expectedRequests.size(), is(actualRequests.size()));
        int index = 0;
        for (EnrollmentRequest actual : actualRequests) {
            assertScheduleEnrollmentRequest(actual, expectedRequests.get(index++));
        }
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
                                  String facilityId, String staffId, String patientMotechId, List<ANCCareHistory> careHistories, Date estimatedDateOfDelivery,
                                  String lastHbLevels, String lastMotherVitaminA, String lastIronOrFolate, String lastSyphilis, String lastMalaria, String lastDiarrhea,String lastPnuemonia,
                                  Date lastHbDate, Date lastMotherVitaminADate, Date lastIronOrFolateDate, Date lastSyphilisDate, Date lastMalariaDate, Date lastDiarrheaDate, Date lastPnuemoniaDate, Boolean addCareHistory) {
        return new ANCVO(staffId, facilityId, patientMotechId, registrationDate, registrationToday, "2321322", estimatedDateOfDelivery,
                12.34, 12, 34, true, (!careHistories.isEmpty()), careHistories, ipt, tt,lastHbLevels,lastMotherVitaminA,lastIronOrFolate,lastSyphilis,
                lastMalaria, lastDiarrhea, lastPnuemonia, iptDate, ttDate, lastHbDate, lastMotherVitaminADate, lastIronOrFolateDate, lastSyphilisDate, lastMalariaDate, lastDiarrheaDate, lastPnuemoniaDate, true);
    }

    private void setupPatient(String patientId, String patientMotechId) {
        mockPatient = mock(Patient.class);
        mockMRSPatient = mock(MRSPatient.class);

        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockPatient.getMRSPatientId()).thenReturn(patientId);
        when(mockPatient.getMotechId()).thenReturn(patientMotechId);
        when(mockMRSPatient.getId()).thenReturn(patientId);
        when(mockAllPatients.getPatientByMotechId(mockPatient.getMotechId())).thenReturn(mockPatient);
        when(mockPatient.dateOfBirth()).thenReturn(DateUtil.newDate(2011, 8, 31).toDateTimeAtCurrentTime());
    }

}
