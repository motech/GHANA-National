package org.motechproject.ghana.national.repository;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.AggregationMessageIdentifier;
import org.motechproject.ghana.national.messagegateway.service.MessageGateway;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSchedulesAndMessages {

    private AllCareSchedules allCareSchedules;
    private MessageGateway messageGateway;
    private AllPatientsOutbox allPatientsOutbox;

    @Autowired
    public AllSchedulesAndMessages(AllCareSchedules allCareSchedules, MessageGateway messageGateway,AllPatientsOutbox allPatientsOutbox){
        this.allCareSchedules = allCareSchedules;
        this.messageGateway = messageGateway;
        this.allPatientsOutbox=allPatientsOutbox;
    }

    public void enrollOrFulfill(EnrollmentRequest enrollmentRequest, LocalDate fulfillmentDate, Time fulfillmentTime) {
        messageGateway.delete(new AggregationMessageIdentifier(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName()).getIdentifier());
        allPatientsOutbox.removeCareAndAppointmentMessages(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName());
        allCareSchedules.enrollOrFulfill(enrollmentRequest, fulfillmentDate, fulfillmentTime);
    }

    public void enrollOrFulfill(EnrollmentRequest enrollmentRequest, LocalDate fulfillmentDate) {
        messageGateway.delete(new AggregationMessageIdentifier(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName()).getIdentifier());
        allPatientsOutbox.removeCareAndAppointmentMessages(enrollmentRequest.getExternalId(), enrollmentRequest.getScheduleName());
        allCareSchedules.enrollOrFulfill(enrollmentRequest, fulfillmentDate);
    }

    public boolean safeFulfilCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate) {
        messageGateway.delete(new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier());
        allPatientsOutbox.removeCareAndAppointmentMessages(externalId, scheduleName);
        return allCareSchedules.safeFulfilCurrentMilestone(externalId, scheduleName, fulfillmentDate);
    }

    public void fulfilCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate) {
        messageGateway.delete(new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier());
        allPatientsOutbox.removeCareAndAppointmentMessages(externalId, scheduleName);
        allCareSchedules.fulfilCurrentMilestone(externalId, scheduleName, fulfillmentDate);
    }

    public void unEnroll(String externalId, List<String> scheduleNames) {
        for (String scheduleName : scheduleNames) {
            messageGateway.delete(new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier());
            allPatientsOutbox.removeCareAndAppointmentMessages(externalId, scheduleName);
        }
        allCareSchedules.unEnroll(externalId, scheduleNames);
    }
    


}
