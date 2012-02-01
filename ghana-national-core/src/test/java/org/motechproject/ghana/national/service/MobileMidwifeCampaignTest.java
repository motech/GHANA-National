package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.mobilemidwife.ServiceType.PREGNANCY;

public class MobileMidwifeCampaignTest {

    MobileMidwifeCampaign campaign;

    @Mock
    MessageCampaignService mockMessageCampaignService;

    @Before
    public void setUp(){
        initMocks(this);
        campaign=new MobileMidwifeCampaign(mockMessageCampaignService);
    }

    @Test
    public void shouldStartCampaignservice() {
        MobileMidwifeEnrollment enrollment=MobileMidwifeEnrollment.newEnrollment().setServiceType(PREGNANCY);
        campaign.startFor(enrollment);
        verify(mockMessageCampaignService).startFor(any(CampaignRequest.class));
    }
    
    @Test
    public void shouldStopCampaignservice() {
        MobileMidwifeEnrollment enrollment=MobileMidwifeEnrollment.newEnrollment().setServiceType(ServiceType.CHILD_CARE);
        campaign.stopExpired(enrollment);
        verify(mockMessageCampaignService).stopAll(any(CampaignRequest.class));

    }

}
