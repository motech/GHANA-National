package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.service.MobileClientQueryService.UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY;
import static org.motechproject.ghana.national.service.MobileClientQueryService.UPCOMING_CARE_DUE_CLIENT_QUERY;
import static org.motechproject.util.DateUtil.newDateTime;

public class MobileClientQueryServiceTest {
    @Mock
    private SMSGateway mockSmsGateway;
    @Mock
    private AllSchedules mockAllSchedules;
    private MobileClientQueryService serviceSpy;

    @Before
    public void setUp() {
        initMocks(this);
        serviceSpy = spy(new MobileClientQueryService(mockAllSchedules, mockSmsGateway));
    }

    @Test
    public void shouldSendUpcomingCareDetailsIfQueryTypeIsUpcomingCare() {
        String mrsPatientId = "patientId";
        final String motechId = "motech-id";
        String responsePhoneNumber = "94423232";
        final String firstName = "Sachin";
        final String lastName = "A";

        final Patient patient = new Patient(new MRSPatient(mrsPatientId, motechId, new MRSPerson().gender("M").firstName(firstName).lastName(lastName)
                .dateOfBirth(newDateTime(2012, 4, 5, 2, 2, 0).toDate()), null));
        EnrollmentRecord ttEnrollmentRecord = new EnrollmentRecord("", "TT schedule", "", null, null, null, null, newDateTime(2012, 3, 3, 1, 1, 0), null, null);
        EnrollmentRecord iptEnrollmentRecord = new EnrollmentRecord("", "IPT schedule", "", null, null, null, null, newDateTime(2012, 4, 5, 2, 2, 0), null, null);
        List<EnrollmentRecord> upcomingEnrollments = asList(ttEnrollmentRecord, iptEnrollmentRecord);

        when(mockAllSchedules.upcomingCareForCurrentWeek(patient.getMRSPatientId())).thenReturn(upcomingEnrollments);
        when(mockSmsGateway.getSMSTemplate(UPCOMING_CARE_CLIENT_QUERY_RESPONSE_SMS_KEY))
                .thenReturn("Upcoming care programs for ${firstName} ${lastName} (MotechID:${motechId}):\\n $T{UPCOMING_CARE_DUE_CLIENT_QUERY}.");
        when(mockSmsGateway.getSMSTemplate(UPCOMING_CARE_DUE_CLIENT_QUERY))
                .thenReturn("${scheduleName}: ${date}");

        serviceSpy.queryUpcomingCare(patient, responsePhoneNumber);
        String expectedMsg = format("Upcoming care programs for %s %s (MotechID:%s):\\n %s."
                , firstName, lastName, motechId, "TT schedule: 03 Mar 2012, IPT schedule: 05 Apr 2012");
        verify(mockSmsGateway).dispatchSMS(eq(responsePhoneNumber), eq(expectedMsg));
    }

    @Test
    public void shouldSendNoResultsFoundIfNoUpcomingCareDetailsFound() {
        final String motechId = "motech-id";
        final String mrsPatientId = "patient-id";
        String responsePhoneNumber = "94423232";
        final String firstName = "Sachin";
        final String lastName = "A";

        final Patient patient = new Patient(new MRSPatient(mrsPatientId, motechId, new MRSPerson().gender("M").firstName(firstName).lastName(lastName)
                .dateOfBirth(newDateTime(2012, 4, 5, 2, 2, 0).toDate()), null));
        List<EnrollmentRecord> upcomingEnrollments = Collections.emptyList();
        when(mockAllSchedules.upcomingCareForCurrentWeek(patient.getMRSPatientId())).thenReturn(upcomingEnrollments);
        when(mockSmsGateway.getSMSTemplate(MobileClientQueryService.NONE_UPCOMING)).thenReturn("None upcoming for MotechID:${motechId}");

        serviceSpy.queryUpcomingCare(patient, responsePhoneNumber);

        String expectedMsg = "None upcoming for MotechID:%s".format(motechId);
        mockSmsGateway.dispatchSMS(eq(responsePhoneNumber), eq(expectedMsg));
    }
}
