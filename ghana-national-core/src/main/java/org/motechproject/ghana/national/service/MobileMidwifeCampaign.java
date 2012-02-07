package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MobileMidwifeCampaign {

    public static final String CHILDCARE_MESSAGE_NAME = "ChildCare Message";
    public static final String PREGNANCY_MESSAGE_NAME = "Pregnancy Message";

    private MessageCampaignService campaignService;
    private AllMessageCampaigns allMessageCampaigns;

    @Autowired
    public MobileMidwifeCampaign(MessageCampaignService campaignService, AllMessageCampaigns allMessageCampaigns) {
        this.campaignService = campaignService;
        this.allMessageCampaigns = allMessageCampaigns;
    }

    public Boolean start(MobileMidwifeEnrollment enrollment) {
        campaignService.startFor(enrollment.createCampaignRequest());
        return true;
    }

    public Boolean stop(MobileMidwifeEnrollment enrollment) {
        campaignService.stopAll(enrollment.createCampaignRequest());
        return true;
    }

    public DateTime nearestCycleDate(MobileMidwifeEnrollment enrollment) {
        DateTime fromDate = enrollment.getEnrollmentDateTime();
        ServiceType serviceType = enrollment.getServiceType();
        List<DayOfWeek> applicableDays = allMessageCampaigns.getApplicableDaysForRepeatingCampaign(serviceType.name(), programMessageKey(serviceType));
        return DateUtil.nextApplicableWeekDay(fromDate, applicableDays);
    }

    private String programMessageKey(ServiceType serviceType) {
        Map<ServiceType, String> messageMap = new HashMap<ServiceType, String>() {{
            put(ServiceType.PREGNANCY, PREGNANCY_MESSAGE_NAME);
            put(ServiceType.CHILD_CARE, CHILDCARE_MESSAGE_NAME);
        }};
        return messageMap.get(serviceType);
    }
}
