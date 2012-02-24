package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

public abstract class BaseScheduleService {
    protected ScheduleTrackingService scheduleTrackingService;

    protected BaseScheduleService(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
    }

    protected void enrollOrFulfill(Patient patient, EnrollmentRequest enrollmentRequest) {
        String mrsPatientId = patient.getMRSPatientId();
        if (scheduleTrackingService.getEnrollment(mrsPatientId, enrollmentRequest.getScheduleName()) == null) {
            scheduleTrackingService.enroll(enrollmentRequest);
        }
        scheduleTrackingService.fulfillCurrentMilestone(mrsPatientId, enrollmentRequest.getScheduleName());
    }

    protected void enroll(EnrollmentRequest enrollmentRequest) {
        scheduleTrackingService.enroll(enrollmentRequest);
    }
}
