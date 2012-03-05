package org.motechproject.ghana.national.tools.seed.data;

import ch.lambdaj.group.Group;
import org.motechproject.MotechException;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.TTVaccineSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;

public class TTVaccineSeed extends Seed {
    TTVaccineSource ttVaccineSource;

    @Autowired
    public TTVaccineSeed(TTVaccineSource ttVaccineSource) {
        this.ttVaccineSource = ttVaccineSource;
    }

    @Override
    protected void load() {
        try {
            final List<UpcomingSchedule> allUpcomingSchedules = ttVaccineSource.getAllUpcomingSchedules();

        } catch (Exception e) {
            throw new MotechException("Encountered exception while migrating upcoming TT vaccine schedules", e);
        }
    }


    public void migrate(List<UpcomingSchedule> upcomingSchedulesFromDb) {
        final Group<UpcomingSchedule> schedulesForPatients = group(upcomingSchedulesFromDb, by(on(UpcomingSchedule.class).getPatientId()));
        for (Group<UpcomingSchedule> schedulesForPatient : schedulesForPatients.subgroups()) {
            if(schedulesForPatients.findAll().size() > 1){
                throw new MotechException("Encountered more than one active upcoming TT schedule for patient, " + schedulesForPatient.findAll().get(0).getPatientId());
            }
            else {

            }
        }
    }
}
