package org.motechproject.ghana.national.service;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.motechproject.util.DateUtil.endOfDay;

@Service
public class CareProgramQueryService {

    AllSchedules allSchedules;

    @Autowired
    public CareProgramQueryService(AllSchedules allSchedules) {
        this.allSchedules = allSchedules;
    }

    public List<EnrollmentRecord> upcomingCareProgramsForCurrentWeek(Patient patient) {
        final DateTime startToday = DateUtil.now().withTimeAtStartOfDay();
        Period period = Period.days(6);
        return allSchedules.search(new EnrollmentsQuery().havingExternalId(patient.getMRSPatientId())
                .havingWindowStartingDuring(WindowName.due, startToday, endOfDay(startToday.plus(period).toDate())));
    }
}
