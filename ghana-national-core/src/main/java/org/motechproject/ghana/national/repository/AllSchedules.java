package org.motechproject.ghana.national.repository;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllSchedules {
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    public AllSchedules(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
    }

    public void enrollOrFulfill(Patient patient, EnrollmentRequest enrollmentRequest) {
        String mrsPatientId = patient.getMRSPatientId();
        if (scheduleTrackingService.getEnrollment(mrsPatientId, enrollmentRequest.getScheduleName()) == null) {
            scheduleTrackingService.enroll(enrollmentRequest);
        }
        scheduleTrackingService.fulfillCurrentMilestone(mrsPatientId, enrollmentRequest.getScheduleName());
    }

    public void enroll(EnrollmentRequest enrollmentRequest) {
        scheduleTrackingService.enroll(enrollmentRequest);
    }

    public void unEnroll(Patient patient, String scheduleName) {
        scheduleTrackingService.unenroll(patient.getMRSPatientId(), scheduleName);
    }
}
