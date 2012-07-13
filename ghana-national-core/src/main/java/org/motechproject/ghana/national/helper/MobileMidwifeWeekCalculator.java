package org.motechproject.ghana.national.helper;

import ch.lambdaj.Lambda;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatterBuilder;
import org.motechproject.ghana.national.domain.json.MobileMidwifeCampaignRecord;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeCampaign;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.repository.CampaignJsonReader;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;
import static org.joda.time.Days.daysBetween;
import static org.motechproject.util.DateUtil.daysToCalendarWeekEnd;
import static org.motechproject.util.DateUtil.today;

@Component
public class MobileMidwifeWeekCalculator {

    CampaignJsonReader campaignJsonReader;

    @Autowired
    public MobileMidwifeWeekCalculator(CampaignJsonReader campaignJsonReader){
        this.campaignJsonReader=campaignJsonReader;
    }

    public String getMessageKey(String campaignName,LocalDate campaignStartDate, Integer startWeek, String repeatInterval) {
        Campaign campaign=getCampaign(campaignName);
        return campaign.getMessageKey(campaignStartDate, startWeek, repeatInterval);
    }

    public Boolean hasProgramEnded(String campaignName,String messageKey){
        Campaign campaign=getCampaign(campaignName);
        return campaign.isEnded(messageKey);
    }

    private Campaign getCampaign(String campaignName){
        Campaign campaign=null;
        if ("PREGNANCY_VOICE".equals(campaignName))
            campaign = new PregnancyVoiceCampaign();
        else if ("PREGNANCY_SMS".equals(campaignName))
            campaign = new PregnancySMSCampaign();
        else if ("CHILD_CARE_VOICE".equals(campaignName))
            campaign = new ChildVoiceCampaign();
        else if ("CHILD_CARE_SMS".equals(campaignName))
            campaign = new ChildSMSCampaign();

        return campaign;
    }

    abstract class VoiceCampaign extends Campaign{
        public Integer currentOffsetForVoice(LocalDate startTime, Integer startIntervalOffset, String repeatInterval) {
            WallTime repeatIntervalWallTime = new WallTime(repeatInterval);
            int interval = daysBetween(startTime, today()).getDays();

            return (interval / repeatIntervalWallTime.inDays()) + startIntervalOffset;
        }
    }

    abstract class SMSCampaign extends Campaign{
        public Integer currentOffsetForSMS(LocalDate cycleStartDate, Integer startIntervalOffset) {
            int startOfTheWeek = DayOfWeek.Sunday.getValue();

            LocalDate currentDate = DateUtil.today();

            if (cycleStartDate.isAfter(DateUtil.today()))
                throw new IllegalArgumentException("cycleStartDate cannot be in future");

            int daysDiff = new Period(cycleStartDate, currentDate, PeriodType.days()).getDays();

            if (daysDiff > 0) {
                int daysToFirstCalendarWeekEnd = daysToCalendarWeekEnd(cycleStartDate, startOfTheWeek);
                int daysAfterFirstCalendarWeekEnd = daysDiff > daysToFirstCalendarWeekEnd ? daysDiff - daysToFirstCalendarWeekEnd : 0;
                int weeksAfterFirstSaturday = daysAfterFirstCalendarWeekEnd / 7 + (daysAfterFirstCalendarWeekEnd % 7 > 0 ? 1 : 0);
                return weeksAfterFirstSaturday + startIntervalOffset;
            }
            return startIntervalOffset;
        }

    }

    abstract class Campaign{
        abstract String getMessageKey(LocalDate startTime, Integer startIntervalOffset, String repeatInterval);
        abstract Boolean isEnded(String messageKey);

        public MobileMidwifeCampaignRecord getCampaignRecord(String campaignName) {
            List<MobileMidwifeCampaignRecord> mobileMidwifeCampaignRecords = Lambda.filter(having(on(MobileMidwifeCampaignRecord.class).getName(), equalTo(campaignName)), campaignJsonReader.records);
            return mobileMidwifeCampaignRecords.get(0);
        }

        public String getLastApplicableWeekDay(String campaignName){
            MobileMidwifeCampaignRecord mobileMidwifeCampaignRecord=getCampaignRecord(campaignName);
            List<String> applicableWeekDays=mobileMidwifeCampaignRecord.getMessages().get(0).getWeekDaysApplicable();
            return applicableWeekDays.get(applicableWeekDays.size()-1);
        }

        public String getWeek(String campaignName){
            MobileMidwifeCampaignRecord mobileMidwifeCampaignRecord=getCampaignRecord(campaignName);
            Period period=new Period().parse(mobileMidwifeCampaignRecord.getMaxDuration(),new PeriodFormatterBuilder()
                    .appendWeeks().appendSuffix(" week", " weeks")
                    .toFormatter());
            return period.getWeeks()+"";
        }

        }


    class PregnancyVoiceCampaign extends VoiceCampaign{
        @Override
        public String getMessageKey(LocalDate startTime, Integer startIntervalOffset, String repeatInterval) {
            return String.valueOf(currentOffsetForVoice(startTime, startIntervalOffset, repeatInterval));
        }

        @Override
        public Boolean isEnded(String messageKey) {
            String campaignName = MobileMidwifeCampaign.getName(ServiceType.PREGNANCY, Medium.VOICE);
            String weekNum=getWeek(campaignName);
            if(weekNum.equals(messageKey)){
                return Boolean.TRUE;
            }
         return Boolean.FALSE;
        }


    }

    class ChildVoiceCampaign extends VoiceCampaign {
        @Override
        public String getMessageKey(LocalDate startTime, Integer startIntervalOffset, String repeatInterval) {
            return String.valueOf(currentOffsetForVoice(startTime, startIntervalOffset, repeatInterval));
        }

        @Override
        public Boolean isEnded(String messageKey) {
            String campaignName = MobileMidwifeCampaign.getName(ServiceType.CHILD_CARE, Medium.VOICE);
            String weekNum=getWeek(campaignName);
            if(weekNum.equals(messageKey)){
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    }

    class ChildSMSCampaign extends SMSCampaign{
        @Override
        public String getMessageKey(LocalDate startTime, Integer startIntervalOffset, String repeatInterval) {
            Integer currentWeek = currentOffsetForSMS(startTime, startIntervalOffset);
            return "CHILD_CARE-cw" + currentWeek + "-" + DayOfWeek.getDayOfWeek(DateUtil.today().getDayOfWeek());
        }

        @Override
        public Boolean isEnded(String messageKey) {
            String campaignName = MobileMidwifeCampaign.getName(ServiceType.CHILD_CARE, Medium.SMS);
            MobileMidwifeCampaignRecord mobileMidwifeCampaignRecord = getCampaignRecord(campaignName);
            String lastApplicableWeekDay=getLastApplicableWeekDay(campaignName);
            String weekNumber=getWeek(campaignName);
            if(messageKey.contains(weekNumber) && messageKey.contains(lastApplicableWeekDay)){
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    }

    class PregnancySMSCampaign extends SMSCampaign{
        @Override
        public String getMessageKey(LocalDate startTime, Integer startIntervalOffset, String repeatInterval) {
            Integer currentWeek = currentOffsetForSMS(startTime, startIntervalOffset);
            return "PREGNANCY-cw" + currentWeek + "-" + DayOfWeek.getDayOfWeek(DateUtil.today().getDayOfWeek());
        }

        @Override
        public Boolean isEnded(String messageKey) {
            String campaignName = MobileMidwifeCampaign.getName(ServiceType.PREGNANCY, Medium.SMS);
            String lastApplicableWeekDay=getLastApplicableWeekDay(campaignName);
            String weekNumber=getWeek(campaignName);
            if(messageKey.contains(weekNumber) && messageKey.contains(lastApplicableWeekDay)){
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    }


}
