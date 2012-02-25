package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.model.DayOfWeek;
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

    public Boolean start(MobileMidwifeEnrollment enrollment) {
        campaignService.startFor(enrollment.createCampaignRequest());
        return true;
    }

    public Boolean stop(MobileMidwifeEnrollment enrollment) {
        campaignService.stopAll(enrollment.stopCampaignRequest());
        return true;
    }

    public DateTime nearestCycleDate(MobileMidwifeEnrollment enrollment) {
        DateTime fromDate = enrollment.getEnrollmentDateTime();
        ServiceType serviceType = enrollment.getServiceType();
        List<DayOfWeek> applicableDays = allMessageCampaigns.getApplicableDaysForRepeatingCampaign(serviceType.name(), serviceType.getServiceName());
        return Utility.nextApplicableWeekDay(fromDate, applicableDays);
    }
}
