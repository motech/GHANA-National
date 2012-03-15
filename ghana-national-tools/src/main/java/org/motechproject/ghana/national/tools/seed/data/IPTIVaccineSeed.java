package org.motechproject.ghana.national.tools.seed.data;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("iptiVaccineSeed")
public class IPTIVaccineSeed extends ScheduleMigrationSeed {

    private AllSchedules allSchedules;

    @Autowired
    public IPTIVaccineSeed(OldGhanaScheduleSource oldGhanaScheduleSource, AllTrackedSchedules allTrackedSchedules, AllSchedules allSchedules) {
        super(allTrackedSchedules, oldGhanaScheduleSource);
        this.allSchedules = allSchedules;
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getUpcomingIPTSchedules();
    }

    @Override
    public String getScheduleName() {
        return ScheduleNames.CWC_IPT_VACCINE;
    }

    protected void enroll(DateTime milestoneReferenceDate, String milestoneName, Patient patient) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(patient.getMRSPatientId(),
                getScheduleName(), new Time(DateUtil.now().toLocalTime()),
                milestoneReferenceDate.toLocalDate(), new Time(milestoneReferenceDate.toLocalTime()),
                milestoneReferenceDate.toLocalDate(), new Time(milestoneReferenceDate.toLocalTime()),
                mapMilestoneName(milestoneName));
        allSchedules.enroll(enrollmentRequest);
    }

    protected String mapMilestoneName(String milestoneName) {
        return "IPT" + milestoneName.charAt(milestoneName.length() - 1);
    }
}
