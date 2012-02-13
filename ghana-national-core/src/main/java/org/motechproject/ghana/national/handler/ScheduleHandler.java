package org.motechproject.ghana.national.handler;

import org.motechproject.ghana.national.configuration.CareScheduleNames;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleHandler {
    private Logger logger = LoggerFactory.getLogger(ScheduleHandler.class);

    @Autowired
    private CareScheduleHandler careScheduleHandler;

    @MotechListener(subjects = {EventSubject.MILESTONE_ALERT})
    public void handleAlert(MotechEvent motechEvent) {
        try {
            MilestoneEvent milestoneEvent = new MilestoneEvent(motechEvent);
            if (milestoneEvent.getScheduleName().equals(CareScheduleNames.DELIVERY))
                careScheduleHandler.handlePregnancyAlert(milestoneEvent);
        } catch (Exception e) {
            logger.error("Encountered error while sending pregnancy alert, ", e);
        }
    }
}
