package org.motechproject.ghana.national.tools.seed.data.domain;

import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class ANCExpiredScheduleFilter extends Filter{
    @Override
    public List<UpcomingSchedule> filteringLogic(List<UpcomingSchedule> schedules) {
        List<UpcomingSchedule> activeSchedules = new ArrayList<UpcomingSchedule>();
        for (UpcomingSchedule schedule : schedules) {
            if(DateUtil.today().toDate().before(schedule.getDueDatetime().plusWeeks(2).plusDays(2).toDate()))
                activeSchedules.add(schedule);
        }
        return activeSchedules;
    }
}
