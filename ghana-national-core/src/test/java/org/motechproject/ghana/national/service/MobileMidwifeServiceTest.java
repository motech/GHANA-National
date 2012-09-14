package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.builder.MobileMidwifeEnrollmentBuilder;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.PhoneOwnership;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.repository.AllCampaigns;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.retry.service.RetryService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateTimeSourceUtil;
import org.motechproject.util.DateUtil;
import org.motechproject.util.datetime.DateTimeSource;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

public class MobileMidwifeServiceTest extends BaseUnitTest {
    private MobileMidwifeService service;
    @Mock
    private AllMobileMidwifeEnrollments mockAllMobileMidwifeEnrollments;
    @Mock
    private AllCampaigns mockAllCampaigns;
    @Mock
    private AllPatientsOutbox mockAllPatientsOutbox;
    @Mock
    private RetryService mockRetryService;
    @Mock
    private AllPatients mockAllPatients;

    public MobileMidwifeServiceTest() {
        initMocks(this);
        service = new MobileMidwifeService(mockAllMobileMidwifeEnrollments, mockAllCampaigns, mockAllPatientsOutbox, mockRetryService, mockAllPatients);
    }

    @Test
    public void shouldCreateMobileMidwifeEnrollmentAndCreateScheduleIfNotRegisteredAlready() {
        String patientId = "patientId";
        mockNow(now());
        final DateTime enrollmentDateTime = newDateTime(2012, 2, 3, 4, 3, 2);
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().medium(Medium.SMS).serviceType(ServiceType.CHILD_CARE).facilityId("facility12").
                patientId(patientId).staffId("staff13").consent(true).dayOfWeek(DayOfWeek.Thursday).phoneOwnership(PhoneOwnership.HOUSEHOLD)
                .enrollmentDateTime(enrollmentDateTime).messageStartWeek("52")
                .build();
        when(mockAllMobileMidwifeEnrollments.findActiveBy(patientId)).thenReturn(null);

        service.register(enrollment);
        assertThat(enrollment.getEnrollmentDateTime(), is(enrollmentDateTime));

        verifyCreateNewEnrollment(enrollment);
        ArgumentCaptor<CampaignRequest> campaignRequestCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(mockAllCampaigns).start(campaignRequestCaptor.capture());

        assertCampaignRequestWith(enrollment, campaignRequestCaptor.getValue(), enrollmentDateTime.toLocalDate());
    }

    @Test
    public void shouldRolloverMobileMidwifeEnrollment() {
        String patientId = "patientId";
        mockNow(now());
        final DateTime enrollmentDateTime = newDateTime(2012, 2, 3, 4, 3, 2);
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().medium(Medium.SMS).serviceType(ServiceType.PREGNANCY).facilityId("facility12").
                patientId(patientId).staffId("staff13").consent(true).dayOfWeek(DayOfWeek.Thursday).phoneOwnership(PhoneOwnership.HOUSEHOLD)
                .enrollmentDateTime(enrollmentDateTime).messageStartWeek("52")
                .build();
        when(mockAllMobileMidwifeEnrollments.findActiveBy(patientId)).thenReturn(null);
        service.register(enrollment);
        MobileMidwifeEnrollment newEnrollment = MobileMidwifeEnrollment.cloneNew(enrollment).setServiceType(ServiceType.CHILD_CARE).setMessageStartWeek("41");
        newEnrollment.setEnrollmentDateTime(DateTime.now());
        newEnrollment.setActive(true);
        when(mockAllMobileMidwifeEnrollments.findActiveBy(patientId)).thenReturn(enrollment);
        service.rollover(patientId, newEnrollment.getEnrollmentDateTime());

        verify(mockAllMobileMidwifeEnrollments, times(2)).update(enrollment);
        ArgumentCaptor<CampaignRequest> campaignRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(mockAllCampaigns, times(2)).stop(campaignRequestArgumentCaptor.capture());
        verify(mockAllCampaigns, times(2)).start(campaignRequestArgumentCaptor.capture());
    }

    @Test
    public void shouldCreateNewScheduleOnlyIfEnrolledWithConsentYes() {
        MobileMidwifeEnrollment enrollmentWithNoConsent = new MobileMidwifeEnrollmentBuilder().serviceType(ServiceType.PREGNANCY).facilityId("facility12").
                patientId("patienId").staffId("staff13").consent(false).messageStartWeek("6").phoneOwnership(PhoneOwnership.PERSONAL).medium(Medium.VOICE).build();
        service.register(enrollmentWithNoConsent);
        verify(mockAllMobileMidwifeEnrollments).add(enrollmentWithNoConsent);
        verify(mockAllCampaigns, never()).start(enrollmentWithNoConsent.createCampaignRequestForTextMessage(Matchers.<LocalDate>any()));
    }

    @Test
    public void shouldStopScheduleOnlyIfEnrolledWithConsentYes() {
        String patientId = "patienId";
        MobileMidwifeEnrollment existingEnrollmentWithNoConsent = new MobileMidwifeEnrollmentBuilder().medium(Medium.SMS).serviceType(ServiceType.PREGNANCY)
                .facilityId("facility12").patientId(patientId).staffId("staff13").consent(false).phoneOwnership(PhoneOwnership.PERSONAL)
                .messageStartWeek("9").build();
        when(mockAllMobileMidwifeEnrollments.findActiveBy(patientId)).thenReturn(existingEnrollmentWithNoConsent);

        MobileMidwifeEnrollment newEnrollment = new MobileMidwifeEnrollmentBuilder().medium(Medium.SMS).serviceType(ServiceType.PREGNANCY).patientId(patientId)
                .messageStartWeek("9").phoneOwnership(PhoneOwnership.HOUSEHOLD).consent(true).build();
        service.register(newEnrollment);

        verify(mockAllMobileMidwifeEnrollments).update(existingEnrollmentWithNoConsent);
        verify(mockAllCampaigns, never()).stop(existingEnrollmentWithNoConsent.campaignRequest());
    }

    private void mockNow(final DateTime now) {
        DateTimeSourceUtil.setSourceInstance(new DateTimeSource() {
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
        });
    }

    private void verifyCreateNewEnrollment(MobileMidwifeEnrollment enrollment) {
        verify(mockAllMobileMidwifeEnrollments).add(enrollment);
    }

    @Test
    public void shouldDeactivateExistingEnrollmentAndCampaign_AndCreateNewEnrollmentIfEnrolledAlready() {
        String patientId = "patientId";
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().serviceType(ServiceType.PREGNANCY).medium(Medium.SMS).facilityId("facility12").
                patientId(patientId).staffId("staff13").consent(true).dayOfWeek(DayOfWeek.Thursday).phoneOwnership(PhoneOwnership.PERSONAL)
                .messageStartWeek("6").build();
        MobileMidwifeEnrollment existingEnrollment = new MobileMidwifeEnrollmentBuilder().serviceType(ServiceType.PREGNANCY).facilityId("facility12").
                messageStartWeek("6").patientId(patientId).consent(true).phoneOwnership(PhoneOwnership.HOUSEHOLD).medium(Medium.SMS).build();
        when(mockAllMobileMidwifeEnrollments.findActiveBy(patientId)).thenReturn(existingEnrollment);

        service = spy(service);

        service.register(enrollment);
        assertTrue(enrollment.getActive());
        verify(service).unRegister(patientId);
        verifyCreateNewEnrollment(enrollment);
        verify(mockAllCampaigns).start(enrollment.createCampaignRequestForTextMessage(Matchers.<LocalDate>any()));
        verify(mockAllPatientsOutbox).removeMobileMidwifeMessages(patientId);
    }

    @Test
    public void shouldDeactivateEnrollmentAndClearScheduleOnUnregister() {
        String patientId = "patientId";
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().facilityId("facility12").
                patientId(patientId).staffId("staff13").consent(true).dayOfWeek(DayOfWeek.Thursday).phoneOwnership(PhoneOwnership.PERSONAL)
                .messageStartWeek("6").serviceType(ServiceType.PREGNANCY).medium(Medium.VOICE).build();
        when(mockAllMobileMidwifeEnrollments.findActiveBy(patientId)).thenReturn(enrollment);

        service.unRegister(patientId);
        assertFalse(enrollment.getActive());
        verify(mockAllMobileMidwifeEnrollments).update(enrollment);
        ArgumentCaptor<CampaignRequest> campaignRequestCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(mockAllCampaigns).stop(campaignRequestCaptor.capture());
        verify(mockRetryService).fulfill(patientId, Constants.RETRY_GROUP);
        assertThat(campaignRequestCaptor.getValue().externalId(), is(patientId));
        assertThat(campaignRequestCaptor.getValue().campaignName(), is("PREGNANCY_VOICE"));
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
    public void shouldCreateCampaignForIVREnrollments() {
        super.mockCurrentDate(DateUtil.newDateTime(DateUtil.newDate(2012, 7, 4), new Time(10, 10))); // wed
        String patientId = "patientId";
        DayOfWeek dayOfWeek = DayOfWeek.Thursday;
        Time timeOfDay = new Time(11, 11);
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollmentBuilder().serviceType(ServiceType.PREGNANCY).medium(Medium.VOICE)
                .facilityId("facility12").patientId(patientId).staffId("staff13").consent(true).dayOfWeek(dayOfWeek).timeOfDay(timeOfDay)
                .phoneOwnership(PhoneOwnership.PERSONAL).messageStartWeek("6").build();
        service.register(enrollment);
        ArgumentCaptor<CampaignRequest> campaignRequestCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(mockAllCampaigns).start(campaignRequestCaptor.capture());
        CampaignRequest actualRequest = campaignRequestCaptor.getValue();
//        assertThat(actualRequest.getUserPreferredDays(), is(asList(dayOfWeek)));
        assertThat(actualRequest.deliverTime(), is(timeOfDay));
//        assertEquals(null, actualRequest.startOffset());
        assertThat(actualRequest.referenceDate(), is(newDate(2012, 7, 5)));
    }

    private void assertCampaignRequestWith(MobileMidwifeEnrollment enrollment, CampaignRequest actualRequest, LocalDate expectedScheduleStartDate) {
        assertThat(actualRequest.externalId(), is(enrollment.getPatientId()));
//        assertThat(actualRequest.startOffset(), is(MessageStartWeek.findBy(enrollment.getMessageStartWeek()).getWeek()));
        assertThat(actualRequest.campaignName(), is(enrollment.getServiceType().name() + "_" + enrollment.getMedium().name()));
        assertThat(actualRequest.referenceDate(), is(expectedScheduleStartDate));
//        assertNull(actualRequest.reminderTime());
    }

}
