package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.FALSE;

@Component
public class YellowFeverVaccineSeed extends ScheduleMigrationSeed {

    @Autowired
    public YellowFeverVaccineSeed(OldGhanaScheduleSource oldGhanaScheduleSource, org.motechproject.scheduletracking.api.repository.AllSchedules allSchedules, AllCareSchedules allCareSchedules) {
        super(allSchedules, oldGhanaScheduleSource, allCareSchedules, FALSE);
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getUpcomingYellowFeverSchedules();
    }

    @Override
    public String getScheduleName(String milestoneName) {
        return ScheduleNames.CWC_YELLOW_FEVER.getName();
    }

    protected String mapMilestoneName(String milestoneName) {
        return "YF";
    }
}
