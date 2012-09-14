package org.motechproject.ghana.national.repository;

import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllCampaigns {

    private MessageCampaignService campaignService;

    @Autowired
    public AllCampaigns(MessageCampaignService campaignService) {
        this.campaignService = campaignService;
    }

    public Boolean start(CampaignRequest campaignRequest) {
        campaignService.startFor(campaignRequest);
        return true;
    }

    public Boolean stop(CampaignRequest enrollRequest) {
        campaignService.stopAll(enrollRequest);
        return true;
    }
}
