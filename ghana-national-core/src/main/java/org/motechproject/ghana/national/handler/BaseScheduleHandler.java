package org.motechproject.ghana.national.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.SMSTemplate;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import java.util.HashMap;
import java.util.Map;

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
        String externalId = (String) parameters.get(EventKeys.EXTERNAL_ID_KEY);
        final Patient patient = allPatients.getPatientByMotechId(externalId);

        Facility facility = allFacilities.getFacility(patient.getMrsPatient().getFacility().getId());

        final String windowName = getVisitWindow((String) parameters.get(MotechSchedulerService.JOB_ID_KEY));
        final String scheduleName = (String) parameters.get(EventKeys.VISIT_NAME);
        for (String phoneNumber : facility.getPhoneNumbers()) {
            smsGateway.dispatchSMSToAggregator(ancVisitSmsKey, patientDetailsMap(patient, windowName, scheduleName), phoneNumber);
        }
    }

    private String getVisitWindow(String jobId) {
        char reminderCount = jobId.charAt(jobId.length() - 1);
        switch (reminderCount) {
            case '0':
                return "Due";
            case '1':
            case '2':
            case '3':
                return "Overdue";
        }
        return null;
    }

    protected void sendAggregativeSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        String externalId = milestoneEvent.getExternalId();
        final Patient patient = allPatients.patientByOpenmrsId(externalId);

        Facility facility = allFacilities.getFacility(patient.getMrsPatient().getFacility().getId());

        final String windowName = AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName();
        for (String phoneNumber : facility.getPhoneNumbers()) {
            smsGateway.dispatchSMSToAggregator(smsTemplateKey, patientDetailsMap(patient, windowName, milestoneEvent.getScheduleName()), phoneNumber);
        }
    }

    protected void sendInstantSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        final Patient patient = allPatients.patientByOpenmrsId(milestoneEvent.getExternalId());
        final MRSPatient mrsPatient = patient.getMrsPatient();
        final Facility facility = allFacilities.getFacility(mrsPatient.getFacility().getId());

        final String windowName = AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName();
        for (String phoneNumber : facility.getPhoneNumbers()) {
            smsGateway.dispatchSMS(smsTemplateKey, patientDetailsMap(patient, windowName, milestoneEvent.getScheduleName()), phoneNumber);
        }
    }

    private HashMap<String, String> patientDetailsMap(final Patient patient, final String windowName, final String scheduleName) {
        return new SMSTemplate().fillPatientDetails(patient).fillScheduleDetails(scheduleName, windowName).getRuntimeVariables();
    }
}
