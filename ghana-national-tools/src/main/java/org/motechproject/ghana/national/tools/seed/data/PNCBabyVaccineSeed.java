package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.DuplicateScheduleFilter;
import org.motechproject.ghana.national.tools.seed.data.domain.ScheduleExpiryBasedOnFirstLateAlertFilter;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Component("pncBabyVaccineSeed")
public class PNCBabyVaccineSeed extends ScheduleMigrationSeed {

    @Autowired
    public PNCBabyVaccineSeed(OldGhanaScheduleSource oldGhanaScheduleSource, org.motechproject.scheduletracking.api.repository.AllSchedules allTrackedSchedules, AllCareSchedules allCareSchedules) {
        super(allTrackedSchedules, oldGhanaScheduleSource, allCareSchedules, TRUE);
        filters = Arrays.asList(new DuplicateScheduleFilter(), new ScheduleExpiryBasedOnFirstLateAlertFilter());
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getUpcomingPNCBabySchedules();
    }

    @Override
    public String getScheduleName(String milestoneName) {
        if("PNC1".equals(milestoneName) || "PNC-C1".equals(milestoneName))
            return ScheduleNames.PNC_CHILD_1.getName();
        else if("PNC2".equals(milestoneName) || "PNC-C2".equals(milestoneName))
            return ScheduleNames.PNC_CHILD_2.getName();
        else if("PNC3".equals(milestoneName) || "PNC-C3".equals(milestoneName))
            return ScheduleNames.PNC_CHILD_3.getName();
        return null;
    }

    @Override
    protected String mapMilestoneName(String milestoneName) {
        return "PNC-C" + milestoneName.charAt(milestoneName.length() - 1);
    }
}
