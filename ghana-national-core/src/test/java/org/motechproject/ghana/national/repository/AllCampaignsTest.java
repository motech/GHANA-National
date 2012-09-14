package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.testing.utils.BaseUnitTest;

import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
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
        campaign = new AllCampaigns(mockMessageCampaignService);
    }

    @Test
    public void shouldStartCampaignservice() {
        final DateTime scheduleStartDate = new DateTime();
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment(now()).setServiceType(ServiceType.PREGNANCY).setMedium(Medium.VOICE).setMessageStartWeek("33");
        campaign.start(enrollment.createCampaignRequestForTextMessage(scheduleStartDate.toLocalDate()));
        verify(mockMessageCampaignService).startFor(any(CampaignRequest.class));
    }

    @Test
    public void shouldStopCampaignservice() {
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment(now()).setServiceType(ServiceType.CHILD_CARE)
                .setMedium(Medium.VOICE).setMessageStartWeek("53");
        campaign.stop(enrollment.campaignRequest());
        verify(mockMessageCampaignService).stopAll(any(CampaignRequest.class));
    }
}
