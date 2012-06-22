package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.repository.AllSchedulesAndMessages;
import org.motechproject.ghana.national.service.request.PNCBabyRequest;
import org.motechproject.ghana.national.vo.CWCVisit;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.*;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.PNCChildVisit.PNC1;
import static org.motechproject.ghana.national.domain.PNCChildVisit.PNC2;
import static org.motechproject.ghana.national.domain.PatientTest.createPatient;
import static org.motechproject.util.DateUtil.*;

public class ChildVisitServiceTest extends BaseUnitTest {
    private ChildVisitService service;
    @Mock
    AllEncounters mockAllEncounters;
    @Mock
    private AllCareSchedules mockAllCareSchedules;
    @Mock
    private AllSchedulesAndMessages mockAllSchedulesAndMessages;

    @Before
    public void setUp() {
        initMocks(this);
        service = new ChildVisitService(mockAllEncounters, mockAllCareSchedules, mockAllSchedulesAndMessages);
    }

    @Test
    public void shouldCreateEncounterForCWCVisitWithAllInfo() {
        MRSUser staff = mock(MRSUser.class);
        Facility facility = mock(Facility.class);
        Patient patient = createPatient("pId", "motechId", DateUtil.today(), "facilityId");
        CWCVisit cwcVisit = createTestCWCVisit(new Date(), staff, facility, patient);

        ChildVisitService spyService = spy(service);
        spyService.save(cwcVisit);

        ArgumentCaptor<Encounter> encounterCaptor = ArgumentCaptor.forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCaptor.capture());
        verify(spyService).updatePentaSchedule(cwcVisit);
        verify(spyService).updateYellowFeverSchedule(cwcVisit);
        verify(spyService).updateIPTSchedule(cwcVisit);
        verify(spyService).updateBCGSchedule(cwcVisit);
        verify(spyService).updateOPVSchedule(cwcVisit);
        verify(spyService).updateRotavirusSchedule(cwcVisit);
        verify(spyService).updatePneumococcalSchedule(cwcVisit);
    }

    @Test
    public void shouldCreateEncounterForPNCBabyWithAllInfo() {
        String patientId = "mrsPatientId";
        MRSUser staff = mock(MRSUser.class);
        Facility facility = mock(Facility.class);
        Patient patient = new Patient(new MRSPatient(patientId, "motechId", new MRSPerson().dateOfBirth(newDate(2011, 3, 30).toDate()), new MRSFacility(null)));
        DateTime visitDate = newDateTime(2012, 2, 2, 9, 8, 8);
        Time visitTime = time(visitDate);
        PNCBabyRequest pncBabyRequest = createTestPNCBabyForm(visitDate, staff, facility, patient).visit(PNC2);

        DateTime today = visitDate.plusDays(1);
        mockCurrentDate(today);
        ChildVisitService spyService = spy(service);
        spyService.save(pncBabyRequest);

        ArgumentCaptor<Encounter> encounterCaptor = ArgumentCaptor.forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCaptor.capture());

        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedulesAndMessages).enrollOrFulfill(requestCaptor.capture(), eq(visitDate.toLocalDate()), eq(visitTime));
        assertEnrollmentRequest(new EnrollmentRequest(patientId, PNC2.scheduleName(), null, visitDate.toLocalDate(), visitTime, visitDate.toLocalDate(), visitTime, null, null)
                , requestCaptor.getValue());
    }

    private PNCBabyRequest createTestPNCBabyForm(DateTime date, MRSUser staff, Facility facility, Patient patient) {
        PNCBabyRequest pncBabyRequest = new PNCBabyRequest();
        return pncBabyRequest.staff(staff).
                facility(facility).
                patient(patient).
                date(date).
                weight(65.67d).
                comments("comments").
                location("34").
                house("house").
                community("community").
                maleInvolved(false).
                babyConditionGood(Boolean.TRUE).
                cordConditionNormal(Boolean.TRUE).
                referred(false).
                bcg(false).opv0(false).
                respiration(12).
                temperature(32.2d).
                visit(PNC1);
    }

    @Test
    public void shouldFulfillPentaScheduleIfAlreadyEnrolled() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = createPatient(mrsPatientId, "motechId", DateUtil.today(), "facilityId");

        EnrollmentRecord enrollmentRecord = mock(EnrollmentRecord.class);
        when(mockAllCareSchedules.enrollment(any(EnrollmentRequest.class))).thenReturn(enrollmentRecord);

        final LocalDate visitDate = DateUtil.today();
        service.updatePentaSchedule(createTestCWCVisit(visitDate.toDate(), mock(MRSUser.class), mock(Facility.class), patient).pentadose("2"));

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedulesAndMessages).enrollOrFulfill(enrollmentRequestCaptor.capture(), any(LocalDate.class));

        assertThat(enrollmentRequestCaptor.getValue().getReferenceDate(), is(equalTo(visitDate)));
    }

    @Test
    public void shouldNotFulfillPentaScheduleWhenPentaNotChosenInImmunization() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = mock(Patient.class);
        when(patient.getMRSPatientId()).thenReturn(mrsPatientId);
        EnrollmentRecord enrollmentRecord = mock(EnrollmentRecord.class);
        when(mockAllCareSchedules.enrollment(any(EnrollmentRequest.class))).thenReturn(enrollmentRecord);

        CWCVisit cwcVisit = createTestCWCVisit(new Date(), mock(MRSUser.class), mock(Facility.class), patient);
        cwcVisit.pentadose("");
        service.updatePentaSchedule(cwcVisit);

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllCareSchedules, never()).enroll(enrollmentRequestCaptor.capture());
        verify(mockAllCareSchedules, never()).fulfilCurrentMilestone(eq(mrsPatientId), eq(ScheduleNames.CWC_PENTA.getName()), any(LocalDate.class));
    }

    @Test
    public void shouldEnrollAndFulfillPentaScheduleIfNotEnrolledEarlier() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = createPatient(mrsPatientId, "motechId", DateUtil.today(), "facilityId");
        when(mockAllCareSchedules.enrollment(any(EnrollmentRequest.class))).thenReturn(null);

        final LocalDate visitDate = DateUtil.today();
        service.updatePentaSchedule(createTestCWCVisit(visitDate.toDate(), mock(MRSUser.class), mock(Facility.class), patient).pentadose("2"));

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedulesAndMessages).enrollOrFulfill(enrollmentRequestCaptor.capture(), any(LocalDate.class));

        assertThat(enrollmentRequestCaptor.getValue().getReferenceDate(), is(equalTo(visitDate)));

    }

    @Test
    public void shouldFulfillRotavirusScheduleIfAlreadyEnrolled() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = createPatient(mrsPatientId, "motechId", DateUtil.today(), "facilityId");

        EnrollmentRecord enrollmentRecord = mock(EnrollmentRecord.class);
        when(mockAllCareSchedules.enrollment(any(EnrollmentRequest.class))).thenReturn(enrollmentRecord);


        final LocalDate visitDate = DateUtil.today();
        service.updateRotavirusSchedule(createTestCWCVisit(visitDate.toDate(), mock(MRSUser.class), mock(Facility.class), patient).rotavirusdose("1"));

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedulesAndMessages).enrollOrFulfill(enrollmentRequestCaptor.capture(), any(LocalDate.class));

        assertThat(enrollmentRequestCaptor.getValue().getReferenceDate(), is(equalTo(visitDate)));
    }

    @Test
    public void shouldFulfillPneumococcalScheduleIfAlreadyEnrolled() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = createPatient(mrsPatientId, "motechId", DateUtil.today(), "facilityId");

        EnrollmentRecord enrollmentRecord = mock(EnrollmentRecord.class);
        when(mockAllCareSchedules.enrollment(any(EnrollmentRequest.class))).thenReturn(enrollmentRecord);


        final LocalDate visitDate = DateUtil.today();
        service.updatePneumococcalSchedule(createTestCWCVisit(visitDate.toDate(), mock(MRSUser.class), mock(Facility.class), patient).pneumococcaldose("1"));

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedulesAndMessages).enrollOrFulfill(enrollmentRequestCaptor.capture(), any(LocalDate.class));

        assertThat(enrollmentRequestCaptor.getValue().getReferenceDate(), is(equalTo(visitDate)));
    }

    @Test
    public void shouldNotFulfillRotavirusScheduleWhenRotavirusNotChosenInImmunization() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = mock(Patient.class);
        when(patient.getMRSPatientId()).thenReturn(mrsPatientId);
        EnrollmentRecord enrollmentRecord = mock(EnrollmentRecord.class);
        when(mockAllCareSchedules.enrollment(any(EnrollmentRequest.class))).thenReturn(enrollmentRecord);

        CWCVisit cwcVisit = createTestCWCVisit(new Date(), mock(MRSUser.class), mock(Facility.class), patient);
        service.updateRotavirusSchedule(cwcVisit);

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllCareSchedules, never()).enroll(enrollmentRequestCaptor.capture());
        verify(mockAllCareSchedules, never()).fulfilCurrentMilestone(eq(mrsPatientId), eq(ScheduleNames.CWC_PENTA.getName()), any(LocalDate.class));

    }

    @Test
    public void shouldEnrollAndFulfillRotavirusScheduleIfNotEnrolledEarlier() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = createPatient(mrsPatientId, "motechId", DateUtil.today(), "facilityId");
        when(mockAllCareSchedules.enrollment(any(EnrollmentRequest.class))).thenReturn(null);

        final LocalDate visitDate = DateUtil.today();
        service.updateRotavirusSchedule(createTestCWCVisit(visitDate.toDate(), mock(MRSUser.class), mock(Facility.class), patient).rotavirusdose("1"));

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedulesAndMessages).enrollOrFulfill(enrollmentRequestCaptor.capture(), any(LocalDate.class));

        assertThat(enrollmentRequestCaptor.getValue().getReferenceDate(), is(equalTo(visitDate)));

    }

    @Test
    // 2 scenarios - in both case IPT1 is created and fulfilled and send alerts for remaining weeks
    // i) User enrolled after 14th week
    // ii) User enrolled for SP2, taking IPT2 directly based on some pre-history
    public void shouldEnrollOrFulfilIPTiScheduleBasedOnVisitDate_AndRecordObservations() {

        Time deliveryTime = new Time(20, 2);
        DateTime today = new DateTime(2012, 2, 1, deliveryTime.getHour(), deliveryTime.getMinute());
        LocalDate visitDate = today.minusDays(10).toLocalDate();
        IPTiDose ipTiDose = IPTiDose.IPTi1;
        String mrsPatientId = "1234";
        CWCVisit cwcVisit = createTestCWCVisit(visitDate.toDate(), new MRSUser(), mock(Facility.class),
                new Patient(new MRSPatient(mrsPatientId, "", new MRSPerson().dateOfBirth(newDate(2011, 1, 1).toDate()), new MRSFacility("fid"))))
                .iptidose(ipTiDose.getDose(ipTiDose).toString());

        mockCurrentDate(today);
        service.save(cwcVisit);

        ArgumentCaptor<Encounter> encounterCaptor = forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCaptor.capture());
        assertIfObservationsAvailableForConcepts(true, encounterCaptor.getValue().getObservations(), IPTI.getName());

        ArgumentCaptor<EnrollmentRequest> captor = forClass(EnrollmentRequest.class);

        verify(mockAllSchedulesAndMessages).enrollOrFulfill(captor.capture(), eq(visitDate));
        assertEnrollmentRequest(new EnrollmentRequest(mrsPatientId, CWC_IPT_VACCINE.getName(), deliveryTime, visitDate, deliveryTime, visitDate, null, ipTiDose.milestoneName(), null), captor.getAllValues().get(0));
    }

    @Test
    public void shouldNotCreateIPTSchedule_IfIPTReadingsAreNotCaptured() {
        Time deliveryTime = new Time(20, 2);
        DateTime today = new DateTime(2012, 2, 1, deliveryTime.getHour(), deliveryTime.getMinute());
        mockCurrentDate(today);
        LocalDate visitDate = today.minusDays(10).toLocalDate();
        String mrsPatientId = "99999";
        CWCVisit cwcVisit = createTestCWCVisit(visitDate.toDate(), new MRSUser(), mock(Facility.class),
                new Patient(new MRSPatient(mrsPatientId, "", new MRSPerson().dateOfBirth(newDate(2011, 1, 1).toDate()), new MRSFacility("fid"))))
                .iptidose(null);

        service.save(cwcVisit);
        ArgumentCaptor<Encounter> encounterCaptor = forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCaptor.capture());
        assertIfObservationsAvailableForConcepts(false, encounterCaptor.getValue().getObservations(), IPTI.getName());
        verify(mockAllCareSchedules, never()).fulfilCurrentMilestone(mrsPatientId, CWC_IPT_VACCINE.getName(), visitDate);
    }

    @Test
    public void shouldFulfillYellowFeverScheduleIfYellowFeverIsChosen() {
        String facilityId = "facilityId";
        String mrsPatientId = "mrsId";
        MRSPatient mrsPatient = mock(MRSPatient.class);
        MRSFacility mockFacility = mock(MRSFacility.class);
        Facility facility = new Facility(mockFacility);
        Patient patient = new Patient(mrsPatient);

        CWCVisit testCWCVisit = createTestCWCVisit(new Date(), new MRSUser(), facility, patient);

        testCWCVisit.immunizations(asList(Concept.YF.name()));
        LocalDate date = DateUtil.newDate(testCWCVisit.getDate());
        when(mrsPatient.getId()).thenReturn(mrsPatientId);
        when(mrsPatient.getFacility()).thenReturn(mockFacility);
        when(mrsPatient.getFacility().getId()).thenReturn(facilityId);

        service.updateYellowFeverSchedule(testCWCVisit);

        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedulesAndMessages).enrollOrFulfill(requestCaptor.capture(), eq(date));

        EnrollmentRequest request = requestCaptor.getValue();
        assertThat(request.getExternalId(), is(mrsPatientId));
        assertThat(request.getEnrollmentDateTime(), is(date.toDateTimeAtStartOfDay()));
        assertThat(request.getScheduleName(), is(CWC_YELLOW_FEVER.getName()));
        assertThat(request.getStartingMilestoneName(), is(nullValue()));
    }

    @Test
    public void shouldFulfillBCGScheduleIfChosen() {
        String facilityId = "facilityId";
        String mrsPatientId = "mrsId";

        MRSPatient mrsPatient = mock(MRSPatient.class);
        MRSFacility mockFacility = mock(MRSFacility.class);
        Facility facility = new Facility(mockFacility);
        Patient patient = new Patient(mrsPatient);

        CWCVisit testCWCVisit = createTestCWCVisit(new Date(), new MRSUser(), facility, patient);
        testCWCVisit.immunizations(asList(Concept.BCG.name()));
        LocalDate date = DateUtil.newDate(testCWCVisit.getDate());

        when(mrsPatient.getId()).thenReturn(mrsPatientId);
        when(mrsPatient.getFacility()).thenReturn(mockFacility);
        when(mrsPatient.getFacility().getId()).thenReturn(facilityId);

        service.updateBCGSchedule(testCWCVisit);

        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedulesAndMessages).enrollOrFulfill(requestCaptor.capture(), eq(date));

        EnrollmentRequest request = requestCaptor.getValue();
        assertThat(request.getExternalId(), is(mrsPatientId));
        assertThat(request.getEnrollmentDateTime(), is(date.toDateTimeAtStartOfDay()));
        assertThat(request.getScheduleName(), is(CWC_BCG.getName()));
        assertThat(request.getStartingMilestoneName(), is(nullValue()));
    }

    @Test
    public void shouldFulFillOPV0ScheduleIfChosen() {
        String facilityId = "facilityId";
        String mrsPatientId = "mrsId";

        MRSPatient mrsPatient = mock(MRSPatient.class);
        MRSFacility mockFacility = mock(MRSFacility.class);
        Facility facility = new Facility(mockFacility);
        Patient patient = new Patient(mrsPatient);

        CWCVisit testCWCVisit = createTestCWCVisit(new Date(), new MRSUser(), facility, patient);
        testCWCVisit.immunizations(asList(Concept.OPV.name()));
        testCWCVisit.opvdose(String.valueOf(OPVDose.OPV_0.value()));
        LocalDate date = DateUtil.newDate(testCWCVisit.getDate());

        when(mrsPatient.getId()).thenReturn(mrsPatientId);
        when(mrsPatient.getFacility()).thenReturn(mockFacility);
        when(mrsPatient.getFacility().getId()).thenReturn(facilityId);

        service.updateOPVSchedule(testCWCVisit);

        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedulesAndMessages).enrollOrFulfill(requestCaptor.capture(), eq(date));

        EnrollmentRequest request = requestCaptor.getValue();
        assertThat(request.getExternalId(), is(mrsPatientId));
        assertThat(request.getEnrollmentDateTime(), is(date.toDateTimeAtStartOfDay()));
        assertThat(request.getScheduleName(), is(CWC_OPV_0.getName()));
        assertThat(request.getStartingMilestoneName(), is(nullValue()));
    }

    @Test
    public void shouldFulFillOtherOPVScheduleIfChosen() {
        String facilityId = "facilityId";
        String mrsPatientId = "mrsId";

        MRSPatient mrsPatient = mock(MRSPatient.class);
        MRSFacility mockFacility = mock(MRSFacility.class);
        Facility facility = new Facility(mockFacility);
        Patient patient = new Patient(mrsPatient);

        CWCVisit testCWCVisit = createTestCWCVisit(new Date(), new MRSUser(), facility, patient);
        testCWCVisit.immunizations(asList(Concept.OPV.name()));
        testCWCVisit.opvdose(String.valueOf(OPVDose.OPV_1.value()));
        LocalDate date = DateUtil.newDate(testCWCVisit.getDate());

        when(mrsPatient.getId()).thenReturn(mrsPatientId);
        when(mrsPatient.getFacility()).thenReturn(mockFacility);
        when(mrsPatient.getFacility().getId()).thenReturn(facilityId);

        service.updateOPVSchedule(testCWCVisit);

        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedulesAndMessages).enrollOrFulfill(requestCaptor.capture(), eq(date));

        EnrollmentRequest request = requestCaptor.getValue();
        assertThat(request.getExternalId(), is(mrsPatientId));
        assertThat(request.getEnrollmentDateTime(), is(date.toDateTimeAtStartOfDay()));
        assertThat(request.getScheduleName(), is(CWC_OPV_OTHERS.getName()));
        assertThat(request.getStartingMilestoneName(), is(nullValue()));
    }

    @Test
    public void shouldFulfilMilestoneIfEnrolledAndTakenMeaslesVaccineOnVisit() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = new Patient(new MRSPatient(mrsPatientId, "motechId", new MRSPerson().dateOfBirth(newDate(2011, 3, 30).toDate()), new MRSFacility(null)));
        LocalDate visitDate = newDate(2012, 2, 3);
        CWCVisit cwcVisit = createTestCWCVisit(visitDate.toDate(), mock(MRSUser.class), mock(Facility.class), patient)
                .immunizations(asList(MEASLES.name()));
        EnrollmentRecord mockEnrollmentRecord = mock(EnrollmentRecord.class);
        when(mockAllCareSchedules.enrollment(Matchers.<EnrollmentRequest>any())).thenReturn(mockEnrollmentRecord);
        service.save(cwcVisit);

        ArgumentCaptor<EnrollmentRequest> requestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedulesAndMessages).enrollOrFulfill(requestCaptor.capture(), eq(visitDate));
    }

    @Test
    public void shouldNotFulFillAnyOPVMilestoneIfDosageNotTaken() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = new Patient(new MRSPatient(mrsPatientId, "motechId", new MRSPerson().dateOfBirth(newDate(2011, 3, 30).toDate()), new MRSFacility(null)));
        LocalDate visitDate = newDate(2012, 2, 3);
        CWCVisit cwcVisit = createTestCWCVisit(visitDate.toDate(), mock(MRSUser.class), mock(Facility.class), patient)
                .immunizations(asList(BCG.name()));
        service.save(cwcVisit);

        verify(mockAllSchedulesAndMessages, never()).fulfilCurrentMilestone(mrsPatientId, CWC_OPV_0.getName(), visitDate);
        verify(mockAllSchedulesAndMessages, never()).fulfilCurrentMilestone(mrsPatientId, CWC_OPV_OTHERS.getName(), visitDate);
    }

    @Test
    public void shouldNonFulfilMilestoneIfHaveNotTakenMeaslesVaccineOnVisit() {

        Patient patient = new Patient(new MRSPatient("mrsPatientId", "motechId", new MRSPerson().dateOfBirth(newDate(2011, 3, 30).toDate()), new MRSFacility(null)));
        CWCVisit cwcVisit = createTestCWCVisit(today().toDate(), mock(MRSUser.class), mock(Facility.class), patient).immunizations(asList(Concept.PENTA.name()));
        EnrollmentRecord mockEnrollmentRecord = mock(EnrollmentRecord.class);
        when(mockAllCareSchedules.enrollment(Matchers.<EnrollmentRequest>any())).thenReturn(mockEnrollmentRecord);
        service.save(cwcVisit);
        verify(mockAllSchedulesAndMessages, never()).fulfilCurrentMilestone(eq("mrsPatientId"), eq(CWC_MEASLES_VACCINE.getName()), Matchers.<LocalDate>any());
    }

    @Test
    public void shouldNonFulfilMilestoneIfNotEnrolledAndHaveEnteredTakenMeaslesVaccineMistakenlyOnVisit() {

        Patient patient = new Patient(new MRSPatient("mrsPatientId", "motechId", new MRSPerson().dateOfBirth(newDate(2011, 3, 30).toDate()), new MRSFacility(null)));
        CWCVisit cwcVisit = createTestCWCVisit(today().toDate(), mock(MRSUser.class), mock(Facility.class), patient).immunizations(asList(MEASLES.name()));
        when(mockAllCareSchedules.enrollment(Matchers.<EnrollmentRequest>any())).thenReturn(null);
        service.save(cwcVisit);
        verify(mockAllSchedulesAndMessages, never()).fulfilCurrentMilestone(eq("mrsPatientId"), eq(CWC_MEASLES_VACCINE.getName()), Matchers.<LocalDate>any());
    }

    private CWCVisit createTestCWCVisit(Date registrationDate, MRSUser staff, Facility facility, Patient patient) {
        CWCVisit cwcVisit = new CWCVisit();
        return cwcVisit.staff(staff).facility(facility).patient(patient).date(registrationDate).serialNumber("4ds65")
                .weight(65.67d).comments("comments").cwcLocation("34").house("house").community("community").maleInvolved(false);
    }

    private void assertEnrollmentReqWithoutDeliveryTime(EnrollmentRequest expected, EnrollmentRequest actual) {
        assertThat(actual.getScheduleName(), is(equalTo(expected.getScheduleName())));
        assertThat(actual.getReferenceDate(), is(equalTo(expected.getReferenceDate())));
        assertThat(actual.getExternalId(), is(equalTo(expected.getExternalId())));
        assertThat(actual.getStartingMilestoneName(), is(equalTo(expected.getStartingMilestoneName())));
    }

    private void assertEnrollmentRequest(EnrollmentRequest expected, EnrollmentRequest actual) {
        assertEnrollmentReqWithoutDeliveryTime(expected, actual);
        assertThat(actual.getPreferredAlertTime(), is(equalTo(expected.getPreferredAlertTime())));
    }

    private void assertIfObservationsAvailableForConcepts(Boolean present, Set<MRSObservation> observations, String... conceptNames) {
        for (String conceptName : conceptNames)
            if (present)
                assertNotNull("concept not present:" + conceptName, selectFirst(observations, having(on(MRSObservation.class).getConceptName(), equalTo(conceptName))));
            else
                assertNull("concept present:" + conceptName, selectFirst(observations, having(on(MRSObservation.class).getConceptName(), equalTo(conceptName))));
    }
}
