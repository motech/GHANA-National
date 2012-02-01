package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileMidwifeCampaign {

    private MessageCampaignService campaignService;

    @Autowired
    public MobileMidwifeCampaign(MessageCampaignService campaignService) {
        this.campaignService = campaignService;
    }

    public Boolean startFor(MobileMidwifeEnrollment enrollment) {
        campaignService.startFor(enrollment.createCampaignRequest());
        return true;
    }

    public Boolean stopExpired(MobileMidwifeEnrollment enrollment) {
        campaignService.stopAll(enrollment.createCampaignRequest());
        return true;
    }
}
