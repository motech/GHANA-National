package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.contract.AppointmentCalendarRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSPatient;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllAppointmentsTest {
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
        allAppointments.remove(new Patient(new MRSPatient(motechId, null, null)));
        verify(mockAppointmentService).removeCalendar(motechId);
    }

    @Test
    public void shouldCreateANCVisitSchedule() {
        String motechId = "1234556";
        DateTime today = DateTime.now();
        allAppointments.updateANCVisitSchedule(new Patient(new MRSPatient(motechId, null, null)), today);

        ArgumentCaptor<AppointmentCalendarRequest> calendarCaptor = ArgumentCaptor.forClass(AppointmentCalendarRequest.class);
        ArgumentCaptor<VisitRequest> visitCaptor = ArgumentCaptor.forClass(VisitRequest.class);
        ArgumentCaptor<String> visitNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockAppointmentService).removeCalendar(motechId);
        verify(mockAppointmentService).addCalendar(calendarCaptor.capture());
        verify(mockAppointmentService, times(3)).addVisit(eq(motechId), visitNameCaptor.capture(), visitCaptor.capture());

        AppointmentCalendarRequest actualAppointmentRequest = calendarCaptor.getValue();
        List<String> allVisitNames = visitNameCaptor.getAllValues();
        List<VisitRequest> allVisits = visitCaptor.getAllValues();

        assertThat(actualAppointmentRequest.getExternalId(), is(motechId));
        assertThat(today, is(allVisits.get(0).getDueDate()));
        assertThat(today.plusWeeks(1), is(allVisits.get(1).getDueDate()));
        assertThat(today.plusWeeks(3), is(allVisits.get(2).getDueDate()));
        assertThat(allVisitNames.get(0), is("DueVisit - " + motechId));
        assertThat(allVisitNames.get(1), is("LateVisit - " + motechId));
        assertThat(allVisitNames.get(2), is("MaxVisit - " + motechId));
        assertThat(allVisits.get(0).getReminderConfiguration().getIntervalUnit(), is(ReminderConfiguration.IntervalUnit.WEEKS));
        assertThat(allVisits.get(0).getReminderConfiguration().getIntervalCount(), is(1));
    }
}
