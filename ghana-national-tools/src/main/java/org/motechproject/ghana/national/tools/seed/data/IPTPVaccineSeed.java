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

@Component("iptpVaccineSeed")
public class IPTPVaccineSeed extends ScheduleMigrationSeed {

    @Autowired
    public IPTPVaccineSeed(OldGhanaScheduleSource oldGhanaScheduleSource, AllTrackedSchedules allTrackedSchedules, AllSchedules allSchedules) {
        super(allTrackedSchedules, oldGhanaScheduleSource, allSchedules, FALSE);
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getUpcomingIPTPSchedules();
    }

    @Override
    protected String mapMilestoneName(String milestoneName) {
        return milestoneName;
    }

    @Override
    public String getScheduleName(String milestoneName) {
        return ScheduleNames.ANC_IPT_VACCINE.getName();
    }
}
