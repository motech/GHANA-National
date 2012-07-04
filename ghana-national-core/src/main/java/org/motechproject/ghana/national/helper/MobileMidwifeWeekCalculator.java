package org.motechproject.ghana.national.helper;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;

import static org.joda.time.Days.daysBetween;
import static org.motechproject.util.DateUtil.daysToCalendarWeekEnd;
import static org.motechproject.util.DateUtil.today;

public class MobileMidwifeWeekCalculator {
    private Campaign campaign;

    public MobileMidwifeWeekCalculator(String campaignName) {
        if("PREGNANCY_VOICE".equals(campaignName))
            campaign = new PregnancyVoiceCampaign();
        else if("PREGNANCY_SMS".equals(campaignName))
            campaign = new PregnancySMSCampaign();
        else if("CHILD_CARE_VOICE".equals(campaignName))
            campaign = new ChildVoiceCampaign();
        else if("CHILD_CARE_SMS".equals(campaignName))
            campaign = new ChildSMSCampaign();
    }


    public String getMessageKey(LocalDate campaignStartDate, Integer startWeek, String repeatInterval) {
        return campaign.getMessageKey(campaignStartDate, startWeek, repeatInterval);
    }

    class VoiceCampaign {
        public Integer currentOffsetForVoice(LocalDate startTime, Integer startIntervalOffset, String repeatInterval) {
            WallTime repeatIntervalWallTime = new WallTime(repeatInterval);
            int interval = daysBetween(startTime, today()).getDays();

            return (interval / repeatIntervalWallTime.inDays()) + startIntervalOffset;
        }
    }

    class SMSCampaign {
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

    interface Campaign{
        String getMessageKey(LocalDate startTime, Integer startIntervalOffset, String repeatInterval);
    }

    class PregnancyVoiceCampaign extends VoiceCampaign implements Campaign{
        @Override
        public String getMessageKey(LocalDate startTime, Integer startIntervalOffset, String repeatInterval) {
            return String.valueOf(currentOffsetForVoice(startTime, startIntervalOffset, repeatInterval));
        }
    }

    class ChildVoiceCampaign extends VoiceCampaign implements Campaign{
        @Override
        public String getMessageKey(LocalDate startTime, Integer startIntervalOffset, String repeatInterval) {
            return String.valueOf(currentOffsetForVoice(startTime, startIntervalOffset, repeatInterval));
        }
    }

    class ChildSMSCampaign extends SMSCampaign implements Campaign{
        @Override
        public String getMessageKey(LocalDate startTime, Integer startIntervalOffset, String repeatInterval) {
            Integer currentWeek = currentOffsetForSMS(startTime, startIntervalOffset);
            return "CHILD_CARE-cw" + currentWeek + "-" + DayOfWeek.getDayOfWeek(DateUtil.today().getDayOfWeek());
        }
    }

    class PregnancySMSCampaign extends SMSCampaign implements Campaign{
        @Override
        public String getMessageKey(LocalDate startTime, Integer startIntervalOffset, String repeatInterval) {
            Integer currentWeek = currentOffsetForSMS(startTime, startIntervalOffset);
            return "PREGNANCY-cw" + currentWeek + "-" + DayOfWeek.getDayOfWeek(DateUtil.today().getDayOfWeek());
        }
    }



}
