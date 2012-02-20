package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

public abstract class BaseScheduleService {
    private ScheduleTrackingService scheduleTrackingService;

    protected BaseScheduleService(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
    }

    protected void scheduleAlerts(Patient patient, EnrollmentRequest enrollmentRequest){
        final String mrsPatientId = patient.getMRSPatientId();
        if(scheduleTrackingService.getEnrollment(mrsPatientId, enrollmentRequest.getScheduleName()) == null){
            scheduleTrackingService.enroll(enrollmentRequest);
        }
        scheduleTrackingService.fulfillCurrentMilestone(mrsPatientId, enrollmentRequest.getScheduleName());
    }
}
