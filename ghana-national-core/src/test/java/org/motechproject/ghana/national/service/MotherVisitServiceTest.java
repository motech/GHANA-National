package org.motechproject.ghana.national.service;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.repository.AllAppointments;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.service.request.ANCVisitRequest;
import org.motechproject.ghana.national.service.request.PNCMotherRequest;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.*;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
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
import static org.joda.time.DateTime.now;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_DELIVERY;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.domain.Concept.IPT;
import static org.motechproject.ghana.national.domain.Concept.IPT_REACTION;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_VISIT;
import static org.motechproject.ghana.national.domain.PNCMotherVisit.PNC1;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.today;
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
    private VisitService mockVisitService;
    @Mock
    private AllAppointments mockAllAppointments;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        motherVisitService = new MotherVisitService(mockAllEncounters, mockAllObservations, mockAllSchedules, mockAllAppointments, mockVisitService);
    }

    @Test
    public void shouldCreateEncounterForANCVisitWithAllInfo() {

        final MotherVisitService spyMotherVisitService = spy(motherVisitService);

        String mrsFacilityId = "mrsFacilityId";
        String mrsPatientId = "34";
        Facility facility = new Facility(new MRSFacility(mrsFacilityId)).mrsFacilityId(mrsFacilityId);
        MRSPatient mrsPatient = new MRSPatient(mrsPatientId, "motechPatient", null, facility.mrsFacility());
        Patient patient = new Patient(mrsPatient);
        MRSUser staff = new MRSUser();
        Pregnancy pregnancy = basedOnDeliveryDate(new LocalDate(2012, 12, 23));
        LocalDate visitDate = today().minusDays(10);
        ANCVisitRequest ancVisit = createTestANCVisit(staff, facility, patient).estDeliveryDate(pregnancy.dateOfDelivery().toDate()).date(visitDate.toDate());

        mockCurrentDate(new LocalDate(2012, 5, 1));
        when(mockAllObservations.updateEDD(ancVisit.getEstDeliveryDate(), patient, ancVisit.getStaff().getId()))
                .thenReturn(new HashSet<MRSObservation>() {{
                    add(new MRSObservation<Object>(new Date(), null, null));
                }});

        spyMotherVisitService.registerANCVisit(ancVisit);

        ArgumentCaptor<Encounter> encounterCapture = ArgumentCaptor.forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCapture.capture());

        Encounter encounter = encounterCapture.getValue();
        assertThat(encounter.getStaff().getId(), Is.is(ancVisit.getStaff().getId()));
        assertThat(encounter.getMrsPatient().getId(), Is.is(mrsPatientId));
        assertThat(encounter.getFacility().getId(), Is.is(ancVisit.getFacility().getMrsFacilityId()));
        assertThat(encounter.getType(), Is.is(ANC_VISIT.value()));
        assertReflectionEquals(encounter.getDate(), DateUtil.today().toDate(), ReflectionComparatorMode.LENIENT_DATES);

        verify(spyMotherVisitService).updateTT(eq(ancVisit), org.mockito.Matchers.<Set<MRSObservation>>any());

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedules, times(1)).enroll(enrollmentRequestCaptor.capture());
        verify(mockAllSchedules, times(1)).enrollOrFulfill(enrollmentRequestCaptor.capture(), eq(visitDate));

        assertEnrollmentReqWithoutDeliveryTime(new EnrollmentRequest(mrsPatientId, ANC_DELIVERY, null, pregnancy.dateOfConception(), null, null, null, null),
                enrollmentRequestCaptor.getAllValues().get(0));
        assertEnrollmentReqWithoutDeliveryTime(new EnrollmentRequest(mrsPatientId, ANC_IPT_VACCINE, null, visitDate, null,
                visitDate, null, IPTDose.byValue(ancVisit.getIptdose()).milestone()),
                enrollmentRequestCaptor.getAllValues().get(1));
//        verify(mockAllAppointments).updateANCVisitSchedule(patient, ancVisit.getDate(), DateUtil.newDateTime(ancVisit.getNextANCDate()));

    }

    @Test
    public void shouldEnrollOrFulfillPNCSchedulesForMother() {
        String mrsFacilityId = "mrsFacilityId";
        String mrsPatientId = "34";
        Facility facility = new Facility(new MRSFacility(mrsFacilityId)).mrsFacilityId(mrsFacilityId);
        MRSPerson mrsPerson = new MRSPerson();
        DateTime date = DateUtil.now();
        mrsPerson.dateOfBirth(date.toDate());
        MRSPatient mrsPatient = new MRSPatient(mrsPatientId, "motechPatient", mrsPerson, facility.mrsFacility());
        Patient patient = new Patient(mrsPatient);

        MRSUser staff = new MRSUser();
        Facility encounterFacility = new Facility(new MRSFacility("1"));

        motherVisitService.enrollOrFulfillPNCSchedulesForMother(createTestPncRequest(patient, encounterFacility, staff, date));

        ArgumentCaptor<Encounter> encounterArgumentCaptor = ArgumentCaptor.forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterArgumentCaptor.capture());
        Encounter actualEncounter = encounterArgumentCaptor.getValue();
        assertThat(actualEncounter.getMrsPatient(),is(equalTo(mrsPatient)));
        assertThat(actualEncounter.getFacility(),is(equalTo(encounterFacility.mrsFacility())));
        assertThat(actualEncounter.getStaff(),is(equalTo(staff)));
        assertThat(actualEncounter.getType(),is(equalTo(EncounterType.PNC_MOTHER_VISIT.value())));
        assertThat(actualEncounter.getObservations().size(),is(equalTo(9)));

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestArgumentCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        ArgumentCaptor<LocalDate> localDateArgumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(mockAllSchedules, times(1)).enrollOrFulfill(enrollmentRequestArgumentCaptor.capture(), localDateArgumentCaptor.capture());

        final DateTime expectedDateTime = date.withSecondOfMinute(0).withMillisOfSecond(0);
        assertThat(localDateArgumentCaptor.getValue(), is(equalTo(today())));
        EnrollmentRequest actualEnrollmentRequest = enrollmentRequestArgumentCaptor.getValue();
        assertThat(actualEnrollmentRequest.getExternalId(), is(equalTo(mrsPatientId)));
        assertThat(actualEnrollmentRequest.getReferenceDateTime(), is(equalTo(expectedDateTime)));
        assertThat(actualEnrollmentRequest.getScheduleName(), is(equalTo(ScheduleNames.PNC_MOTHER_1)));
        assertThat(actualEnrollmentRequest.getEnrollmentDateTime(), is(equalTo(expectedDateTime)));
        assertThat(actualEnrollmentRequest.getPreferredAlertTime(), is(equalTo(null)));
        assertThat(actualEnrollmentRequest.getStartingMilestoneName(), is(equalTo(null)));
    }

    private PNCMotherRequest createTestPncRequest(Patient patient, Facility facility, MRSUser staff, DateTime date) {
        return new PNCMotherRequest().maleInvolved(Boolean.TRUE).patient(patient).ttDose("1").visit(PNC1).vitaminA("Y").comments("Comments")
                .community("House").date(date).facility(facility).staff(staff).location("Outreach").lochiaAmountExcess(Boolean.TRUE)
                .lochiaColour("1").lochiaOdourFoul(Boolean.TRUE).temperature(10D);
    }

    @Test
    public void shouldAddObservationAndCreateScheduleForTTVaccine(){
        Patient patient = new Patient(new MRSPatient("100"));
        final DateTime vaccinationDate = DateUtil.newDateTime(2000, 1, 1, new Time(10, 10));
        ANCVisitRequest ancVisit = new ANCVisitRequest().date(vaccinationDate.toDate()).ttdose("1").patient(patient);
        final HashSet<MRSObservation> observations = new HashSet<MRSObservation>();
        motherVisitService.updateTT(ancVisit, observations);
        assertThat(observations.size(), is(equalTo(1)));
        assertThat(observations.iterator().next(), is(Matchers.equalTo(new MRSObservation(vaccinationDate.toDate(), Concept.TT.getName(), 1))));

        ArgumentCaptor<TTVaccine> ttVaccineArgumentCaptor = ArgumentCaptor.forClass(TTVaccine.class);
        verify(mockVisitService).createTTSchedule(ttVaccineArgumentCaptor.capture());
        TTVaccineTest.assertIfTTVaccineAreEqual(new TTVaccine(vaccinationDate, TTVaccineDosage.TT1, patient), ttVaccineArgumentCaptor.getValue());
    }

    // 2 scenarios - in both case IPT1 is created and fulfilled and send alerts for remaining weeks
    // i) User enrolled after 19th week
    // ii) User enrolled for SP2, taking IPT2 directly based on some pre-history
    @Test
    public void shouldEnrollOrFulfilIPTpScheduleBasedOnVisitDate_AndRecordObservations() {

        Time deliveryTime = new Time(20, 2);
        DateTime today = new DateTime(2012, 2, 1, deliveryTime.getHour(), deliveryTime.getMinute());
        LocalDate visitDate = today.minusDays(10).toLocalDate();
        Pregnancy pregnancy = basedOnDeliveryDate(new LocalDate(2012, 9, 1));
        IPTDose dose2 = IPTDose.SP2;
        ANCVisitRequest ancVisit = createTestANCVisit().date(visitDate.toDate()).iptdose(dose2.value().toString()).iptReactive(true)
                .estDeliveryDate(pregnancy.dateOfDelivery().toDate());
        String mrsPatientId = ancVisit.getPatient().getMRSPatientId();

        mockCurrentDate(today);
        motherVisitService.registerANCVisit(ancVisit);

        ArgumentCaptor<Encounter> encounterCaptor = forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCaptor.capture());
        assertIfObservationsAvailableForConcepts(true, encounterCaptor.getValue().getObservations(), IPT.getName(), IPT_REACTION.getName());

        ArgumentCaptor<EnrollmentRequest> captor = forClass(EnrollmentRequest.class);
        verify(mockAllSchedules).enrollOrFulfill(captor.capture(), eq(visitDate));
        assertEnrollmentRequest(new EnrollmentRequest(mrsPatientId, ANC_IPT_VACCINE, deliveryTime, visitDate, deliveryTime, visitDate, null, dose2.milestone()), captor.getAllValues().get(0));
    }

    @Test
    public void shouldNotCreateIPTSchedule_IfIPTReadingsAreNotCaptured() {
        Time deliveryTime = new Time(20, 2);
        mockCurrentDate(new DateTime(2012, 2, 1, deliveryTime.getHour(), deliveryTime.getMinute()));
        LocalDate visitDate = now().minusDays(10).toLocalDate();

        ANCVisitRequest ancVisit = createTestANCVisit().date(visitDate.toDate()).iptdose(null).iptReactive(false).patient(new Patient(new MRSPatient("patientId")));
        motherVisitService.registerANCVisit(ancVisit);

        ArgumentCaptor<Encounter> encounterCaptor = forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCaptor.capture());
        assertIfObservationsAvailableForConcepts(false, encounterCaptor.getValue().getObservations(), IPT.getName(), IPT_REACTION.getName());
        verify(mockAllSchedules, never()).fulfilCurrentMilestone(ancVisit.getPatient().getMRSPatientId(), ANC_IPT_VACCINE, visitDate);
    }

    private void assertIfObservationsAvailableForConcepts(Boolean present, Set<MRSObservation> observations, String... conceptNames) {
        for (String conceptName : conceptNames)
            if (present)
                assertNotNull("concept not present:" + conceptName, selectFirst(observations, having(on(MRSObservation.class).getConceptName(), equalTo(conceptName))));
            else
                assertNull("concept present:" + conceptName, selectFirst(observations, having(on(MRSObservation.class).getConceptName(), equalTo(conceptName))));
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
