package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.builder.MobileMidwifeEnrollmentBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.PhoneOwnership;
import org.motechproject.ghana.national.repository.AllCampaigns;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateTimeSourceUtil;
import org.motechproject.util.DateUtil;
import org.motechproject.util.datetime.DateTimeSource;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MobileMidwifeServiceTest {
    private MobileMidwifeService service;
    @Mock
    private AllMobileMidwifeEnrollments mockAllMobileMidwifeEnrollments;
    @Mock
    private AllCampaigns mockAllCampaigns;

    public MobileMidwifeServiceTest() {
        initMocks(this);
        service = new MobileMidwifeService(mockAllMobileMidwifeEnrollments, mockAllCampaigns);
    }

    @Test
    public void shouldCreateMobileMidwifeEnrollmentAndCreateScheduleIfNotRegisteredAlready() {
        String patientId = "patientId";
        mockNow(DateUtil.now());
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().facilityId("facility12").
                patientId(patientId).staffId("staff13").consent(true).dayOfWeek(DayOfWeek.Thursday).phoneOwnership(PhoneOwnership.HOUSEHOLD)
                .build();
        when(mockAllMobileMidwifeEnrollments.findActiveBy(patientId)).thenReturn(null);
        when(mockAllCampaigns.nearestCycleDate(enrollment)).thenReturn(enrollment.getEnrollmentDateTime());

        service.register(enrollment);
        assertThat(DateUtil.newDate(enrollment.getEnrollmentDateTime()).toDate(), is(DateUtil.today().toDate()));

        verifyCreateNewEnrollment(enrollment);
        verify(mockAllCampaigns).start(enrollment);
    }

    private void mockNow(final DateTime now) {
        DateTimeSourceUtil.SourceInstance = new DateTimeSource() {
            @Override
            public DateTimeZone timeZone() {
                return DateTimeZone.getDefault();
            }

            @Override
            public DateTime now() {
                return now;
            }

            @Override
            public LocalDate today() {
                return now.toLocalDate();
            }
        };
    }

    private void verifyCreateNewEnrollment(MobileMidwifeEnrollment enrollment) {
        verify(mockAllCampaigns).nearestCycleDate(enrollment);
        verify(mockAllMobileMidwifeEnrollments).add(enrollment);
    }

    @Test
    public void shouldCreateNewScheduleOnlyIfEnrolledWithConsentYes() {
        MobileMidwifeEnrollment enrollmentWithNoConsent = new MobileMidwifeEnrollmentBuilder().facilityId("facility12").
                patientId("patienId").staffId("staff13").consent(false).phoneOwnership(PhoneOwnership.PERSONAL).build();
        service.register(enrollmentWithNoConsent);
        verify(mockAllMobileMidwifeEnrollments).add(enrollmentWithNoConsent);
        verify(mockAllCampaigns, never()).start(enrollmentWithNoConsent);
    }

    @Test
    public void shouldStopScheduleOnlyIfEnrolledWithConsentYes() {
        String patientId = "patienId";
        MobileMidwifeEnrollment existingEnrollmentWithNoConsent = new MobileMidwifeEnrollmentBuilder().facilityId("facility12").
                patientId(patientId).staffId("staff13").consent(false).phoneOwnership(PhoneOwnership.PERSONAL).build();
        when(mockAllMobileMidwifeEnrollments.findActiveBy(patientId)).thenReturn(existingEnrollmentWithNoConsent);

        MobileMidwifeEnrollment newEnrollment = new MobileMidwifeEnrollmentBuilder().patientId(patientId)
                .phoneOwnership(PhoneOwnership.HOUSEHOLD).consent(true).build();
        when(mockAllCampaigns.nearestCycleDate(newEnrollment)).thenReturn(newEnrollment.getEnrollmentDateTime());
        service.register(newEnrollment);

        verify(mockAllMobileMidwifeEnrollments).update(existingEnrollmentWithNoConsent);
        verify(mockAllCampaigns, never()).stop(existingEnrollmentWithNoConsent);
    }

    @Test
    public void shouldDeactivateExistingEnrollmentAndCampaign_AndCreateNewEnrollmentIfEnrolledAlready() {
        String patientId = "patientId";
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().facilityId("facility12").
                patientId(patientId).staffId("staff13").consent(true).dayOfWeek(DayOfWeek.Thursday).phoneOwnership(PhoneOwnership.PERSONAL)
                .build();
        MobileMidwifeEnrollment existingEnrollment = new MobileMidwifeEnrollmentBuilder().facilityId("facility12").
                patientId(patientId).consent(true).phoneOwnership(PhoneOwnership.HOUSEHOLD).build();
        when(mockAllMobileMidwifeEnrollments.findActiveBy(patientId)).thenReturn(existingEnrollment);
        when(mockAllCampaigns.nearestCycleDate(enrollment)).thenReturn(enrollment.getEnrollmentDateTime());

        service = spy(service);

        service.register(enrollment);
        assertTrue(enrollment.getActive());
        verify(service).unRegister(patientId);
        verifyCreateNewEnrollment(enrollment);
        verify(mockAllCampaigns).start(enrollment);
    }

    @Test
    public void shouldDeactivateEnrollmentAndClearScheduleOnUnregister() {
        String patientId = "patientId";
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().facilityId("facility12").
                patientId(patientId).staffId("staff13").consent(true).dayOfWeek(DayOfWeek.Thursday).phoneOwnership(PhoneOwnership.PERSONAL)
                .build();
        when(mockAllMobileMidwifeEnrollments.findActiveBy(patientId)).thenReturn(enrollment);

        service.unRegister(patientId);
        assertFalse(enrollment.getActive());
        verify(mockAllMobileMidwifeEnrollments).update(enrollment);
        verify(mockAllCampaigns).stop(enrollment);
    }

    @Test
    public void shouldFindMobileMidwifeEnrollmentByPatientId() {
        String patientId = "patientId";
        service.findActiveBy(patientId);
        verify(mockAllMobileMidwifeEnrollments).findActiveBy(patientId);
    }

    @Test
    public void shouldFindLatestMobileMidwifeEnrollmentByPatientId() {
        String patientId = "patientId";
        service.findLatestEnrollment(patientId);
        verify(mockAllMobileMidwifeEnrollments).findLatestEnrollment(patientId);
    }

    @Test
    public void shouldCreateEnrollmentAndNotCreateScheduleIfUsersPhoneOwnership_IsPUBLIC() {
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().consent(true).phoneOwnership(PhoneOwnership.PUBLIC).build();

        service.register(enrollment);
        verify(mockAllMobileMidwifeEnrollments).add(enrollment);
        verify(mockAllCampaigns, never()).nearestCycleDate(enrollment);
        verify(mockAllCampaigns, never()).start(enrollment);
    }

}
