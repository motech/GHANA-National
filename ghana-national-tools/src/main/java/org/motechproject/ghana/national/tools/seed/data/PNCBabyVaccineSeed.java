package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.DuplicateScheduleFilter;
import org.motechproject.ghana.national.tools.seed.data.domain.ScheduleExpiredBasedOnMaxAlertFilter;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.domain.VoidedScheduleFilter;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Component("pncBabyVaccineSeed")
public class PNCBabyVaccineSeed extends ScheduleMigrationSeed {

    @Autowired
    public PNCBabyVaccineSeed(OldGhanaScheduleSource oldGhanaScheduleSource, AllTrackedSchedules allTrackedSchedules, AllSchedules allSchedules) {
        super(allTrackedSchedules, oldGhanaScheduleSource, allSchedules, TRUE);
        filters = Arrays.asList(new DuplicateScheduleFilter(), new VoidedScheduleFilter(), new ScheduleExpiredBasedOnMaxAlertFilter());
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getUpcomingPNCBabySchedules();
    }

    @Override
    public String getScheduleName(String milestoneName) {
        if("PNC1".equals(milestoneName) || "PNC-CHILD-1".equals(milestoneName))
            return ScheduleNames.PNC_CHILD_1;
        else if("PNC2".equals(milestoneName) || "PNC-CHILD-2".equals(milestoneName))
            return ScheduleNames.PNC_CHILD_2;
        else if("PNC3".equals(milestoneName) || "PNC-CHILD-3".equals(milestoneName))
            return ScheduleNames.PNC_CHILD_3;
        return null;
    }

    @Override
    protected String mapMilestoneName(String milestoneName) {
        return "PNC-C" + milestoneName.charAt(milestoneName.length() - 1);
    }
}
