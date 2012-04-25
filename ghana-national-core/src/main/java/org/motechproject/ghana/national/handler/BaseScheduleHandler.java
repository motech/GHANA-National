package org.motechproject.ghana.national.handler;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseScheduleHandler {

    protected PatientService patientService;
    protected SMSGateway smsGateway;
    protected FacilityService facilityService;
    protected AllObservations allObservations;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    protected BaseScheduleHandler() {
    }

    protected BaseScheduleHandler(PatientService patientService, FacilityService facilityService, SMSGateway smsGateway, AllObservations allObservations) {
        this.patientService = patientService;
        this.smsGateway = smsGateway;
        this.facilityService = facilityService;
        this.allObservations = allObservations;
    }

    protected void sendAggregatedSMSToFacilityForAnAppointment(String ancVisitSmsKey, MotechEvent motechEvent) {
        final Map<String, Object> parameters = motechEvent.getParameters();
        String externalId = (String) parameters.get(EventKeys.EXTERNAL_ID_KEY);
        final Patient patient = patientService.getPatientByMotechId(externalId);
        String serialNumber = "-";
        MRSObservation observation = allObservations.findLatestObservation(patient.getMotechId(), Concept.SERIAL_NUMBER.getName());
        if (observation != null) {
            serialNumber = (String) observation.getValue();
        }

        Facility facility = facilityService.getFacility(patient.getMrsPatient().getFacility().getId());

        final String windowName = getVisitWindow((String) parameters.get(MotechSchedulerService.JOB_ID_KEY));
        final String scheduleName = (String) parameters.get(EventKeys.VISIT_NAME);
        List<String> phoneNumbers = facility.getPhoneNumbers();
        if (phoneNumbers.size() == 0) {
            logger.warn("No Phone Numbers in Facility to send SMS.");
        }
        final String messageIdentifier = new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier();
        for (String phoneNumber : phoneNumbers) {
            smsGateway.dispatchSMSToAggregator(ancVisitSmsKey, patientDetailsMap(patient, windowName, scheduleName, serialNumber), phoneNumber, messageIdentifier);
        }
    }

    private String getVisitWindow(String jobId) {
        char reminderCount = jobId.charAt(jobId.length() - 1);
        switch (reminderCount) {
            case '0':
                return AlertWindow.DUE.getName();
            case '1':
            case '2':
            case '3':
                return AlertWindow.OVERDUE.getName();
        }
        return null;
    }

    protected void sendAggregatedSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        String externalId = milestoneEvent.getExternalId();
        final Patient patient = patientService.patientByOpenmrsId(externalId);

        Facility facility = facilityService.getFacility(patient.getMrsPatient().getFacility().getId());

        final String windowName = AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName();
        String serialNumber = getSerialNumber(patient);
        final String messageIdentifier = new AggregationMessageIdentifier(milestoneEvent.getExternalId(), milestoneEvent.getScheduleName()).getIdentifier();

        for (String phoneNumber : facility.getPhoneNumbers()) {
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

    protected void sendInstantSMSToFacility(String smsTemplateKey, final MilestoneEvent milestoneEvent) {
        Patient patient = patientService.patientByOpenmrsId(milestoneEvent.getExternalId());
        MRSPatient mrsPatient = patient.getMrsPatient();
        Facility facility = facilityService.getFacility(mrsPatient.getFacility().getId());

        String windowName = AlertWindow.byPlatformName(milestoneEvent.getWindowName()).getName();
        for (String phoneNumber : facility.getPhoneNumbers()) {
            smsGateway.dispatchSMS(smsTemplateKey, patientDetailsMap(patient, windowName, milestoneEvent.getMilestoneAlert().getMilestoneName(), null), phoneNumber);
        }
    }

    private HashMap<String, String> patientDetailsMap(final Patient patient, final String windowName, final String scheduleName, String serialNumber) {
        return new SMSTemplate().fillPatientDetails(patient).fillScheduleDetails(scheduleName, windowName).fillSerialNumber(serialNumber).getRuntimeVariables();
    }
}
