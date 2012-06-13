package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.MotechException;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.model.DayOfWeek;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.motechproject.ghana.national.domain.mobilemidwife.ServiceType.CHILD_CARE_TEXT;
import static org.motechproject.ghana.national.domain.mobilemidwife.ServiceType.PREGNANCY_TEXT;
import static org.springframework.core.io.support.PropertiesLoaderUtils.loadAllProperties;

@Component
public class MobileMidwifeMessageSeed extends Seed {

    @Autowired
    private CMSLiteService cmsLiteService;

    @Autowired
    private AllMessageCampaigns messageCampaigns;

    @Override
    protected void load() {
        try {
            Language[] languages = {Language.EN};
            for (Language language : languages) {
                Properties properties = loadAllProperties("programs/mobilemidwife/sms/message_" + language.name() + ".properties");
                savePropertiesToCMS(properties, language);
            }
        } catch (Exception e) {
            throw new MotechException("Encountered exception while loading seed", e);
        }
    }

    private void savePropertiesToCMS(Properties properties, Language language) throws CMSLiteException {

        RepeatingCampaignMessage pregnancyCampaignMessage = (RepeatingCampaignMessage) messageCampaigns.getCampaignMessageByMessageName(PREGNANCY_TEXT.name(), PREGNANCY_TEXT.getServiceName(Medium.SMS));
        RepeatingCampaignMessage childCareCampaignMessage = (RepeatingCampaignMessage) messageCampaigns.getCampaignMessageByMessageName(CHILD_CARE_TEXT.name(), CHILD_CARE_TEXT.getServiceName(Medium.SMS));
        Map<String, String> pregnancyDayMap = createDayMap(pregnancyCampaignMessage);
        Map<String, String> childCareDayMap = createDayMap(childCareCampaignMessage);

        for (Object key : properties.keySet()) {
            String keyStr = (String) key;
            String value = (String) properties.get(key);
            String messageContentKey = null;
            String[] tokens = keyStr.split("-");

            String weekDay = tokens[2];
            if (tokens[0].equals(PREGNANCY_TEXT.name())) {
                messageContentKey = keyStr.replace(weekDay, pregnancyDayMap.get(weekDay));
            } else if (tokens[0].equals(CHILD_CARE_TEXT.name())) {
                messageContentKey = keyStr.replace(weekDay, childCareDayMap.get(weekDay));
            }
            cmsLiteService.addContent(new StringContent(language.name(), messageContentKey, value));
        }
    }

    private Map<String, String> createDayMap(RepeatingCampaignMessage campaignMessage) {
        List<DayOfWeek> weekDayList = campaignMessage.weekDaysApplicable();
        Map<String, String> weekDayMap = new HashMap<String, String>();
        int count = 0;
        for (DayOfWeek dayOfWeek : weekDayList) {
            weekDayMap.put("{d" + (++count) + "}", dayOfWeek.name());
        }
        return weekDayMap;
    }
}
