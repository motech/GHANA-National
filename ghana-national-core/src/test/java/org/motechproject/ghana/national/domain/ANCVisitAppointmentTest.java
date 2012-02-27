package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.model.TypeOfVisit;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.ghana.national.repository.AllAppointments;
import org.motechproject.mrs.model.MRSPatient;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ANCVisitAppointmentTest {

    @Mock
    AppointmentService mockAppointmentService;
    AllAppointments allAppointments;

    @Before
    public void setup() {
        initMocks(this);
        allAppointments = new AllAppointments();
        ReflectionTestUtils.setField(allAppointments, "appointmentService", mockAppointmentService);
    }

    @Test
    public void shouldFulfilCurrentVisitSchedule() {
        String motechId = "motech id";
        allAppointments.fulfilVisit(new Patient(new MRSPatient(motechId, null, null)));
        verify(mockAppointmentService).removeCalendar(motechId);
    }

    @Test
    public void shouldCreateANCVisitSchedule() {
        String motechId = "1234556";
        DateTime today = DateTime.now();
        allAppointments.createANCVisitSchedule(new Patient(new MRSPatient(motechId, null, null)), today);

        ArgumentCaptor<AppointmentCalendarRequest> calendarCaptor = ArgumentCaptor.forClass(AppointmentCalendarRequest.class);
        ArgumentCaptor<ReminderConfiguration> reminderCaptor = ArgumentCaptor.forClass(ReminderConfiguration.class);
        ArgumentCaptor<DateTime> dateCaptor = ArgumentCaptor.forClass(DateTime.class);
        verify(mockAppointmentService).addCalendar(calendarCaptor.capture());
        verify(mockAppointmentService, times(3)).addVisit(eq(motechId), dateCaptor.capture(), reminderCaptor.capture(), eq(TypeOfVisit.Scheduled));

        AppointmentCalendarRequest actualAppointmentRequest = calendarCaptor.getValue();
        List<DateTime> allActualScheduleDates = dateCaptor.getAllValues();
        ReminderConfiguration actualReminderConfiguration = reminderCaptor.getValue();

        assertThat(actualAppointmentRequest.getExternalId(), is(motechId));
        assertThat(today, is(allActualScheduleDates.get(0)));
        assertThat(today.plusWeeks(1), is(allActualScheduleDates.get(1)));
        assertThat(today.plusWeeks(3), is(allActualScheduleDates.get(2)));
        assertThat(actualReminderConfiguration.getIntervalUnit(), is(ReminderConfiguration.IntervalUnit.WEEKS));
        assertThat(actualReminderConfiguration.getIntervalCount(), is(1));
    }
}
