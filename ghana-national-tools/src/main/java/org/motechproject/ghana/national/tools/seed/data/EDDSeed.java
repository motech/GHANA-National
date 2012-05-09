package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.FALSE;

@Component("eddSeed")
public class EDDSeed extends ScheduleMigrationSeed {

    @Autowired
    public EDDSeed(OldGhanaScheduleSource oldGhanaScheduleSource, AllSchedules allTrackedSchedules, AllCareSchedules allCareSchedules) {
        super(allTrackedSchedules, oldGhanaScheduleSource, allCareSchedules, FALSE);
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getEDDSchedules();
    }

    @Override
    protected String mapMilestoneName(String milestoneName) {
        return milestoneName.replace("EDD", "Default");
    }

    @Override
    public String getScheduleName(String milestoneName) {
        return ScheduleNames.ANC_DELIVERY.getName();
    }
}
