package org.motechproject.ghana.national.handlers;

import org.motechproject.ghana.national.bean.DeliveryNotificationForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.TextMessageService;
import org.motechproject.mobileforms.api.callbacks.FormPublishHandler;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
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

    static final String ENCOUNTER_TYPE = "PREGDELNOTIFYVISIT";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CareService careService;

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
            careService.persistEncounter(deliveryNotificationForm.getMotechId(), deliveryNotificationForm.getStaffId(),
                    deliveryNotificationForm.getFacilityId(), ENCOUNTER_TYPE, deliveryNotificationForm.getDatetime().toDate(), new HashSet<MRSObservation>());
            sendDeliveryNotificationMessage(deliveryNotificationForm.getMotechId());
        } catch (Exception e) {
            log.error("Exception occured in saving Delivery Notification details for: " + deliveryNotificationForm.getMotechId(), e);
        }
    }

    private void sendDeliveryNotificationMessage(String motechId) {
        Patient patient = patientService.getPatientByMotechId(motechId);
        MRSFacility facility = patient.getMrsPatient().getFacility();
        if (facility != null) {
            String facilityPhoneNumber = facilityService.getFacility(facility.getId()).phoneNumber();
            textMessageService.sendSMS(facilityPhoneNumber, patient, Constants.DELIVERY_NOTIFICATION_SMS);
        }
    }
}

