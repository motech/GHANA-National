package org.motechproject.ghana.national.repository;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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
        String scheduleName = "scheduleName";

        allSchedules.unEnroll(patientId, scheduleName);

        verify(mockScheduleTrackingService).unenroll(patientId, asList(scheduleName));
    }

    @Test
    public void shouldUnEnrollListOfSchedules() {
        String patientId = "12";
        List<String> scheduleNames = Arrays.asList("scheduleName1", "scheduleName2", "scheduleName3");

        allSchedules.unEnroll(patientId, scheduleNames);

        verify(mockScheduleTrackingService).unenroll(patientId, scheduleNames);
    }

    @Test
    public void shouldEnrollAndFulfilIfNoEnrollmentsWereFoundForTheSchedule() {
        String scheduleName = "scheduleName";
        String externalId = "12";
        EnrollmentRequest anyEnrollmentRequest = new EnrollmentRequest(externalId, scheduleName, new Time(12, 0), new LocalDate(), null, null, null, null, null);
        when(mockScheduleTrackingService.getEnrollment(externalId, scheduleName)).thenReturn(null);

        allSchedules.enrollOrFulfill(anyEnrollmentRequest, null);

        verify(mockScheduleTrackingService).enroll(anyEnrollmentRequest);
        verify(mockScheduleTrackingService).fulfillCurrentMilestone(externalId, scheduleName, null);
    }

    @Test
    public void shouldNotEnrollWhileFulfillingIfEnrollmentsWereFoundForTheSchedule() {
        String scheduleName = "scheduleName";
        String externalId = "12";
        EnrollmentRequest anyEnrollmentRequest = new EnrollmentRequest(externalId, scheduleName, new Time(12, 0), new LocalDate(), null, null, null, null, null);
        EnrollmentRecord notNullEnrollment = mock(EnrollmentRecord.class);
        when(mockScheduleTrackingService.getEnrollment(externalId, scheduleName)).thenReturn(notNullEnrollment);

        allSchedules.enrollOrFulfill(anyEnrollmentRequest, null);

        verify(mockScheduleTrackingService, never()).enroll(anyEnrollmentRequest);
        verify(mockScheduleTrackingService).fulfillCurrentMilestone(externalId, scheduleName, null);
    }

    @Test
    public void shouldEnrollForSchedule() {
        EnrollmentRequest request = new EnrollmentRequest("123", "sche", new Time(12, 0), new LocalDate(), null, null, null, null, null);
        allSchedules.enroll(request);
        verify(mockScheduleTrackingService).enroll(request);
    }

    @Test
    public void shouldFulfilMilestoneForSchedule() {
        String externalId = "123";
        String scheduleName = "scheduleName";
        LocalDate fulfillmentDate = new LocalDate();

        allSchedules.fulfilCurrentMilestone(externalId, scheduleName, fulfillmentDate);
        verify(mockScheduleTrackingService).fulfillCurrentMilestone(externalId, scheduleName, fulfillmentDate);

        reset(mockScheduleTrackingService);

        Time fulfillmentTime = new Time(2, 3);
        allSchedules.fulfilCurrentMilestone(externalId, scheduleName, fulfillmentDate, fulfillmentTime);
        verify(mockScheduleTrackingService).fulfillCurrentMilestone(externalId, scheduleName, fulfillmentDate, fulfillmentTime);
    }

    @Test
    public void shouldFetchEnrollmentForSchedule() {
        EnrollmentRequest request = new EnrollmentRequest("123", "scheduleName", new Time(12, 0), new LocalDate(), null, null, null, null, null);
        allSchedules.enrollment(request);
        verify(mockScheduleTrackingService).getEnrollment(request.getExternalId(), request.getScheduleName());
    }

    @Test
    public void shouldFetchEnrollmentAlongWithAllStartWindowDateInfo() {
        EnrollmentsQuery enrollmentsQuery = mock(EnrollmentsQuery.class);
        allSchedules.search(enrollmentsQuery);
        verify(mockScheduleTrackingService).searchWithWindowDates(enrollmentsQuery);
    }

    @Test
    public void shouldFulfilCurrentMilestoneSafely() {
        doNothing().when(mockScheduleTrackingService).fulfillCurrentMilestone(Matchers.<String>any(), Matchers.<String>any(), Matchers.<LocalDate>any());
        assertTrue(allSchedules.safeFulfilCurrentMilestone("id", "some name", null));

        reset(mockScheduleTrackingService);

        doThrow(new InvalidEnrollmentException("not exists")).when(mockScheduleTrackingService).fulfillCurrentMilestone(Matchers.<String>any(), Matchers.<String>any(), Matchers.<LocalDate>any());
        assertFalse(allSchedules.safeFulfilCurrentMilestone("id", "some name", null));
    }

    @Test
    public void shouldReturnActiveEnrollmentToAScheduleGivenAnEnrollmentIdAndScheduleName(){
        String externalId = "external id";
        String scheduleName = "schedule name";
        EnrollmentRecord enrollmentRecord = mock(EnrollmentRecord.class);

        when(mockScheduleTrackingService.getEnrollment(externalId, scheduleName)).thenReturn(enrollmentRecord);
        assertThat(enrollmentRecord, is(equalTo(allSchedules.getActiveEnrollment(externalId, scheduleName))));
    }
}
