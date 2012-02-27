package org.motechproject.ghana.national.repository;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllSchedulesTest {

    @Mock
    ScheduleTrackingService mockScheduleTrackingService;

    AllSchedules allSchedules;

    @Before
    public void setUp() {
        initMocks(this);
        allSchedules = new AllSchedules(mockScheduleTrackingService);
    }

    @Test
    public void shouldEnroll() {
        EnrollmentRequest mockEnrollmentRequest = Matchers.any();
        allSchedules.enroll(mockEnrollmentRequest);
        verify(mockScheduleTrackingService).enroll(mockEnrollmentRequest);
    }

    @Test
    public void shouldUnEnroll() {
        String patientId = "12";
        Patient mockPatient = new Patient(new MRSPatient(patientId));
        String scheduleName = "scheduleName";

        allSchedules.unEnroll(mockPatient, scheduleName);

        verify(mockScheduleTrackingService).unenroll(patientId, scheduleName);
    }

    @Test
    public void shouldEnrollAndFulfilIfNoEnrollmentsWereFoundForTheSchedule() {
        String scheduleName = "scheduleName";
        EnrollmentRequest anyEnrollmentRequest = new EnrollmentRequest("123", scheduleName, new Time(12, 0), new LocalDate());
        String patientId = "12";
        Patient mockPatient = new Patient(new MRSPatient(patientId));
        when(mockScheduleTrackingService.getEnrollment(patientId, scheduleName)).thenReturn(null);

        allSchedules.enrollOrFulfill(mockPatient, anyEnrollmentRequest);

        verify(mockScheduleTrackingService).enroll(anyEnrollmentRequest);
        verify(mockScheduleTrackingService).fulfillCurrentMilestone(patientId, scheduleName);
    }

    @Test
    public void shouldNotEnrollWhileFulfillingIfEnrollmentsWereFoundForTheSchedule() {
        String scheduleName = "scheduleName";
        EnrollmentRequest anyEnrollmentRequest = new EnrollmentRequest("123", scheduleName, new Time(12, 0), new LocalDate());
        String patientId = "12";
        Patient mockPatient = new Patient(new MRSPatient(patientId));
        EnrollmentResponse notNullEnrollment =mock(EnrollmentResponse.class);
        when(mockScheduleTrackingService.getEnrollment(patientId, scheduleName)).thenReturn(notNullEnrollment);

        allSchedules.enrollOrFulfill(mockPatient, anyEnrollmentRequest);

        verify(mockScheduleTrackingService, never()).enroll(anyEnrollmentRequest);
        verify(mockScheduleTrackingService).fulfillCurrentMilestone(patientId, scheduleName);
    }

    @Test
    public void shouldEnrollForSchedule() {
        EnrollmentRequest request = new EnrollmentRequest("123", "sche", new Time(12, 0), new LocalDate());
        allSchedules.enroll(request);
        verify(mockScheduleTrackingService).enroll(request);
    }

    @Test
    public void shouldFulfilMilestoneForSchedule() {
        EnrollmentRequest request = new EnrollmentRequest("123", "scheduleName", new Time(12, 0), new LocalDate());
        allSchedules.fulfilMilestone(request);
        verify(mockScheduleTrackingService).fulfillCurrentMilestone(request.getExternalId(), request.getScheduleName());
    }

    @Test
    public void shouldFetchEnrollmentForSchedule() {
        EnrollmentRequest request = new EnrollmentRequest("123", "scheduleName", new Time(12, 0), new LocalDate());
        allSchedules.enrollment(request);
        verify(mockScheduleTrackingService).getEnrollment(request.getExternalId(), request.getScheduleName());
    }
}
