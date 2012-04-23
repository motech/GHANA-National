package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;

public abstract class VisitService {
    protected AllSchedules allSchedules;

    public VisitService(AllSchedules allSchedules) {
        this.allSchedules = allSchedules;
    }

    protected EnrollmentResponse enrollment(String mrsPatientId, String programName) {
        return allSchedules.enrollment(new ScheduleEnrollmentMapper().map(mrsPatientId, programName));
    }
}
