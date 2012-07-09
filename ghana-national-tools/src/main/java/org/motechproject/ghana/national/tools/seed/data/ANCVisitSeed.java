package org.motechproject.ghana.national.tools.seed.data;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllAppointments;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.tools.seed.data.domain.ANCExpiredScheduleFilter;
import org.motechproject.ghana.national.tools.seed.data.domain.DuplicateScheduleFilter;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.FALSE;

@Component("ancVisitSeed")
public class ANCVisitSeed extends ScheduleMigrationSeed {

    private AllAppointments allAppointments;
    private PatientService patientService;

    @Autowired
    public ANCVisitSeed(OldGhanaScheduleSource oldGhanaScheduleSource, AllSchedules allSchedules, AllCareSchedules allCareSchedules, AllAppointments allAppointments, PatientService patientService) {
        super(allSchedules, oldGhanaScheduleSource, allCareSchedules, FALSE);
        this.allAppointments = allAppointments;
        this.patientService = patientService;
        this.filters = Arrays.asList(new DuplicateScheduleFilter(), new ANCExpiredScheduleFilter());
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return oldGhanaScheduleSource.getUpcomingANCVisitSchedules();
    }

    @Override
    protected Patient getPatient(String motechId, String patientId) {
        return patientService.getPatientByMotechId(motechId);
    }

    @Override
    public String getScheduleName(String milestoneName) {
        return null;
    }

    @Override
    protected void enroll(DateTime milestoneReferenceDate, String milestoneName, Patient patient) {
        allAppointments.updateANCVisitSchedule(patient, milestoneReferenceDate);
    }

    @Override
    DateTime getReferenceDate(UpcomingSchedule upcomingSchedule) {
        return upcomingSchedule.getDueDatetime();
    }

    @Override
    protected String mapMilestoneName(String milestoneName) {
        return null;
    }
}
