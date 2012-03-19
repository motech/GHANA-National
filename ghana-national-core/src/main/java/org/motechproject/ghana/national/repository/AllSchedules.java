package org.motechproject.ghana.national.repository;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;

@Repository
public class AllSchedules {
    private ScheduleTrackingService scheduleTrackingService;
    private Logger log = LoggerFactory.getLogger(AllSchedules.class);

    @Autowired
    public AllSchedules(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
    }

    public void enrollOrFulfill(EnrollmentRequest enrollmentRequest, LocalDate fulfillmentDate, Time fulfillmentTime) {
        if (enrollment(enrollmentRequest) == null) {
            enroll(enrollmentRequest);
        }
        fulfilCurrentMilestone(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName(), fulfillmentDate, fulfillmentTime);
    }

    public void enrollOrFulfill(EnrollmentRequest enrollmentRequest, LocalDate fulfillmentDate) {
        if (enrollment(enrollmentRequest) == null) {
            enroll(enrollmentRequest);
        }
        fulfilCurrentMilestone(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName(), fulfillmentDate);
    }

    public void enroll(EnrollmentRequest enrollmentRequest) {
        scheduleTrackingService.enroll(enrollmentRequest);
    }

    public boolean safeFulfilCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate) {
        try {
            scheduleTrackingService.fulfillCurrentMilestone(externalId, scheduleName, fulfillmentDate);
            return true;
        } catch (InvalidEnrollmentException invalidEnrollmentException) {
            log.warn(format("Problem in fulfil for {%s, %s, %s}", externalId, scheduleName, fulfillmentDate), invalidEnrollmentException.toString());
            return false;
        }
    }

    public void fulfilCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate) {
        scheduleTrackingService.fulfillCurrentMilestone(externalId, scheduleName, fulfillmentDate);
    }

    public void fulfilCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate, Time fulfillmentTime) {
        scheduleTrackingService.fulfillCurrentMilestone(externalId, scheduleName, fulfillmentDate, fulfillmentTime);
    }

    public void unEnroll(String externalId, String scheduleName) {
        scheduleTrackingService.unenroll(externalId, asList(scheduleName));
    }

    public void unEnroll(String externalId, List<String> scheduleNames) {
        scheduleTrackingService.unenroll(externalId, scheduleNames);
    }

    public EnrollmentRecord enrollment(EnrollmentRequest enrollmentRequest) {
        return scheduleTrackingService.getEnrollment(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName());
    }
    
    public List<EnrollmentRecord> search(EnrollmentsQuery enrollmentQuery) {
        return scheduleTrackingService.searchWithWindowDates(enrollmentQuery);
    }
}
