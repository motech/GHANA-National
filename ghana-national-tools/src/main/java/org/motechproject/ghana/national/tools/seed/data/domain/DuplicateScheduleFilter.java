package org.motechproject.ghana.national.tools.seed.data.domain;

import java.util.ArrayList;
import java.util.List;

public class DuplicateScheduleFilter extends Filter{

    @Override
    public List<UpcomingSchedule> filteringLogic(List<UpcomingSchedule> schedules) {
        List<UpcomingSchedule> filteredSchedules = new ArrayList<UpcomingSchedule>();
        for (UpcomingSchedule schedule : schedules) {
            if(!filteredSchedules.contains(schedule)){
                filteredSchedules.add(schedule);
            }
        }
        return filteredSchedules;
    }
}
