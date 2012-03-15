package org.motechproject.ghana.national.tools.seed.data.domain;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class Filter {
    private Logger LOG = Logger.getLogger(this.getClass());

    public List<UpcomingSchedule> filter(List<UpcomingSchedule> schedules){
        LOG.info("Applying filter, " + this.getClass().getName() + " on patient, " + schedules.get(0).getPatientId());
        final List<UpcomingSchedule> schedulesAfterFiltering = filteringLogic(schedules);
        if(schedulesAfterFiltering.size() < schedules.size()){
            List<UpcomingSchedule> filteredSchedules = new ArrayList<UpcomingSchedule>();
            for (UpcomingSchedule schedule : schedules) {
                if(!schedulesAfterFiltering.contains(schedule))
                    filteredSchedules.add(schedule);
            }
            LOG.info("Filtered schedules, " + filteredSchedules);
        }
        return schedulesAfterFiltering;
    }

    protected abstract List<UpcomingSchedule> filteringLogic(List<UpcomingSchedule> schedules);
}
