package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.testing.utils.BaseUnitTest;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllCampaignsTest extends BaseUnitTest {

    AllCampaigns campaign;

    @Mock
    MessageCampaignService mockMessageCampaignService;
    @Mock
    AllMessageCampaigns mockAllMessageCampaigns;

    @Before
    public void setUp() {
        initMocks(this);
        campaign = new AllCampaigns(mockMessageCampaignService, mockAllMessageCampaigns);
    }

    @Test
    public void shouldStartCampaignservice() {
        final DateTime scheduleStartDate = new DateTime();
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment(now()).setServiceType(ServiceType.PREGNANCY).setMessageStartWeek("33");
        campaign.start(enrollment.createCampaignRequestForTextMessage(scheduleStartDate.toLocalDate()));
        verify(mockMessageCampaignService).startFor(any(CampaignRequest.class));
    }

    @Test
    public void shouldStopCampaignservice() {
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment(now()).setServiceType(ServiceType.CHILD_CARE)
                .setMessageStartWeek("53");
        campaign.stop(enrollment.stopCampaignRequest());
        verify(mockMessageCampaignService).stopAll(any(CampaignRequest.class));

    }

    @Test
    public void shouldReturnCycleStartDateForEnrollmentRegistrationDateAndDaysApplicable() {
        ServiceType serviceType = ServiceType.CHILD_CARE;
        DateTime currentDay = new DateTime(2012, 2, 2, 4, 15, 0);
        mockCurrentDate(currentDay);

        Medium medium = Medium.SMS;
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment(currentDay).setServiceType(serviceType).setMedium(medium);
        when(mockAllMessageCampaigns.getApplicableDaysForRepeatingCampaign(serviceType.name(), ServiceType.CHILD_CARE.getServiceName(medium))).thenReturn(asList(DayOfWeek.Monday));

        LocalDate actualDateTime = campaign.nextCycleDateFromToday(enrollment.getServiceType(), medium);
        assertThat(actualDateTime, is(currentDay.dayOfMonth().addToCopy(4).toLocalDate()));
    }

}
