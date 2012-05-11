package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.FALSE;

@Component("iptiVaccineSeed")
public class IPTIVaccineSeed extends ScheduleMigrationSeed {

    @Autowired
    public IPTIVaccineSeed(OldGhanaScheduleSource oldGhanaScheduleSource, org.motechproject.scheduletracking.api.repository.AllSchedules allSchedules, AllCareSchedules allCareSchedules) {
        super(allSchedules, oldGhanaScheduleSource, allCareSchedules, FALSE);
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getUpcomingIPTSchedules();
    }

    @Override
    public String getScheduleName(String milestoneName) {
        return ScheduleNames.CWC_IPT_VACCINE.getName();
    }

    protected String mapMilestoneName(String milestoneName) {
        return "IPTi" + milestoneName.charAt(milestoneName.length() - 1);
    }
}
