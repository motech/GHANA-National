package org.motechproject.ghana.national.domain;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import java.util.Map;

public class AlertDetails {
    private String scheduleName;
    private AlertWindow window;
    private String scheduleId;
    private String milestoneName;

    private AlertDetails(){}

    public static AlertDetails createFromSchedule(MilestoneEvent milestoneEvent){
        AlertDetails alertDetails = new AlertDetails();
        alertDetails.scheduleName = milestoneEvent.getScheduleName();
        alertDetails.window = AlertWindow.byPlatformName(milestoneEvent.getWindowName());
        alertDetails.scheduleId = milestoneEvent.getExternalId();
        alertDetails.milestoneName = milestoneEvent.getMilestoneAlert().getMilestoneName();
        return alertDetails;
    }

    public static AlertDetails createFromAppointment(MotechEvent motechEvent){
        AlertDetails alertDetails = new AlertDetails();
        Map<String, Object> parameters = motechEvent.getParameters();

        alertDetails.scheduleId = (String) motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
        alertDetails.scheduleName = (String) motechEvent.getParameters().get(EventKeys.VISIT_NAME);
        alertDetails.window = alertDetails.getVisitWindow((String) parameters.get(MotechSchedulerService.JOB_ID_KEY));
        alertDetails.milestoneName = alertDetails.scheduleName;
        return alertDetails;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public AlertWindow getWindow() {
        return window;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public AlertWindow getVisitWindow(String jobId) {
        char reminderCount = jobId.charAt(jobId.length() - 1);
        switch (reminderCount) {
            case '0':
                return AlertWindow.DUE;
            case '1':
            case '2':
            case '3':
                return AlertWindow.OVERDUE;
        }
        return null;
    }
}
