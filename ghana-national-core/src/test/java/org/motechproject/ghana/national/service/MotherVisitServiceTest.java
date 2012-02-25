package org.motechproject.ghana.national.service;

import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.vo.ANCVisit;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.Date;
import java.util.Set;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.DELIVERY;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION_VISIT;
import static org.motechproject.ghana.national.domain.Concept.EDD;
import static org.motechproject.ghana.national.domain.Concept.PREGNANCY;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_VISIT;
import static org.motechproject.ghana.national.domain.TTVaccineDosage.TT2;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class MotherVisitServiceTest {
    private MotherVisitService motherVisitServiceSpy;

    @Mock
    private AllEncounters mockAllEncounters;
    @Mock
    private AllObservations mockAllObservations;
    @Mock
    private AllSchedules mockAllSchedules;


    @Before
    public void setUp() throws Exception {
        initMocks(this);
        motherVisitServiceSpy = spy(new MotherVisitService(mockAllEncounters, mockAllObservations, mockAllSchedules));
    }

    @Test
    public void shouldCreateEncounter_EnrollPatientForCurrentScheduleAndCreateSchedulesForTheNext() {
        MRSUser staff = new MRSUser();
        Facility facility = new Facility();
        final String patientId = "patient id";
        final Patient patient = new Patient(new MRSPatient(patientId, null, null));
        final LocalDate vaccinationDate = DateUtil.newDate(2000, 2, 1);
        motherVisitServiceSpy.receivedTT(TT2, patient, staff, facility, vaccinationDate);

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedules).enrollOrFulfill(eq(patient), enrollmentRequestCaptor.capture());

        EnrollmentRequest enrollmentRequest = enrollmentRequestCaptor.getValue();
        assertThat(enrollmentRequest.getScheduleName(), is(equalTo(TT_VACCINATION_VISIT)));
        assertThat(enrollmentRequest.getStartingMilestoneName(), is(equalTo(TT2.name())));
        assertThat(enrollmentRequest.getReferenceDate(), is(equalTo(vaccinationDate)));
    }

    @Test
    public void shouldCreateEncounterForANCVisitWithAllInfo() {
        Patient mockPatient = mock(Patient.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        String mrsFacilityId = "mrsFacilityId";
        Facility facility = new Facility(new MRSFacility(mrsFacilityId));
        facility.mrsFacilityId(mrsFacilityId);
        MRSUser staff = new MRSUser();
        ANCVisit ancVisit = createTestANCVisit(staff, facility, mockPatient);
        String mrsPatientId = "34";
        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockMRSPatient.getId()).thenReturn(mrsPatientId);

        motherVisitServiceSpy.registerANCVisit(ancVisit);

        ArgumentCaptor<Encounter> encounterCapture = ArgumentCaptor.forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCapture.capture());

        Encounter encounter = encounterCapture.getValue();
        assertThat(encounter.getStaff().getId(), Is.is(ancVisit.getStaff().getId()));
        assertThat(encounter.getMrsPatient().getId(), Is.is(mrsPatientId));
        assertThat(encounter.getFacility().getId(), Is.is(ancVisit.getFacility().getMrsFacilityId()));
        assertThat(encounter.getType(), Is.is(ANC_VISIT.value()));
        assertReflectionEquals(encounter.getDate(), DateUtil.today().toDate(), ReflectionComparatorMode.LENIENT_DATES);
    }

    @Test
    public void shouldCreateEDDObservationsIfModified() {
        ANCVisit ancVisit = createTestANCVisit();
        MRSObservation edd = mock(MRSObservation.class);
        MRSObservation activePregnancy = mock(MRSObservation.class);
        when(edd.getValue()).thenReturn(DateUtil.newDate(2010, 12, 11).toDate());
        String motechId = ancVisit.getPatient().getMotechId();

        when(mockAllObservations.findObservation(motechId, PREGNANCY.getName())).thenReturn(activePregnancy);
        when(mockAllObservations.findObservation(motechId, EDD.getName())).thenReturn(edd);

        Set<MRSObservation> eddObservations = motherVisitServiceSpy.updatedEddObservations(ancVisit.getEstDeliveryDate(), ancVisit.getPatient(), ancVisit.getStaff().getId());

        assertTrue(CollectionUtils.isNotEmpty(eddObservations));
        verify(mockAllObservations).findObservation(motechId, PREGNANCY.getName());
        verify(mockAllObservations).voidObservation(eq(edd), anyString(), eq("staffId"));
    }

    @Test
    public void shouldNotCreateEDDObservationsIfNotModified() {
        ANCVisit ancVisit = createTestANCVisit();
        ancVisit.estDeliveryDate(null);
        MRSObservation edd = mock(MRSObservation.class);
        MRSObservation activePregnancy = mock(MRSObservation.class);
        when(edd.getValue()).thenReturn(DateUtil.newDate(2010, 12, 11).toDate());
        String motechId = ancVisit.getPatient().getMotechId();
        when(mockAllObservations.findObservation(motechId, PREGNANCY.getName())).thenReturn(activePregnancy);
        when(mockAllObservations.findObservation(motechId, EDD.getName())).thenReturn(edd);

        Set<MRSObservation> eddObservations = motherVisitServiceSpy.updatedEddObservations(ancVisit.getEstDeliveryDate(), ancVisit.getPatient(), ancVisit.getStaff().getId());

        assertTrue(CollectionUtils.isEmpty(eddObservations));
        verify(mockAllObservations, never()).findObservation(motechId, PREGNANCY.getName());
        verify(mockAllObservations, never()).voidObservation(eq(edd), anyString(), eq("staffId"));
    }

    @Test
    public void shouldNotRescheduleEDDIfEddIsNotModified() {
        ANCVisit ancVisit = createTestANCVisit();
        MRSObservation edd = mock(MRSObservation.class);
        MRSObservation activePregnancy = mock(MRSObservation.class);
        when(edd.getValue()).thenReturn(ancVisit.getEstDeliveryDate());
        String motechId = ancVisit.getPatient().getMotechId();
        when(mockAllObservations.findObservation(motechId, PREGNANCY.getName())).thenReturn(activePregnancy);
        when(mockAllObservations.findObservation(motechId, EDD.getName())).thenReturn(edd);

        motherVisitServiceSpy.updatedEddObservations(ancVisit.getEstDeliveryDate(), ancVisit.getPatient(), ancVisit.getStaff().getId());

        verify(mockAllObservations).findObservation(motechId, PREGNANCY.getName());
        verify(mockAllObservations, never()).voidObservation(eq(edd), anyString(), eq("staffId"));
    }

    @Test
    public void shouldCreateEDDScheduleForANCVisit() {
        String patientId = "1234567";
        Patient patient = new Patient(new MRSPatient(patientId));
        Date estimatedDateOfDelivery = new LocalDate(2012, 5, 1).toDate();
        motherVisitServiceSpy.createEDDScheduleForANCVisit(patient, estimatedDateOfDelivery);

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedules).enroll(enrollmentRequestCaptor.capture());

        EnrollmentRequest enrollmentRequest = enrollmentRequestCaptor.getValue();
        assertThat(enrollmentRequest.getScheduleName(), is(equalTo(DELIVERY)));
        assertThat(enrollmentRequest.getReferenceDate(), is(equalTo(new LocalDate(2011, 7, 26))));
    }

    private ANCVisit createTestANCVisit() {
        Patient mockPatient = mock(Patient.class);
        String mrsFacilityId = "mrsFacilityId";
        Facility facility = new Facility(new MRSFacility(mrsFacilityId));
        facility.mrsFacilityId(mrsFacilityId);
        MRSUser staff = new MRSUser();
        staff.id("staffId");
        return createTestANCVisit(staff, facility, mockPatient);
    }

    private ANCVisit createTestANCVisit(MRSUser staff, Facility facility, Patient patient) {
        return new ANCVisit().staff(staff).facility(facility).patient(patient).date(new Date()).serialNumber("4ds65")
                .visitNumber("4").estDeliveryDate(DateUtil.newDate(2012, 8, 8).toDate())
                .bpDiastolic(67).bpSystolic(10).weight(65.67d).comments("comments").ttdose("4").iptdose("5")
                .iptReactive("Y").itnUse("Y").fht(4.3d).fhr(4).urineTestGlucosePositive("0").urineTestProteinPositive("1")
                .hemoglobin(13.8).vdrlReactive("N").vdrlTreatment(null).dewormer("Y").pmtct("Y").preTestCounseled("N")
                .hivTestResult("hiv").postTestCounseled("Y").pmtctTreament("Y").location("34").house("house").community("community")
                .referred("Y").maleInvolved(false).nextANCDate(DateUtil.newDate(2012, 2, 20).toDate());
    }

}
