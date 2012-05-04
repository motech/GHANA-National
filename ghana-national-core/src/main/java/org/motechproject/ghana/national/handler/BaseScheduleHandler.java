package org.motechproject.ghana.national.handler;

import org.apache.commons.lang.StringUtils;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseScheduleHandler {

    protected PatientService patientService;
    protected SMSGateway smsGateway;
    protected FacilityService facilityService;
    protected AllObservations allObservations;
    protected AllMobileMidwifeEnrollments allMobileMidwifeEnrollments;
    Logger logger = LoggerFactory.getLogger(this.getClass());

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
        List<String> phoneNumbers = facility.getPhoneNumbers();
        if (phoneNumbers.size() == 0) {
            logger.warn("No Phone Numbers in Facility to send SMS.");
        }
        dispatchSMSToAggregator(facility.getPhoneNumbers(), ancVisitSmsKey, patient, motechEvent);
    }

    protected void sendAggregatedSMSToPatientForAppointment(String smsTemplateKey, MotechEvent motechEvent) {
        Map<String, Object> parameters = motechEvent.getParameters();
        Patient patient = patientService.getPatientByMotechId((String) parameters.get(EventKeys.EXTERNAL_ID_KEY));
        AlertWindow alertWindow = getVisitWindow((String) parameters.get(MotechSchedulerService.JOB_ID_KEY));
        String smsTemplateName = getSMSTemplateName(smsTemplateKey, alertWindow.getPlatformWindowName());
        String phoneNumber = getPatientPhoneNumber(patient);

        if (smsTemplateName != null && StringUtils.isNotBlank(phoneNumber)) {
            dispatchSMSToAggregator(Arrays.asList(phoneNumber), smsTemplateName, patient, motechEvent);
        }
    }

    protected void sendAggregatedSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        Patient patient = patientService.patientByOpenmrsId(milestoneEvent.getExternalId());
        Facility facility = facilityService.getFacility(patient.getMrsPatient().getFacility().getId());
        dispatchSMSToAggregator(facility.getPhoneNumbers(), milestoneEvent, smsTemplateKey, patient);
    }

    protected void sendAggregatedSMSToPatient(String smsTemplateKey, MilestoneEvent milestoneEvent) {
        Patient patient = patientService.patientByOpenmrsId(milestoneEvent.getExternalId());
        String phoneNumber = getPatientPhoneNumber(patient);
        String smsTemplateName = getSMSTemplateName(smsTemplateKey, milestoneEvent.getWindowName());
        if (smsTemplateName != null && StringUtils.isNotBlank(phoneNumber)) {
            dispatchSMSToAggregator(Arrays.asList(phoneNumber), milestoneEvent, smsTemplateName, patient);
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
        String phoneNumber = getPatientPhoneNumber(patient);
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

    private void dispatchSMSToAggregator(List<String> phoneNumbers, String smsTemplateName, Patient patient, MotechEvent motechEvent) {
        Map<String, Object> parameters = motechEvent.getParameters();
        String windowName = getVisitWindow((String) parameters.get(MotechSchedulerService.JOB_ID_KEY)).getName();
        String scheduleName = (String) parameters.get(EventKeys.VISIT_NAME);
        String serialNumber = getSerialNumber(patient);
        String externalId = (String) parameters.get(EventKeys.EXTERNAL_ID_KEY);
        String messageIdentifier = new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier();
        for (String phoneNumber : phoneNumbers) {
            smsGateway.dispatchSMSToAggregator(smsTemplateName, patientDetailsMap(patient, windowName, scheduleName, serialNumber), phoneNumber, messageIdentifier);
        }

    }

    private void dispatchSMSToAggregator(List<String> phoneNumbers, MilestoneEvent milestoneEvent, String smsTemplateKey, Patient patient) {
        String windowName = AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName();
        String serialNumber = getSerialNumber(patient);
        String messageIdentifier = new AggregationMessageIdentifier(milestoneEvent.getExternalId(), milestoneEvent.getScheduleName()).getIdentifier();

        for (String phoneNumber : phoneNumbers) {
            smsGateway.dispatchSMSToAggregator(smsTemplateKey, patientDetailsMap(patient, windowName,
                    milestoneEvent.getMilestoneAlert().getMilestoneName(), serialNumber), phoneNumber, messageIdentifier);
        }
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

    private String getPatientPhoneNumber(Patient patient) {
        String phoneNumber;
        MobileMidwifeEnrollment mobileMidwifeEnrollment = allMobileMidwifeEnrollments.findActiveBy(patient.getMotechId());
        if (mobileMidwifeEnrollment != null && mobileMidwifeEnrollment.getMedium().equals(Medium.SMS)) {
            phoneNumber = mobileMidwifeEnrollment.getPhoneNumber();
        } else {
            phoneNumber = patient.getPhoneNumber();
        }
        return phoneNumber;
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
