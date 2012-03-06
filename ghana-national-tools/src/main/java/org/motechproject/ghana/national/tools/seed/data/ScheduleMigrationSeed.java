package org.motechproject.ghana.national.tools.seed.data;

import ch.lambdaj.group.Group;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.MotechException;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.ghana.national.tools.seed.data.domain.*;
import org.motechproject.ghana.national.tools.seed.data.source.OldGhanaScheduleSource;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;

public abstract class ScheduleMigrationSeed extends Seed {

    private AllTrackedSchedules allTrackedSchedules;
    protected OldGhanaScheduleSource oldGhanaScheduleSource;
    List<Filter> filters = Arrays.asList(new DuplicateScheduleFilter(), new VoidedScheduleFilter(), new ExpiredScheduleFilter());

    private static Logger LOG = Logger.getLogger(ScheduleMigrationSeed.class);

    protected ScheduleMigrationSeed(AllTrackedSchedules allTrackedSchedules, OldGhanaScheduleSource oldGhanaScheduleSource) {
        this.allTrackedSchedules = allTrackedSchedules;
        this.oldGhanaScheduleSource = oldGhanaScheduleSource;
    }

    void migrate(List<UpcomingSchedule> upcomingSchedulesFromDb) {
        final Group<UpcomingSchedule> schedulesForPatients = group(upcomingSchedulesFromDb, by(on(UpcomingSchedule.class).getPatientId()));
        for (Group<UpcomingSchedule> schedulesForPatient : schedulesForPatients.subgroups()) {
            List<UpcomingSchedule> filteredSchedules = schedulesForPatient.findAll();
            for (Filter filter : filters) {
                filteredSchedules = filter.filter(filteredSchedules);
            }

            if (filteredSchedules.size() > 1) {
                LOG.error("Patient, " + filteredSchedules.get(0).getPatientId() + " has more than one active schedule");
            } else if (filteredSchedules.size() == 1) {
                final UpcomingSchedule scheduleToBeMigrated = filteredSchedules.get(0);
                enroll(getReferenceDate(scheduleToBeMigrated), mapMilestoneName(scheduleToBeMigrated.getMilestoneName()), new Patient(new MRSPatient(scheduleToBeMigrated.getPatientId())));
            }
        }
    }

    DateTime getReferenceDate(UpcomingSchedule upcomingSchedule) {
        final Schedule schedule = allTrackedSchedules.getByName(getScheduleName());
        final Milestone milestone = schedule.getMilestone(mapMilestoneName(upcomingSchedule.getMilestoneName()));
        final Period windowPeriod = milestone.getMilestoneWindow(WindowName.earliest).getPeriod();
        return upcomingSchedule.getDueDatetime().minus(windowPeriod);
    }

    protected abstract String mapMilestoneName(String milestoneName);

    public abstract String getScheduleName();

    @Override
    protected void load() {
        try {
            migrate(getAllUpcomingSchedules());
        } catch (Exception e) {
            throw new MotechException("Encountered exception while migrating upcoming schedules", e);
        }
    }

    protected abstract void enroll(DateTime referenceDate, String milestoneName, Patient patient);

    protected abstract List<UpcomingSchedule> getAllUpcomingSchedules();

}
