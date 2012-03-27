package org.motechproject.ghana.national.handlers;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.bean.DeliveryNotificationForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.SMSTemplate;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
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

import static org.motechproject.ghana.national.domain.EncounterType.PREG_DEL_NOTIFY_VISIT;

@Component
public class DeliveryNotificationFormHandler implements FormPublishHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    static final String DELIVERY_NOTIFICATION_SMS_KEY = "DELIVERY_NOTIFICATION_SMS_KEY";

    @Autowired
    AllEncounters allEncounters;

    @Autowired
    PatientService patientService;

    @Autowired
    FacilityService facilityService;

    @Autowired
    SMSGateway smsGateway;

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
            allEncounters.persistEncounter(patient.getMrsPatient(), deliveryNotificationForm.getStaffId(),
                    facility.getMrsFacilityId(), PREG_DEL_NOTIFY_VISIT.value(),
                    deliveryTime.toDate(), new HashSet<MRSObservation>());
            sendDeliveryNotificationMessage(deliveryNotificationForm.getMotechId(), deliveryTime);
        } catch (Exception e) {
            log.error("Exception occured in saving Delivery Notification details for: " + deliveryNotificationForm.getMotechId(), e);
        }
    }

    private void sendDeliveryNotificationMessage(String motechId, DateTime deliveryTime) {
        final Patient patient = patientService.getPatientByMotechId(motechId);
        if (patient.getMrsPatient().getFacility() != null) {
            Facility facility = facilityService.getFacility(patient.getMrsPatient().getFacility().getId());

            for (String phoneNumber : facility.getPhoneNumbers()) {
                smsGateway.dispatchSMS(DELIVERY_NOTIFICATION_SMS_KEY, new SMSTemplate().fillPatientDetails(patient)
                        .fillDeliveryTime(deliveryTime).getRuntimeVariables(), phoneNumber);
            }
        }
    }
}

