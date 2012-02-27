package org.motechproject.ghana.national.repository;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSchedules {
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    public AllSchedules(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
    }

    public void enrollOrFulfill(EnrollmentRequest enrollmentRequest) {
        if (enrollment(enrollmentRequest) == null) {
            enroll(enrollmentRequest);
        }
        fulfilCurrentMilestone(enrollmentRequest);
    }

    public void enroll(EnrollmentRequest enrollmentRequest) {
        scheduleTrackingService.enroll(enrollmentRequest);
    }

    public void fulfilCurrentMilestone(EnrollmentRequest enrollmentRequest) {
        scheduleTrackingService.fulfillCurrentMilestone(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName());
    }

    public void unEnroll(Patient patient, String scheduleName) {
        scheduleTrackingService.unenroll(patient.getMRSPatientId(), scheduleName);
    }

    public void unEnroll(Patient patient, List<String> scheduleNames) {
        for (String scheduleName : scheduleNames) {
            scheduleTrackingService.unenroll(patient.getMRSPatientId(), scheduleName);
        }
    }

    public EnrollmentResponse enrollment(EnrollmentRequest enrollmentRequest) {
        return scheduleTrackingService.getEnrollment(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName());
    }
}
