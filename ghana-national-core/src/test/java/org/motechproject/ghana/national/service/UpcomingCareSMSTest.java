package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.contract.VisitResponse;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.sms.UpcomingCareSMS;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.sms.UpcomingCareSMS.UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY;
import static org.motechproject.ghana.national.domain.sms.UpcomingCareSMS.UPCOMING_CARE_DUE_CLIENT_QUERY;
import static org.motechproject.util.DateUtil.newDateTime;

public class    UpcomingCareSMSTest {
    @Mock
    private SMSGateway mockSmsGateway;
    @Mock
    private AllSchedules mockAllSchedules;
    private UpcomingCareSMS upcomingCareSMS;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldSendUpcomingCareDetailsIfQueryTypeIsUpcomingCare() {
        String mrsPatientId = "patientId";
        final String motechId = "motech-id";
        final String firstName = "Sachin";
        final String lastName = "A";
        final Patient patient = new Patient(new MRSPatient(mrsPatientId, motechId, new MRSPerson().gender("M").firstName(firstName).lastName(lastName)
                .dateOfBirth(newDateTime(2012, 4, 5, 2, 2, 0).toDate()), null));

        EnrollmentRecord ttEnrollmentRecord = enrollment("TT schedule", newDateTime(2012, 3, 3, 1, 1, 0));
        EnrollmentRecord iptEnrollmentRecord = enrollment("IPT schedule", newDateTime(2012, 4, 5, 2, 2, 0));
        List<EnrollmentRecord> upcomingEnrollments = asList(ttEnrollmentRecord, iptEnrollmentRecord);
        List<VisitResponse> upcomingAppointments = asList(new VisitResponse().setAppointmentDueDate(newDateTime(2012, 4, 9, 3, 3, 0)).setName("Appointment 1"),
                new VisitResponse().setAppointmentDueDate(newDateTime(2012, 4, 8, 1, 2, 0)).setName("Appointment 2"));

        mockSuccessSmsTemplateMessages();
        String expectedCareSchedule = "TT schedule: 03 Mar 2012, IPT schedule: 05 Apr 2012";
        String expectedAppointmentsSchedule = "Appointment 1: 09 Apr 2012, Appointment 2: 08 Apr 2012";

        upcomingCareSMS = new UpcomingCareSMS(mockSmsGateway, patient, upcomingEnrollments, upcomingAppointments);
        String duesText = expectedCareSchedule + ", " + expectedAppointmentsSchedule;
        assertThat(upcomingCareSMS.smsText(), is(format("Upcoming care programs for %s %s (MotechID:%s):\\n %s.", firstName, lastName, motechId, duesText)));

        upcomingCareSMS = new UpcomingCareSMS(mockSmsGateway, patient, upcomingEnrollments, Collections.<VisitResponse>emptyList());
        assertThat(upcomingCareSMS.smsText(), is(format("Upcoming care programs for %s %s (MotechID:%s):\\n %s.", firstName, lastName, motechId, expectedCareSchedule)));

        upcomingCareSMS = new UpcomingCareSMS(mockSmsGateway, patient, Collections.<EnrollmentRecord>emptyList(), upcomingAppointments);
        assertThat(upcomingCareSMS.smsText(), is(format("Upcoming care programs for %s %s (MotechID:%s):\\n %s.", firstName, lastName, motechId, expectedAppointmentsSchedule)));
    }

    private EnrollmentRecord enrollment(String enrollmentName, DateTime dueStart) {
        return new EnrollmentRecord("", enrollmentName, "", null, null, null, null, dueStart, null, null);
    }

    private void mockSuccessSmsTemplateMessages() {
        when(mockSmsGateway.getSMSTemplate(UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY))
                .thenReturn("Upcoming care programs for ${firstName} ${lastName} (MotechID:${motechId}):\\n $T{UPCOMING_CARE_DUE_CLIENT_QUERY}.");
        when(mockSmsGateway.getSMSTemplate(UPCOMING_CARE_DUE_CLIENT_QUERY))
                .thenReturn("${scheduleName}: ${date}");
    }

    @Test
    public void shouldSendNoResultsFoundIfNoUpcomingCareDetailsFound() {
        final String motechId = "motech-id";
        final String mrsPatientId = "patient-id";
        final String firstName = "Sachin";
        final String lastName = "A";

        final Patient patient = new Patient(new MRSPatient(mrsPatientId, motechId, new MRSPerson().gender("M").firstName(firstName).lastName(lastName)
                .dateOfBirth(newDateTime(2012, 4, 5, 2, 2, 0).toDate()), null));
        List<EnrollmentRecord> upcomingEnrollments = Collections.emptyList();
        List<VisitResponse> upcomingAppointments = Collections.emptyList();

        when(mockSmsGateway.getSMSTemplate(UpcomingCareSMS.NONE_UPCOMING)).thenReturn("None upcoming for MotechID:${motechId}");

        upcomingCareSMS = new UpcomingCareSMS(mockSmsGateway, patient, upcomingEnrollments, upcomingAppointments);

        String expectedMsg = format("None upcoming for MotechID:%s", motechId);
        assertThat(upcomingCareSMS.smsText(), is(expectedMsg));
    }
}
