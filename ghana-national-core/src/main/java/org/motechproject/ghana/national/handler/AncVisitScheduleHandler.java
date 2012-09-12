package org.motechproject.ghana.national.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ghana.national.exception.EventHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AncVisitScheduleHandler {

    private Logger logger = LoggerFactory.getLogger(AncVisitScheduleHandler.class);

    @Autowired
    private CareScheduleHandler careScheduleHandler;

    @MotechListener(subjects = {EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT})
    public void handleAlert(MotechEvent motechEvent) {
        try {
            careScheduleHandler.handleAncVisitAlert(motechEvent);
        } catch (Exception e) {
            logger.error("<Appointment Alert Exception>: Encountered error while sending alert: ", e);
            throw new EventHandlerException(motechEvent, e);
        }
    }
}
