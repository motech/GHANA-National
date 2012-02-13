package org.motechproject.ghana.national.handlers;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.bean.DeliveryNotificationForm;
import org.motechproject.ghana.national.configuration.TextMessageTemplates;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.SMS;
import org.motechproject.ghana.national.service.EncounterService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.TextMessageService;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class DeliveryNotificationFormHandler implements FormPublishHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EncounterService encounterService;

    @Autowired
    PatientService patientService;

    @Autowired
    FacilityService facilityService;

    @Autowired
    TextMessageService textMessageService;

    @Override
    @MotechListener(subjects = "form.validation.successful.NurseDataEntry.deliveryNotify")
    @LoginAsAdmin
    @ApiSession
    public void handleFormEvent(MotechEvent event) {
        DeliveryNotificationForm deliveryNotificationForm = null;
        try {
            deliveryNotificationForm = (DeliveryNotificationForm) event.getParameters().get(Constants.FORM_BEAN);
            Patient patient = patientService.getPatientByMotechId(deliveryNotificationForm.getMotechId());
            Facility facility = facilityService.getFacilityByMotechId(deliveryNotificationForm.getFacilityId());
            DateTime deliveryTime = deliveryNotificationForm.getDatetime();
            encounterService.persistEncounter(patient.getMrsPatient(), deliveryNotificationForm.getStaffId(),
                    facility.getMrsFacilityId(), Constants.ENCOUNTER_PREGDELNOTIFYVISIT,
                    deliveryTime.toDate(), new HashSet<MRSObservation>());
            sendDeliveryNotificationMessage(deliveryNotificationForm.getMotechId(), deliveryTime);
        } catch (Exception e) {
            log.error("Exception occured in saving Delivery Notification details for: " + deliveryNotificationForm.getMotechId(), e);
        }
    }

    private void sendDeliveryNotificationMessage(String motechId, DateTime deliveryTime) {
        Patient patient = patientService.getPatientByMotechId(motechId);
        if (patient.getMrsPatient().getFacility() != null) {
            Facility facility = facilityService.getFacility(patient.getMrsPatient().getFacility().getId());
            String template = textMessageService.getSMSTemplate(TextMessageTemplates.DELIVERY_NOTIFICATION_SMS[0]);
            SMS sms = SMS.fromTemplate(template).fillPatientDetails(patient.getMotechId(), patient.getFirstName(), patient.getLastName()).fillDateTime(deliveryTime.toDate());
            textMessageService.sendSMS(facility, sms);
        }
    }
}

