package org.motechproject.ghana.national.tools.seed.data.domain;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class Filter {
    Logger log = LoggerFactory.getLogger(this.getClass());

    public List<UpcomingSchedule> filter(List<UpcomingSchedule> schedules) {
        log.info("Applying filter, on patient, " + schedules.get(0).getPatientId());
        final List<UpcomingSchedule> schedulesAfterFiltering = filteringLogic(schedules);
        if (schedulesAfterFiltering.size() < schedules.size()) {
            List<UpcomingSchedule> filteredSchedules = new ArrayList<UpcomingSchedule>();
            for (UpcomingSchedule schedule : schedules) {
                if (!schedulesAfterFiltering.contains(schedule))
                    filteredSchedules.add(schedule);
            }
            log.info("Filtered schedules, " + filteredSchedules);
        }
        return schedulesAfterFiltering;
    }

    protected abstract List<UpcomingSchedule> filteringLogic(List<UpcomingSchedule> schedules);
}
