package org.motechproject.ghana.national.repository;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.AggregationMessageIdentifier;
import org.motechproject.ghana.national.messagegateway.service.MessageGateway;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllSchedulesAndMessagesTest {
    @Mock
    private AllCareSchedules allCareSchedules;
    @Mock
    private MessageGateway messageGateway;
    @Mock
    private AllPatientsOutbox allPatientsOutbox;
    private AllSchedulesAndMessages allSchedulesAndMessages;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        allSchedulesAndMessages = new AllSchedulesAndMessages(allCareSchedules, messageGateway,allPatientsOutbox);
    }
    
    @Test
    public void shouldRemoveMessagesInAggreationGatewayWhileFulfillingSchedule(){
        String scheduleName = "scheduleName";
        String externalId = "externalId";
        final LocalDate fulfillmentDate = DateUtil.today();
        allSchedulesAndMessages.safeFulfilCurrentMilestone(externalId, scheduleName, fulfillmentDate);
        verify(allCareSchedules).safeFulfilCurrentMilestone(externalId, scheduleName, fulfillmentDate);
        verify(messageGateway).delete(new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier());
        verify(allPatientsOutbox).removeCareAndAppointmentMessages(externalId,scheduleName);

        reset(allCareSchedules);
        reset(messageGateway);
        reset(allPatientsOutbox);

        allSchedulesAndMessages.fulfilCurrentMilestone(externalId, scheduleName, fulfillmentDate);
        verify(allCareSchedules).fulfilCurrentMilestone(externalId, scheduleName, fulfillmentDate);
        verify(messageGateway).delete(new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier());
        verify(allPatientsOutbox).removeCareAndAppointmentMessages(externalId,scheduleName);


        reset(allCareSchedules);
        reset(messageGateway);
        reset(allPatientsOutbox);


        Time fulfillmentTime = new Time(10, 10);
        final EnrollmentRequest enrollmentRequest = new EnrollmentRequest().setExternalId(externalId).setScheduleName(scheduleName);
        allSchedulesAndMessages.enrollOrFulfill(enrollmentRequest, fulfillmentDate, fulfillmentTime);
        verify(allCareSchedules).enrollOrFulfill(enrollmentRequest, fulfillmentDate, fulfillmentTime);
        verify(messageGateway).delete(new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier());
        verify(allPatientsOutbox).removeCareAndAppointmentMessages(externalId,scheduleName);


        reset(allCareSchedules);
        reset(messageGateway);
        reset(allPatientsOutbox);


        allSchedulesAndMessages.enrollOrFulfill(enrollmentRequest, fulfillmentDate);
        verify(allCareSchedules).enrollOrFulfill(enrollmentRequest, fulfillmentDate);
        verify(messageGateway).delete(new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier());
        verify(allPatientsOutbox).removeCareAndAppointmentMessages(externalId,scheduleName);

    }

    @Test
    public void shouldRemoveMessagesInAggregationGatewayWhileUnregisteringSchedule(){
        String externalId = "externalId";
        List<String> scheduleNames = Arrays.asList("schedule1", "schedule2");
        allSchedulesAndMessages.unEnroll(externalId, scheduleNames);
        verify(allCareSchedules).unEnroll(externalId, scheduleNames);
        for (String scheduleName : scheduleNames) {
            verify(messageGateway).delete(new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier());
            verify(allPatientsOutbox).removeCareAndAppointmentMessages(externalId,scheduleName);

        }
    }

}
