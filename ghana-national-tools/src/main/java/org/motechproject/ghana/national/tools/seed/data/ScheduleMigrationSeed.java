package org.motechproject.ghana.national.tools.seed.data;

import ch.lambdaj.group.Group;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.MotechException;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientCare;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllCareSchedules;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.ghana.national.tools.seed.data.domain.DuplicateScheduleFilter;
import org.motechproject.ghana.national.tools.seed.data.domain.Filter;
import org.motechproject.ghana.national.tools.seed.data.domain.ScheduleExpiryBasedOnThirdLateAlertFilter;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.motechproject.model.Time;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static org.motechproject.util.DateUtil.newDateTime;

public abstract class ScheduleMigrationSeed extends Seed {

    private AllSchedules allSchedules;
    protected OldGhanaScheduleSource oldGhanaScheduleSource;
    List<Filter> filters = Arrays.asList(new DuplicateScheduleFilter(), new ScheduleExpiryBasedOnThirdLateAlertFilter());

    Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected AllCareSchedules allCareSchedules;
    private Boolean hasIndependentMilestones;

    protected ScheduleMigrationSeed(AllSchedules allSchedules, OldGhanaScheduleSource oldGhanaScheduleSource, AllCareSchedules allCareSchedules, Boolean hasIndependentMilestones) {
        this.allSchedules = allSchedules;
        this.oldGhanaScheduleSource = oldGhanaScheduleSource;
        this.allCareSchedules = allCareSchedules;
        this.hasIndependentMilestones = hasIndependentMilestones;
    }

    void migrate(List<UpcomingSchedule> upcomingSchedulesFromDb) {
        final Group<UpcomingSchedule> schedulesForPatients = group(upcomingSchedulesFromDb, by(on(UpcomingSchedule.class).getPatientId()));
        for (Group<UpcomingSchedule> schedulesForPatient : schedulesForPatients.subgroups()) {
            List<UpcomingSchedule> filteredSchedules = applyFilters(schedulesForPatient.findAll());
            migrateFilteredSchedules(filteredSchedules);
        }
    }

    private void migrateFilteredSchedules(List<UpcomingSchedule> filteredSchedules) {
        if (hasIndependentMilestones) {
            createSchedules(filteredSchedules);
        } else {
            if (filteredSchedules.size() > 1) {
                LOG.error("Patient, " + filteredSchedules.get(0).getPatientId() + " has more than one active schedule");
            } else if (filteredSchedules.size() == 1) {
                createSchedules(filteredSchedules);
            }
        }
    }

    private void createSchedules(List<UpcomingSchedule> filteredSchedules) {
        for (UpcomingSchedule schedule : filteredSchedules) {
            try {
                enroll(getReferenceDate(schedule), mapMilestoneName(schedule.getMilestoneName()), new Patient(new MRSPatient(schedule.getPatientId(), schedule.getMotechId(), null, null)));
            } catch (Exception e) {
                LOG.error("Encountered exception while migrating schedules for patients, " + schedule.getPatientId(), e);
            }
        }
    }

    private List<UpcomingSchedule> applyFilters(List<UpcomingSchedule> filteredSchedules) {
        for (Filter filter : filters) {
            filteredSchedules = filter.filter(filteredSchedules);
        }
        return filteredSchedules;
    }

    DateTime getReferenceDate(UpcomingSchedule upcomingSchedule) {
        final Schedule schedule = allSchedules.getByName(getScheduleName(upcomingSchedule.getMilestoneName()));
        final Milestone milestone = schedule.getMilestone(mapMilestoneName(upcomingSchedule.getMilestoneName()));
        final Period windowPeriod = milestone.getMilestoneWindow(WindowName.earliest).getPeriod();
        return upcomingSchedule.getDueDatetime().minus(windowPeriod);
    }

    public abstract String getScheduleName(String milestoneName);

    protected abstract String mapMilestoneName(String milestoneName);

    @Override
    protected void load() {
        try {
            migrate(getAllUpcomingSchedules());
        } catch (Exception e) {
            throw new MotechException("Encountered exception while migrating upcoming schedules", e);
        }
    }

    protected void enroll(DateTime milestoneReferenceDate, String milestoneName, Patient patient) {

        DateTime mileStoneReferenceTime = newDateTime(milestoneReferenceDate.toLocalDate(), new Time(milestoneReferenceDate.toLocalTime()));
        PatientCare patientCare = patient.patientCareWithoutMetaData(getScheduleName(milestoneName), mileStoneReferenceTime, mileStoneReferenceTime);
        EnrollmentRequest enrollmentRequest = new ScheduleEnrollmentMapper().map(patient, patientCare.milestoneName(mapMilestoneName(milestoneName)));
        allCareSchedules.enroll(enrollmentRequest);
    }

    protected abstract List<UpcomingSchedule> getAllUpcomingSchedules();

}
