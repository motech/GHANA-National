package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.TRUE;

@Component("opvSeed")
public class OPVSeed extends ScheduleMigrationSeed {

    @Autowired
    public OPVSeed(OldGhanaScheduleSource oldGhanaScheduleSource, AllSchedules allSchedules, AllCareSchedules allCareSchedules) {
        super(allSchedules, oldGhanaScheduleSource, allCareSchedules, TRUE);
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getUpcomingOPVSchedules();
    }

    @Override
    protected String mapMilestoneName(String milestoneName) {
        return milestoneName;
    }

    @Override
    public String getScheduleName(String milestoneName) {
        if ("OPV0".equals(milestoneName)){
            return ScheduleNames.CWC_OPV_0.getName();
        }else if ("OPV1".equals(milestoneName) || "OPV2".equals(milestoneName) || "OPV3".equals(milestoneName)){
            return ScheduleNames.CWC_OPV_OTHERS.getName();
        }
        return null;
    }
}
