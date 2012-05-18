package org.motechproject.ghana.national.handler;

import org.apache.commons.lang.StringUtils;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.messagegateway.domain.MessageRecipientType;
import org.motechproject.ghana.national.repository.AllMobileMidwifeEnrollments;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseScheduleHandler {

    protected PatientService patientService;
    protected SMSGateway smsGateway;
    protected FacilityService facilityService;
    protected AllObservations allObservations;
    protected AllMobileMidwifeEnrollments allMobileMidwifeEnrollments;

    protected BaseScheduleHandler() {
    }

    protected BaseScheduleHandler(PatientService patientService, FacilityService facilityService,
                                  SMSGateway smsGateway, AllObservations allObservations, AllMobileMidwifeEnrollments allMobileMidwifeEnrollments) {
        this.patientService = patientService;
        this.smsGateway = smsGateway;
        this.facilityService = facilityService;
        this.allObservations = allObservations;
        this.allMobileMidwifeEnrollments = allMobileMidwifeEnrollments;
    }

    protected void sendAggregatedSMSToFacilityForAnAppointment(String ancVisitSmsKey, MotechEvent motechEvent) {
        Map<String, Object> parameters = motechEvent.getParameters();
        Patient patient = patientService.getPatientByMotechId((String) parameters.get(EventKeys.EXTERNAL_ID_KEY));
        Facility facility = facilityService.getFacility(patient.getMrsPatient().getFacility().getId());
        dispatchSMSToAggregator(facility.getMotechId(), ancVisitSmsKey, patient, motechEvent, MessageRecipientType.FACILITY);
    }

    protected void sendAggregatedSMSToPatientForAppointment(String smsTemplateKey, MotechEvent motechEvent) {
        Map<String, Object> parameters = motechEvent.getParameters();
        Patient patient = patientService.getPatientByMotechId((String) parameters.get(EventKeys.EXTERNAL_ID_KEY));
        AlertWindow alertWindow = getVisitWindow((String) parameters.get(MotechSchedulerService.JOB_ID_KEY));
        String smsTemplateName = getSMSTemplateName(smsTemplateKey, alertWindow.getPlatformWindowName());

        if (smsTemplateName != null) {
            dispatchSMSToAggregator(patient.getMotechId(), smsTemplateName, patient, motechEvent, MessageRecipientType.PATIENT);
        }
    }

    protected void sendAggregatedSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        Patient patient = patientService.patientByOpenmrsId(milestoneEvent.getExternalId());
        Facility facility = facilityService.getFacility(patient.getMrsPatient().getFacility().getId());
        dispatchSMSToAggregator(facility.getMotechId(), milestoneEvent, smsTemplateKey, patient, MessageRecipientType.FACILITY);
    }

    protected void sendAggregatedSMSToPatient(String smsTemplateKey, MilestoneEvent milestoneEvent) {
        Patient patient = patientService.patientByOpenmrsId(milestoneEvent.getExternalId());
        String smsTemplateName = getSMSTemplateName(smsTemplateKey, milestoneEvent.getWindowName());
        if (smsTemplateName != null) {
            dispatchSMSToAggregator(patient.getMotechId(), milestoneEvent, smsTemplateName, patient, MessageRecipientType.PATIENT);
        }
    }

    protected void sendInstantSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        Patient patient = patientService.patientByOpenmrsId(milestoneEvent.getExternalId());
        Facility facility = facilityService.getFacility(patient.getMrsPatient().getFacility().getId());
        String windowName = AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName();
        for (String phoneNumber : facility.getPhoneNumbers()) {
            smsGateway.dispatchSMS(smsTemplateKey, patientDetailsMap(patient, windowName, milestoneEvent.getMilestoneAlert().getMilestoneName(), null), phoneNumber);
        }
    }

    protected void sendInstantSMSToPatient(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        Patient patient = patientService.patientByOpenmrsId(milestoneEvent.getExternalId());
        String phoneNumber = patientService.getPatientPhoneNumber(patient.getMotechId());
        if (StringUtils.isNotBlank(phoneNumber)) {
            String windowName = AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName();
            smsGateway.dispatchSMS(smsTemplateKey, patientDetailsMap(patient, windowName, milestoneEvent.getMilestoneAlert().getMilestoneName(), null), phoneNumber);
        }
    }

    private AlertWindow getVisitWindow(String jobId) {
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

    private void dispatchSMSToAggregator(String facilityMotechId, String smsTemplateName, Patient patient,
                                         MotechEvent motechEvent, MessageRecipientType messageRecipientType) {
        Map<String, Object> parameters = motechEvent.getParameters();
        String windowName = getVisitWindow((String) parameters.get(MotechSchedulerService.JOB_ID_KEY)).getName();
        String scheduleName = (String) parameters.get(EventKeys.VISIT_NAME);
        String serialNumber = getSerialNumber(patient);
        String externalId = (String) parameters.get(EventKeys.EXTERNAL_ID_KEY);
        String messageIdentifier = new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier();
        smsGateway.dispatchSMSToAggregator(smsTemplateName, patientDetailsMap(patient, windowName, scheduleName, serialNumber),
                facilityMotechId, messageIdentifier, messageRecipientType);
    }

    private void dispatchSMSToAggregator(String facilityMotechId, MilestoneEvent milestoneEvent, String smsTemplateKey,
                                         Patient patient, MessageRecipientType messageRecipientType) {
        String windowName = AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName();
        String serialNumber = getSerialNumber(patient);
        String messageIdentifier = new AggregationMessageIdentifier(milestoneEvent.getExternalId(), milestoneEvent.getScheduleName()).getIdentifier();

        smsGateway.dispatchSMSToAggregator(smsTemplateKey, patientDetailsMap(patient, windowName,
                milestoneEvent.getMilestoneAlert().getMilestoneName(), serialNumber),
                facilityMotechId, messageIdentifier, messageRecipientType);
    }

    private String getSerialNumber(Patient patient) {
        MRSObservation serialNumberObs = allObservations.findLatestObservation(patient.getMotechId(), Concept.SERIAL_NUMBER.getName());
        String serialNumber = "-";
        if (serialNumberObs != null) {
            serialNumber = (String) serialNumberObs.getValue();
        }
        return serialNumber;
    }

    private HashMap<String, String> patientDetailsMap(final Patient patient, final String windowName, final String scheduleName, String serialNumber) {
        return new SMSTemplate().fillPatientDetails(patient).fillScheduleDetails(scheduleName, windowName).fillSerialNumber(serialNumber).getRuntimeVariables();
    }

    private String getSMSTemplateName(String smsTemplateKey, String visitWindow) {
        if (visitWindow.equals(WindowName.due.name())) {
            smsTemplateKey = smsTemplateKey + SmsTemplateKeys.PATIENT_DUE_SMS_KEY;
        } else if (visitWindow.equals(WindowName.late.name())) {
            smsTemplateKey = smsTemplateKey + SmsTemplateKeys.PATIENT_LATE_SMS_KEY;
        } else {
            return null;
        }
        return smsTemplateKey;
    }
}
