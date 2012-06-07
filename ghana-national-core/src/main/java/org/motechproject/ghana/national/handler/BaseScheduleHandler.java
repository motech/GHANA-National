package org.motechproject.ghana.national.handler;

import org.apache.commons.lang.StringUtils;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.messagegateway.domain.MessageRecipientType;
import org.motechproject.ghana.national.repository.*;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseScheduleHandler {

    protected PatientService patientService;
    protected SMSGateway smsGateway;
    private VoiceGateway voiceGateway;
    protected AllPatientsOutbox allPatientsOutbox;
    protected FacilityService facilityService;
    protected AllObservations allObservations;
    protected AllMobileMidwifeEnrollments allMobileMidwifeEnrollments;

    @Value("#{verboiceProperties['resource.url']}")
    private String resourceBaseUrl;

    protected BaseScheduleHandler() {
    }

    protected BaseScheduleHandler(PatientService patientService, FacilityService facilityService, SMSGateway smsGateway,
                                  VoiceGateway voiceGateway, AllObservations allObservations,
                                  AllMobileMidwifeEnrollments allMobileMidwifeEnrollments, AllPatientsOutbox allPatientsOutbox) {

        this.patientService = patientService;
        this.smsGateway = smsGateway;
        this.voiceGateway = voiceGateway;
        this.facilityService = facilityService;
        this.allObservations = allObservations;
        this.allMobileMidwifeEnrollments = allMobileMidwifeEnrollments;
        this.allPatientsOutbox = allPatientsOutbox;
    }

    protected void sendAggregatedSMSToFacilityForAnAppointment(String ancVisitSmsKey, MotechEvent motechEvent) {
        Map<String, Object> parameters = motechEvent.getParameters();
        Patient patient = patientService.getPatientByMotechId((String) parameters.get(EventKeys.EXTERNAL_ID_KEY));
        Facility facility = facilityService.getFacility(patient.getMrsPatient().getFacility().getId());
        dispatchSMSToAggregator(facility.getMotechId(), ancVisitSmsKey, patient, AlertDetails.createFromAppointment(motechEvent), MessageRecipientType.FACILITY);
    }

    protected void sendAggregatedSMSToPatientForAppointment(String smsTemplateKey, MotechEvent motechEvent) {
        AlertDetails alertDetails = AlertDetails.createFromAppointment(motechEvent);
        Patient patient = patientService.getPatientByMotechId(alertDetails.getScheduleId());
        dispatchPatientMessageToAggregator(smsTemplateKey, alertDetails, patient);
    }

    protected void sendAggregatedMessageToPatient(String smsTemplateKey, MilestoneEvent milestoneEvent) {
        AlertDetails alertDetails = AlertDetails.createFromSchedule(milestoneEvent);
        Patient patient = patientService.patientByOpenmrsId(alertDetails.getScheduleId());
        dispatchPatientMessageToAggregator(smsTemplateKey, alertDetails, patient);
    }

    private void dispatchPatientMessageToAggregator(String smsTemplateKey, AlertDetails alertDetails, Patient patient) {
        MobileMidwifeEnrollment mobileMidwifeEnrollment = allMobileMidwifeEnrollments.findActiveBy(patient.getMotechId());
        Medium communicationMedium = getCommunicationMedium(mobileMidwifeEnrollment, Medium.SMS);
        if (dueOrLateWindow(alertDetails.getWindow())) {
            if (Medium.SMS.equals(communicationMedium)) {
                String smsTemplateKeyForWindow = formatTemplateKeyForDueAndLateWindow(smsTemplateKey, alertDetails.getWindow().getPlatformWindowName());
                dispatchSMSToAggregator(patient.getMotechId(), smsTemplateKeyForWindow, patient, alertDetails, MessageRecipientType.PATIENT);
            } else {
                voiceGateway.dispatchVoiceToAggregator(new IVRClip().name(alertDetails.getScheduleName(), alertDetails.getWindow()), getRecipientIdentifierForAggregation(alertDetails), patient.getMotechId());
            }
        }
    }

    protected void sendInstantMessageToPatient(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        AlertDetails alertDetails = AlertDetails.createFromSchedule(milestoneEvent);

        Patient patient = patientService.patientByOpenmrsId(alertDetails.getScheduleId());
        MobileMidwifeEnrollment mobileMidwifeEnrollment = allMobileMidwifeEnrollments.findActiveBy(patient.getMotechId());
        Medium communicationMedium = getCommunicationMedium(mobileMidwifeEnrollment, Medium.SMS);
        if (dueOrLateWindow(alertDetails.getWindow())) {
            if (Medium.SMS.equals(communicationMedium)) {
                String phoneNumber = patient.receiveSMSOnPhoneNumber(mobileMidwifeEnrollment);
                if (StringUtils.isNotBlank(phoneNumber)) {
                    String smsTemplateKeyForWindow = formatTemplateKeyForDueAndLateWindow(smsTemplateKey, alertDetails.getWindow().getPlatformWindowName());
                    smsGateway.dispatchSMS(smsTemplateKeyForWindow, patientDetailsMap(patient, alertDetails.getWindow().getName(), alertDetails.getMilestoneName(), null), phoneNumber);
                }
            } else {
                allPatientsOutbox.addAudioClip(patient.getMotechId(), new IVRClip().name(alertDetails.getScheduleName(), alertDetails.getWindow()), DateUtil.now());
            }
        }
    }

    private boolean dueOrLateWindow(AlertWindow alertWindow) {
        return AlertWindow.DUE.equals(alertWindow) || AlertWindow.OVERDUE.equals(alertWindow);
    }

    protected void sendAggregatedSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        Patient patient = patientService.patientByOpenmrsId(milestoneEvent.getExternalId());
        Facility facility = facilityService.getFacility(patient.getMrsPatient().getFacility().getId());
        dispatchSMSToAggregator(facility.getMotechId(), smsTemplateKey, patient, AlertDetails.createFromSchedule(milestoneEvent), MessageRecipientType.FACILITY);
    }

    protected void sendInstantSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        Patient patient = patientService.patientByOpenmrsId(milestoneEvent.getExternalId());
        Facility facility = facilityService.getFacility(patient.getMrsPatient().getFacility().getId());
        String windowName = AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName();
        for (String phoneNumber : facility.getPhoneNumbers()) {
            smsGateway.dispatchSMS(smsTemplateKey, patientDetailsMap(patient, windowName, milestoneEvent.getMilestoneAlert().getMilestoneName(), null), phoneNumber);
        }
    }

    private Medium getCommunicationMedium(MobileMidwifeEnrollment mobileMidwifeEnrollment, Medium defaultMedium) {
        if (mobileMidwifeEnrollment != null) {
            return mobileMidwifeEnrollment.getMedium();
        }
        return defaultMedium;
    }

    private void dispatchSMSToAggregator(String motechId, String smsTemplateKey, Patient patient, AlertDetails alertDetails,
                                         MessageRecipientType messageRecipientType) {

        String serialNumber = getSerialNumber(patient);
        String smsIdentifier = getRecipientIdentifierForAggregation(alertDetails);

        smsGateway.dispatchSMSToAggregator(smsTemplateKey, patientDetailsMap(patient, alertDetails.getWindow().getName(),
                alertDetails.getMilestoneName(), serialNumber), motechId, smsIdentifier, messageRecipientType);
    }

    private String getRecipientIdentifierForAggregation(AlertDetails alertDetails) {
        return new AggregationMessageIdentifier(alertDetails.getScheduleId(), alertDetails.getScheduleName()).getIdentifier();
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

    private String formatTemplateKeyForDueAndLateWindow(String smsTemplateKey, String visitWindow) {
        if (visitWindow.equals(WindowName.due.name())) {
            return smsTemplateKey + SmsTemplateKeys.PATIENT_DUE_SMS_KEY;
        }
        if (visitWindow.equals(WindowName.late.name())) {
            return smsTemplateKey + SmsTemplateKeys.PATIENT_LATE_SMS_KEY;
        }
        return null;
    }
}
