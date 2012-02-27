package org.motechproject.ghana.national.service;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllAppointments;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.service.request.ANCVisitRequest;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_VISIT;
import static org.motechproject.ghana.national.domain.TTVaccineDosage.TT2;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDate;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class MotherVisitServiceTest extends BaseUnitTest {
    private MotherVisitService motherVisitService;

    @Mock
    private AllEncounters mockAllEncounters;
    @Mock
    private AllObservations mockAllObservations;
    @Mock
    private AllSchedules mockAllSchedules;
    @Mock
    private AllAppointments mockAllAppointments;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        motherVisitService = spy(new MotherVisitService(mockAllEncounters, mockAllObservations, mockAllSchedules, mockAllAppointments));
    }

    @Test
    public void shouldCreateEncounter_EnrollPatientForCurrentScheduleAndCreateSchedulesForTheNext() {
        MRSUser staff = new MRSUser();
        Facility facility = new Facility();
        final String patientId = "patient id";
        final Patient patient = new Patient(new MRSPatient(patientId, null, null));
        final LocalDate vaccinationDate = DateUtil.newDate(2000, 2, 1);
        motherVisitService.receivedTT(TT2, patient, staff, facility, vaccinationDate);

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        ArgumentCaptor<LocalDate> fulfillmentDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(mockAllSchedules).enrollOrFulfill(enrollmentRequestCaptor.capture(), fulfillmentDateCaptor.capture());
        assertThat(fulfillmentDateCaptor.getValue(), is(vaccinationDate));

        EnrollmentRequest enrollmentRequest = enrollmentRequestCaptor.getValue();
        assertThat(enrollmentRequest.getScheduleName(), is(equalTo(TT_VACCINATION_VISIT)));
        assertThat(enrollmentRequest.getStartingMilestoneName(), is(equalTo(TT2.name())));
        assertThat(enrollmentRequest.getReferenceDate(), is(equalTo(vaccinationDate)));
    }

    @Test
    public void shouldCreateEncounterForANCVisitWithAllInfo() {
        String mrsFacilityId = "mrsFacilityId";
        String mrsPatientId = "34";
        Facility facility = new Facility(new MRSFacility(mrsFacilityId)).mrsFacilityId(mrsFacilityId);
        MRSPatient mrsPatient = new MRSPatient(mrsPatientId,"motechPatient", null, facility.mrsFacility());
        Patient patient = new Patient(mrsPatient);
        MRSUser staff = new MRSUser();
        ANCVisitRequest ancVisit = createTestANCVisit(staff, facility, patient);
        Pregnancy pregnancy = basedOnDeliveryDate(new LocalDate(2012, 12, 23));

        mockCurrentDate(new LocalDate(2012, 5, 1));
        when(mockAllObservations.updateEDD(ancVisit.getEstDeliveryDate(), patient, ancVisit.getStaff().getId()))
                .thenReturn(new HashSet<MRSObservation>() {{add(new MRSObservation<Object>(new Date(), null, null));}});
        mockFetchLatestEDD(pregnancy.dateOfDelivery(), ancVisit.getPatient().getMRSPatientId());

        motherVisitService.registerANCVisit(ancVisit);

        ArgumentCaptor<Encounter> encounterCapture = ArgumentCaptor.forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCapture.capture());

        Encounter encounter = encounterCapture.getValue();
        assertThat(encounter.getStaff().getId(), Is.is(ancVisit.getStaff().getId()));
        assertThat(encounter.getMrsPatient().getId(), Is.is(mrsPatientId));
        assertThat(encounter.getFacility().getId(), Is.is(ancVisit.getFacility().getMrsFacilityId()));
        assertThat(encounter.getType(), Is.is(ANC_VISIT.value()));
        assertReflectionEquals(encounter.getDate(), DateUtil.today().toDate(), ReflectionComparatorMode.LENIENT_DATES);

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedules, times(2)).enroll(enrollmentRequestCaptor.capture());

        assertEnrollmentReqWithoutDeliveryTime(new EnrollmentRequest(mrsPatientId, DELIVERY, null, new LocalDate(2011, 7, 26), null, null),
                enrollmentRequestCaptor.getAllValues().get(0));
        assertEnrollmentReqWithoutDeliveryTime(new EnrollmentRequest(mrsPatientId, ANC_IPT_VACCINE, null, pregnancy.dateOfConception(), null, null),
                enrollmentRequestCaptor.getAllValues().get(1));
        verify(mockAllAppointments).updateANCVisitSchedule(patient, DateUtil.newDateTime(ancVisit.getNextANCDate()));

    }

    @Test
    public void shouldEnrollIPTScheduleAndFulfilFirstMilestone_AndRecordObservations_IfNotEnrolledAlready() {
        Time deliveryTime = new Time(20, 2);
        DateTime today = new DateTime(2012, 2, 1, deliveryTime.getHour(), deliveryTime.getMinute());
        Pregnancy pregnancy = basedOnDeliveryDate(new LocalDate(2012, 9, 1));
        ANCVisitRequest ancVisit = createTestANCVisit().iptdose("1").iptReactive(true);
        String mrsPatientId = ancVisit.getPatient().getMRSPatientId();

        mockCurrentDate(today);

        when(mockAllSchedules.enrollment(Matchers.<EnrollmentRequest>any())).thenReturn(null);
        mockFetchLatestEDD(pregnancy.dateOfDelivery(), mrsPatientId);
        motherVisitService.registerANCVisit(ancVisit);

        ArgumentCaptor<Encounter> encounterCaptor = forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCaptor.capture());
        assertIfObservationsAvailableForConcepts(true, encounterCaptor.getValue().getObservations(), IPT.getName(), IPT_REACTION.getName());

        ArgumentCaptor<EnrollmentRequest> captor = forClass(EnrollmentRequest.class);
        verify(mockAllSchedules).enroll(captor.capture());
        ArgumentCaptor<LocalDate> fulfillmentDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(mockAllSchedules).fulfilCurrentMilestone(captor.capture(), fulfillmentDateCaptor.capture());
        assertThat(fulfillmentDateCaptor.getValue(), is(DateUtil.today()));

        assertEnrollmentRequest(new EnrollmentRequest(mrsPatientId, ANC_IPT_VACCINE, deliveryTime, pregnancy.dateOfConception(), null, null), captor.getAllValues().get(0));
        assertEnrollmentRequest(new EnrollmentRequest(mrsPatientId, ANC_IPT_VACCINE, null, null, null, null), captor.getAllValues().get(1));
    }

    @Test
    public void shouldOnlyFulfilCurrentIPTMilestoneAndRecordObservations_IfEnrolledAlready() {
        Time deliveryTime = new Time(20, 2);
        mockCurrentDate(new DateTime(2012, 2, 1, deliveryTime.getHour(), deliveryTime.getMinute()));
        ANCVisitRequest ancVisit = createTestANCVisit().iptdose("1").iptReactive(true);
        String mrsPatientId = ancVisit.getPatient().getMRSPatientId();

        when(mockAllSchedules.enrollment(Matchers.<EnrollmentRequest>any())).thenReturn(new EnrollmentResponse(null, null, null, null, null));
        motherVisitService.registerANCVisit(ancVisit);

        ArgumentCaptor<Encounter> encounterCaptor = forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCaptor.capture());
        assertIfObservationsAvailableForConcepts(true, encounterCaptor.getValue().getObservations(), IPT.getName(), IPT_REACTION.getName());

        ArgumentCaptor<EnrollmentRequest> captor = forClass(EnrollmentRequest.class);
        verify(mockAllSchedules, never()).enroll(captor.capture());
        ArgumentCaptor<LocalDate> fulfillmentDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(mockAllSchedules).fulfilCurrentMilestone(captor.capture(), fulfillmentDateCaptor.capture());
        assertThat(fulfillmentDateCaptor.getValue(), is(DateUtil.today()));
        verify(mockAllObservations, never()).findObservation(ancVisit.getPatient().getMRSPatientId(), EDD.getName());
        assertEnrollmentRequest(new EnrollmentRequest(mrsPatientId, ANC_IPT_VACCINE, null, null, null, null), captor.getAllValues().get(0));
    }

    @Test
    public void shouldNotCreateIPTSchedule_IfIPTReadingsAreNotCaptured() {
        Time deliveryTime = new Time(20, 2);
        mockCurrentDate(new DateTime(2012, 2, 1, deliveryTime.getHour(), deliveryTime.getMinute()));
        ANCVisitRequest ancVisit = createTestANCVisit().iptdose(null).iptReactive(false);
        motherVisitService.registerANCVisit(ancVisit);
        ArgumentCaptor<Encounter> encounterCaptor = forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCaptor.capture());
        assertIfObservationsAvailableForConcepts(false, encounterCaptor.getValue().getObservations(), IPT.getName(), IPT_REACTION.getName());
        verifyZeroInteractions(mockAllSchedules);
    }

//   Working on this
// @Test
//    public void shouldEnrollIPTScheduleInThe21WeekOrLaterOfPregnancy_AndFulfilFirstMilestone_AndRecordObservations() {
//        Time deliveryTime = new Time(20, 2);
//        Pregnancy pregnancy = basedOnDeliveryDate(new LocalDate(2012, 6, 6));
//        DateTime today = DateUtil.newDateTime(pregnancy.dateOfConception().plusWeeks(21), deliveryTime);
//        ANCVisit ancVisit = createTestANCVisit().iptdose("3").iptReactive(false);
//        String mrsPatientId = ancVisit.getPatient().getMRSPatientId();
//
//        mockCurrentDate(today);
//
//        when(mockAllSchedules.enrollment(Matchers.<EnrollmentRequest>any())).thenReturn(new EnrollmentResponse(null, null, null, null, null));
//        mockFetchLatestEDD(pregnancy.dateOfDelivery(), ancVisit.getPatient().getMRSPatientId());
//        motherVisitService.registerANCVisit(ancVisit);
//
//        ArgumentCaptor<EnrollmentRequest> captor = forClass(EnrollmentRequest.class);
//        verify(mockAllSchedules, never()).enroll(captor.capture());
//        verify(mockAllSchedules).fulfilCurrentMilestone(captor.capture());
//
//        assertEnrollmentRequest(new EnrollmentRequest(mrsPatientId, ANC_IPT_VACCINE, null, null), captor.getAllValues().get(0));
//        assertEnrollmentRequest(new EnrollmentRequest(mrsPatientId, ANC_IPT_VACCINE, deliveryTime, today.toLocalDate()),
//                captor.getAllValues().get(0));
//    }

    private void assertIfObservationsAvailableForConcepts(Boolean present, Set<MRSObservation> observations, String... conceptNames) {
        for (String conceptName : conceptNames)
        if(present)
            assertNotNull("concept not present:" + conceptName, selectFirst(observations, having(on(MRSObservation.class).getConceptName(), equalTo(conceptName))));
        else
            assertNull("concept present:" + conceptName, selectFirst(observations, having(on(MRSObservation.class).getConceptName(), equalTo(conceptName))));
    }

    private void assertEnrollmentReqWithoutDeliveryTime(EnrollmentRequest expected, EnrollmentRequest actual) {
        assertThat(actual.getScheduleName(), is(equalTo(expected.getScheduleName())));
        assertThat(actual.getReferenceDate(), is(equalTo(expected.getReferenceDate())));
        assertThat(actual.getExternalId(), is(equalTo(expected.getExternalId())));
    }

    private void assertEnrollmentRequest(EnrollmentRequest expected, EnrollmentRequest actual) {
        assertEnrollmentReqWithoutDeliveryTime(expected, actual);
        assertThat(actual.getPreferredAlertTime(), is(equalTo(expected.getPreferredAlertTime())));
    }

    private void mockFetchLatestEDD(LocalDate edd, String patientMotechId) {
        when(mockAllObservations.findObservation(patientMotechId, EDD.getName())).
                thenReturn(new MRSObservation<Date>(DateUtil.today().toDate(), EDD.getName(), edd.toDate()));
    }

    private ANCVisitRequest createTestANCVisit() {
        return createTestANCVisit(new MRSUser(), new Facility(), new Patient(new MRSPatient("patientId")));
    }

    private ANCVisitRequest createTestANCVisit(MRSUser staff, Facility facility, Patient patient) {
        return new ANCVisitRequest().staff(staff).facility(facility).patient(patient).date(new Date()).serialNumber("4ds65")
                .visitNumber("4").estDeliveryDate(DateUtil.newDate(2012, 5, 1).toDate())
                .bpDiastolic(67).bpSystolic(10).weight(65.67d).comments("comments").ttdose("4").iptdose("3")
                .iptReactive(true).itnUse("Y").fht(4.3d).fhr(4).urineTestGlucosePositive("0").urineTestProteinPositive("1")
                .hemoglobin(13.8).vdrlReactive("N").vdrlTreatment(null).dewormer("Y").pmtct("Y").preTestCounseled("N")
                .hivTestResult("hiv").postTestCounseled("Y").pmtctTreament("Y").location("34").house("house").community("community")
                .referred("Y").maleInvolved(false).nextANCDate(newDate(2012, 2, 20).toDate());
    }

}
