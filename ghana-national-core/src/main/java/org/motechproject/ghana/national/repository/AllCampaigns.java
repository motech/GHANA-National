package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCampaigns {

    private MessageCampaignService campaignService;
    private AllMessageCampaigns allMessageCampaigns;

    @Autowired
    public AllCampaigns(MessageCampaignService campaignService, AllMessageCampaigns allMessageCampaigns) {
        this.campaignService = campaignService;
        this.allMessageCampaigns = allMessageCampaigns;
    }

    public Boolean start(CampaignRequest campaignRequest) {
        campaignService.startFor(campaignRequest);
        return true;
    }

    public Boolean stop(CampaignRequest enrollRequest) {
        campaignService.stopAll(enrollRequest);
        return true;
    }

    public DateTime nearestCycleDate(MobileMidwifeEnrollment enrollment) {
        DateTime fromDate = enrollment.getEnrollmentDateTime();
        ServiceType serviceType = enrollment.getServiceType();
        List<DayOfWeek> applicableDays = allMessageCampaigns.getApplicableDaysForRepeatingCampaign(serviceType.name(), serviceType.getServiceName());
        return Utility.nextApplicableWeekDay(fromDate, applicableDays);
    }
}
