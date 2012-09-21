package org.motechproject.ghana.national.helper;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatterBuilder;
import org.motechproject.ghana.national.domain.json.MobileMidwifeCampaignRecord;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeCampaign;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.repository.CampaignJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

@Component
public class MobileMidwifeWeekCalculator {

    CampaignJsonReader campaignJsonReader;

    @Autowired
    public MobileMidwifeWeekCalculator(CampaignJsonReader campaignJsonReader) {
        this.campaignJsonReader = campaignJsonReader;
    }

    public Boolean hasProgramEnded(String campaignName, String messageKey) {
        Campaign campaign = getCampaign(campaignName);
        return campaign.isEnded(messageKey);
    }

    private Campaign getCampaign(String campaignName) {
        switch (campaignName) {
            case "PREGNANCY_VOICE":
                return new PregnancyVoiceCampaign();
            case "PREGNANCY_SMS":
                return new PregnancySMSCampaign();
            case "CHILD_CARE_VOICE":
                return new ChildVoiceCampaign();
            case "CHILD_CARE_SMS":
                return new ChildSMSCampaign();
        }
        return null;
    }

    abstract class VoiceCampaign extends Campaign {
    }

    abstract class SMSCampaign extends Campaign {
    }

    abstract class Campaign {

        abstract Boolean isEnded(String messageKey);

        public MobileMidwifeCampaignRecord getCampaignRecord(String campaignName) {
            List<MobileMidwifeCampaignRecord> mobileMidwifeCampaignRecords =
                    filter(having(on(MobileMidwifeCampaignRecord.class).getName(), equalTo(campaignName)), campaignJsonReader.records);
            return mobileMidwifeCampaignRecords.get(0);
        }

        public String getLastApplicableWeekDay(String campaignName) {
            MobileMidwifeCampaignRecord mobileMidwifeCampaignRecord = getCampaignRecord(campaignName);
            List<String> applicableWeekDays = mobileMidwifeCampaignRecord.getMessages().get(0).getRepeatOn();
            return applicableWeekDays.get(applicableWeekDays.size() - 1);
        }

        public String getWeek(String campaignName) {
            MobileMidwifeCampaignRecord mobileMidwifeCampaignRecord = getCampaignRecord(campaignName);
            Period period = Period.parse(mobileMidwifeCampaignRecord.getMaxDuration(), new PeriodFormatterBuilder()
                    .appendWeeks().appendSuffix(" week", " weeks")
                    .toFormatter());
            return period.getWeeks() + "";
        }
    }

    class PregnancyVoiceCampaign extends VoiceCampaign {

        @Override
        public Boolean isEnded(String messageKey) {
            String campaignName = MobileMidwifeCampaign.getName(ServiceType.PREGNANCY, Medium.VOICE);
            Integer weekNum = Integer.parseInt(getWeek(campaignName));
            return (Integer.parseInt(messageKey) >= weekNum);
        }
    }

    class ChildVoiceCampaign extends VoiceCampaign {

        @Override
        public Boolean isEnded(String messageKey) {
            String campaignName = MobileMidwifeCampaign.getName(ServiceType.CHILD_CARE, Medium.VOICE);
            Integer weekNum = Integer.parseInt(getWeek(campaignName));
            return (Integer.parseInt(messageKey) >= weekNum);
        }
    }

    class ChildSMSCampaign extends SMSCampaign {
        @Override
        public Boolean isEnded(String messageKey) {
            String campaignName = MobileMidwifeCampaign.getName(ServiceType.CHILD_CARE, Medium.SMS);
            String lastApplicableWeekDay = getLastApplicableWeekDay(campaignName);
            int weekNumber = Integer.parseInt(getWeek(campaignName));
            Pattern compliedPattern = Pattern.compile("CHILD_CARE-cw(\\d+).+");
            Matcher matcher = compliedPattern.matcher(messageKey);

            if (matcher.matches()) {
                if (Integer.parseInt(matcher.group(1)) >= weekNumber && messageKey.contains(lastApplicableWeekDay)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
    }

    class PregnancySMSCampaign extends SMSCampaign {
        @Override
        public Boolean isEnded(String messageKey) {
            String campaignName = MobileMidwifeCampaign.getName(ServiceType.PREGNANCY, Medium.SMS);
            String lastApplicableWeekDay = getLastApplicableWeekDay(campaignName);
            int weekNumber = Integer.parseInt(getWeek(campaignName));

            Pattern compliedPattern = Pattern.compile("PREGNANCY-cw(\\d+).+");
            Matcher matcher = compliedPattern.matcher(messageKey);

            if (matcher.matches()) {
                if (Integer.parseInt(matcher.group(1)) >= weekNumber && messageKey.contains(lastApplicableWeekDay)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        }
    }
}
