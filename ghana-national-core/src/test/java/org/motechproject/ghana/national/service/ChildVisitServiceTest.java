package org.motechproject.ghana.national.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.vo.CWCVisit;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Date;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.PENTA;
import static org.motechproject.ghana.national.configuration.ScheduleNames.YELLOW_FEVER;

public class ChildVisitServiceTest extends BaseUnitTest {
    private ChildVisitService service;
    @Mock
    AllEncounters mockAllEncounters;
    @Mock
    private AllSchedules mockAllSchedules;

    @Before
    public void setUp() {
        initMocks(this);
        service = new ChildVisitService(mockAllEncounters, mockAllSchedules);
    }

    @Test
    public void shouldCreateEncounterForCWCVisitWithAllInfo() {
        MRSUser staff = mock(MRSUser.class);
        Facility facility = mock(Facility.class);
        Patient patient = mock(Patient.class);
        when(patient.pentaPatientCare()).thenReturn(new PatientCare(PENTA, DateUtil.today()));
        CWCVisit cwcVisit = createTestCWCVisit(new Date(), staff, facility, patient);

        ChildVisitService spyService = spy(service);
        spyService.save(cwcVisit);

        ArgumentCaptor<Encounter> encounterCaptor = ArgumentCaptor.forClass(Encounter.class);
        verify(mockAllEncounters).persistEncounter(encounterCaptor.capture());
        verify(spyService).updatePentaSchedule(cwcVisit);
        verify(spyService).updateYellowFeverSchedule(cwcVisit);
    }

    @Test
    public void shouldFulfillPentaScheduleIfAlreadyEnrolled() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = mock(Patient.class);
        when(patient.getMRSPatientId()).thenReturn(mrsPatientId);
        EnrollmentResponse enrollmentResponse = mock(EnrollmentResponse.class);
        when(mockAllSchedules.enrollment(any(EnrollmentRequest.class))).thenReturn(enrollmentResponse);

        service.updatePentaSchedule(createTestCWCVisit(new Date(), mock(MRSUser.class), mock(Facility.class), patient));

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedules, never()).enroll(enrollmentRequestCaptor.capture());
        verify(mockAllSchedules).fulfilCurrentMilestone(eq(mrsPatientId), eq(ScheduleNames.PENTA), any(LocalDate.class));
    }

    @Test
    public void shouldNotFulfillPentaScheduleWhenPentaNotChosenInImmunization() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = mock(Patient.class);
        when(patient.getMRSPatientId()).thenReturn(mrsPatientId);
        EnrollmentResponse enrollmentResponse = mock(EnrollmentResponse.class);
        when(mockAllSchedules.enrollment(any(EnrollmentRequest.class))).thenReturn(enrollmentResponse);

        CWCVisit cwcVisit = createTestCWCVisit(new Date(), mock(MRSUser.class), mock(Facility.class), patient);
        cwcVisit.pentadose("");
        service.updatePentaSchedule(cwcVisit);

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedules, never()).enroll(enrollmentRequestCaptor.capture());
        verify(mockAllSchedules, never()).fulfilCurrentMilestone(eq(mrsPatientId), eq(ScheduleNames.PENTA), any(LocalDate.class));
    }

    @Test
    public void shouldEnrollAndFulfillPentaScheduleIfNotEnrolledEarlier() {
        String mrsPatientId = "mrsPatientId";
        Patient patient = mock(Patient.class);
        when(patient.pentaPatientCare()).thenReturn(new PatientCare(PENTA, DateUtil.today()));
        when(patient.getMRSPatientId()).thenReturn(mrsPatientId);
        when(mockAllSchedules.enrollment(any(EnrollmentRequest.class))).thenReturn(null);

        service.updatePentaSchedule(createTestCWCVisit(new Date(), mock(MRSUser.class), mock(Facility.class), patient));

        ArgumentCaptor<EnrollmentRequest> enrollmentRequestCaptor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(mockAllSchedules).enroll(enrollmentRequestCaptor.capture());
        verify(mockAllSchedules).fulfilCurrentMilestone(eq(mrsPatientId), eq(ScheduleNames.PENTA), any(LocalDate.class));
    }

    @Test
    public void shouldFulfillYellowFeverScheduleIfYellowFeverIsChosen() {
        String mrsPatientId = "1234";
        CWCVisit testCWCVisit = createTestCWCVisit(new Date(), new MRSUser(), mock(Facility.class), new Patient(new MRSPatient(mrsPatientId)));
        testCWCVisit.immunizations(asList(Concept.YF.name()));

        service.updateYellowFeverSchedule(testCWCVisit);

        verify(mockAllSchedules).fulfilCurrentMilestone(mrsPatientId, YELLOW_FEVER, DateUtil.newDate(testCWCVisit.getDate()));
    }

    private CWCVisit createTestCWCVisit(Date registrationDate, MRSUser staff, Facility facility, Patient patient) {
        CWCVisit cwcVisit = new CWCVisit();
        return cwcVisit.staff(staff).facility(facility).patient(patient).date(registrationDate).serialNumber("4ds65").pentadose("2")
                .weight(65.67d).comments("comments").cwcLocation("34").house("house").community("community").maleInvolved(false);
    }

}
