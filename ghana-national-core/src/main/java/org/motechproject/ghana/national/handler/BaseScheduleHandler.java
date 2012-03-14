package org.motechproject.ghana.national.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSPatient;
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

    protected void sendAggregativeSMSToFacilityForAnAppointment(String ancVisitSmsKey, MotechEvent motechEvent) {
        final Map<String, Object> parameters = motechEvent.getParameters();
        final String externalId = (String) parameters.get(EventKeys.EXTERNAL_ID_KEY);
        final Patient patient = allPatients.getPatientByMotechId(externalId);
        Facility facility = allFacilities.getFacility(patient.getMrsPatient().getFacility().getId());

        smsGateway.dispatchSMSToAggregator(ancVisitSmsKey, new HashMap<String, String>() {{
            put(MOTECH_ID, externalId);
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
                return WindowName.due.name() + " 0";
            case '1':
                return WindowName.due.name() + " 1";
            case '2':
                return WindowName.due.name() + " 2";
            case '3':
                return WindowName.due.name() + " 3";
        }
        return null;
    }

    protected void sendAggregativeSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
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

    protected void sendInstantSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        final Patient patient = allPatients.patientByOpenmrsId(milestoneEvent.getExternalId());
        final MRSPatient mrsPatient = patient.getMrsPatient();
        final Facility facility = allFacilities.getFacility(mrsPatient.getFacility().getId());

        smsGateway.dispatchSMS(smsTemplateKey, new HashMap<String, String>() {{
            put(MOTECH_ID, mrsPatient.getMotechId());
            put(WINDOW, AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName());
            put(FIRST_NAME, patient.getFirstName());
            put(LAST_NAME, patient.getLastName());
            put(SCHEDULE_NAME, milestoneEvent.getScheduleName());
        }}, facility.phoneNumber());
    }

}
