package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.FALSE;

@Component
public class YellowFeverVaccineSeed extends ScheduleMigrationSeed {

    @Autowired
    public YellowFeverVaccineSeed(OldGhanaScheduleSource oldGhanaScheduleSource, AllTrackedSchedules allTrackedSchedules, AllSchedules allSchedules) {
        super(allTrackedSchedules, oldGhanaScheduleSource, allSchedules, FALSE);
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getUpcomingYellowFeverSchedules();
    }

    @Override
    public String getScheduleName(String milestoneName) {
        return ScheduleNames.CWC_YELLOW_FEVER.getName();
    }

    protected String mapMilestoneName(String milestoneName) {
        return milestoneName.replace("YellowFever", "Default");
    }
}
