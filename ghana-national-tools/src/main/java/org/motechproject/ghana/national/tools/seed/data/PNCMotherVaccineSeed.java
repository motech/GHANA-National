package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.DuplicateScheduleFilter;
import org.motechproject.ghana.national.tools.seed.data.domain.ScheduleExpiryBasedOnFirstLateAlertFilter;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Component("pncMotherVaccineSeed")
public class PNCMotherVaccineSeed extends ScheduleMigrationSeed {

    @Autowired
    public PNCMotherVaccineSeed(OldGhanaScheduleSource oldGhanaScheduleSource, AllTrackedSchedules allTrackedSchedules, AllSchedules allSchedules) {
        super(allTrackedSchedules, oldGhanaScheduleSource, allSchedules, TRUE);
        filters = Arrays.asList(new DuplicateScheduleFilter(), new ScheduleExpiryBasedOnFirstLateAlertFilter());
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getUpcomingPNCMotherSchedules();
    }

    @Override
    public String getScheduleName(String milestoneName) {
        if("PNC1".equals(milestoneName) || "PNC-MOTHER-1".equals(milestoneName))
            return ScheduleNames.PNC_MOTHER_1;
        else if("PNC2".equals(milestoneName) || "PNC-MOTHER-2".equals(milestoneName))
            return ScheduleNames.PNC_MOTHER_2;
        else if("PNC3".equals(milestoneName) || "PNC-MOTHER-3".equals(milestoneName))
            return ScheduleNames.PNC_MOTHER_3;
        return null;
    }

    @Override
    protected String mapMilestoneName(String milestoneName) {
        return "PNC-M" + milestoneName.charAt(milestoneName.length() - 1);
    }
}
