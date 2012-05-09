package org.motechproject.ghana.national.tools.seed.data;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.FALSE;

@Component("ttVaccineSeed")
public class TTVaccineSeed extends ScheduleMigrationSeed {

    @Autowired
    public TTVaccineSeed(OldGhanaScheduleSource oldGhanaScheduleSource, org.motechproject.scheduletracking.api.repository.AllSchedules allTrackedSchedules, AllCareSchedules allCareSchedules) {
        super(allTrackedSchedules, oldGhanaScheduleSource, allCareSchedules, FALSE);
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getUpcomingTTSchedules();
    }


    @Override
    protected String mapMilestoneName(String milestoneName) {
        return milestoneName;
    }

    @Override
    public String getScheduleName(String milestoneName) {
        return ScheduleNames.TT_VACCINATION.getName();
    }

    @Override
    protected void enroll(DateTime milestoneReferenceDate, String milestoneName, Patient patient) {
        if (TTVaccineDosage.TT1.getScheduleMilestoneName().equals(milestoneName)) {
            milestoneReferenceDate = milestoneReferenceDate.minusWeeks(1);
        }

        super.enroll(milestoneReferenceDate, milestoneName, patient);
    }
}
