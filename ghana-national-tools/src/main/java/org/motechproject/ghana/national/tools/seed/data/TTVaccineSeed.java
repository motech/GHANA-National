package org.motechproject.ghana.national.tools.seed.data;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.service.VisitService;
import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.motechproject.ghana.national.tools.seed.data.source.TTVaccineSource;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ttVaccineSeed")
public class TTVaccineSeed extends ScheduleMigrationSeed {
    private TTVaccineSource ttVaccineSource;
    private VisitService visitService;

    @Autowired
    public TTVaccineSeed(TTVaccineSource ttVaccineSource, AllTrackedSchedules allTrackedSchedules, VisitService visitService) {
        super(allTrackedSchedules);
        this.ttVaccineSource = ttVaccineSource;
        this.visitService = visitService;
    }

    protected List<UpcomingSchedule> getAllUpcomingSchedules() {
        return ttVaccineSource.getAllUpcomingSchedules();
    }


    @Override
    public String getScheduleName() {
        return ScheduleNames.TT_VACCINATION_VISIT;

    }

    protected void enroll(DateTime referenceDate, String milestoneName, Patient patient) {
        visitService.createTTSchedule(new TTVaccine(referenceDate, TTVaccineDosage.valueOf(milestoneName), patient));
    }

}
