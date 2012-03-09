package org.motechproject.ghana.national.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;

public abstract class BaseScheduleHandler {

    protected AllPatients allPatients;
    protected SMSGateway smsGateway;
    protected AllFacilities allFacilities;

    protected BaseScheduleHandler() {
    }

    protected BaseScheduleHandler(AllPatients allPatients, AllFacilities allFacilities, SMSGateway smsGateway) {
        this.allPatients = allPatients;
        this.smsGateway = smsGateway;
        this.allFacilities = allFacilities;
    }

    protected void sendSMSToFacilityForAnAppointment(String ancVisitSmsKey, MotechEvent motechEvent) {
        final Map<String, Object> parameters = motechEvent.getParameters();
        String externalId = (String) parameters.get(EventKeys.EXTERNAL_ID_KEY);
        final Patient patient = allPatients.patientByOpenmrsId(externalId);
        final String motechId = patient.getMrsPatient().getMotechId();

        Facility facility = allFacilities.getFacility(patient.getMrsPatient().getFacility().getId());

        smsGateway.dispatchSMSToAggregator(ancVisitSmsKey, new HashMap<String, String>() {{
            put(MOTECH_ID, motechId);
            put(WINDOW, getVisitWindow((String) parameters.get(MotechSchedulerService.JOB_ID_KEY)));
            put(FIRST_NAME, patient.getFirstName());
            put(LAST_NAME, patient.getLastName());
            put(SCHEDULE_NAME, (String) parameters.get(EventKeys.VISIT_NAME));
        }}, facility.getPhoneNumber());
    }

    private String getVisitWindow(String jobId) {
        char reminderCount = jobId.charAt(jobId.length() - 1);
        switch (reminderCount) {
            case '0':
                return WindowName.due.name();
            case '1':
                return WindowName.late.name();
            case '2':
                return WindowName.max.name();
        }
        return null;
    }

    protected void sendSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        String externalId = milestoneEvent.getExternalId();
        final Patient patient = allPatients.patientByOpenmrsId(externalId);
        final String motechId = patient.getMrsPatient().getMotechId();

        Facility facility = allFacilities.getFacility(patient.getMrsPatient().getFacility().getId());

        smsGateway.dispatchSMSToAggregator(smsTemplateKey, new HashMap<String, String>() {{
            put(MOTECH_ID, motechId);
            put(WINDOW, AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName());
            put(FIRST_NAME, patient.getFirstName());
            put(LAST_NAME, patient.getLastName());
            put(SCHEDULE_NAME, milestoneEvent.getScheduleName());
        }}, facility.getPhoneNumber());
    }

}
