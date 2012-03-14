package org.motechproject.ghana.national.handler;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.ghana.national.configuration.ScheduleNames.*;

@Component
public class ScheduleHandler {
    private Logger logger = LoggerFactory.getLogger(ScheduleHandler.class);

    @Autowired
    private CareScheduleHandler careScheduleHandler;

    @MotechListener(subjects = {EventSubjects.MILESTONE_ALERT})
    public void handleAlert(MotechEvent motechEvent) {
        try {
            MilestoneEvent milestoneEvent = new MilestoneEvent(motechEvent);
            if (milestoneEvent.getScheduleName().equals(ANC_DELIVERY))
                careScheduleHandler.handlePregnancyAlert(milestoneEvent);
            else if (milestoneEvent.getScheduleName().equals(TT_VACCINATION_VISIT))
                careScheduleHandler.handleTTVaccinationAlert(milestoneEvent);
            else if (milestoneEvent.getScheduleName().equals(ANC_IPT_VACCINE))
                careScheduleHandler.handleIPTpVaccinationAlert(milestoneEvent);
            else if (milestoneEvent.getScheduleName().equals(CWC_IPT_VACCINE))
                careScheduleHandler.handleIPTiVaccinationAlert(milestoneEvent);
            else if (milestoneEvent.getScheduleName().equals(CWC_BCG))
                careScheduleHandler.handleBCGAlert(milestoneEvent);
            else if (milestoneEvent.getScheduleName().equals(CWC_MEASLES_VACCINE))
                careScheduleHandler.handleMeaslesVaccinationAlert(milestoneEvent);
            else if (milestoneEvent.getScheduleName().equals(CWC_PENTA))
                careScheduleHandler.handlePentaVaccinationAlert(milestoneEvent);
            else if (milestoneEvent.getScheduleName().equals(CWC_YELLOW_FEVER))
                careScheduleHandler.handleYellowFeverVaccinationAlert(milestoneEvent);
            else if (isPNCMotherSchedule(milestoneEvent.getScheduleName()))
                careScheduleHandler.handlePNCMotherAlert(milestoneEvent);
        } catch (Exception e) {
            logger.error("Encountered error while sending pregnancy alert: ", e);
        }
    }

    private boolean isPNCMotherSchedule(String scheduleName) {
        return scheduleName.equals(PNC_MOTHER_1) || scheduleName.equals(PNC_MOTHER_2) || scheduleName.equals(PNC_MOTHER_3);
    }
}
