package org.motechproject.ghana.national.eventhandler;

import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.SMS;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.TextMessageService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

@Service
public class MobileMidwifeCampaignEventHandler {

    private MobileMidwifeService mobileMidwifeService;
    private TextMessageService textMessageService;
    private PatientService patientService;

    @Autowired
    public MobileMidwifeCampaignEventHandler(MobileMidwifeService mobileMidwifeService, TextMessageService textMessageService, PatientService patientService) {
        this.mobileMidwifeService = mobileMidwifeService;
        this.textMessageService = textMessageService;
        this.patientService = patientService;
    }

    @MotechListener(subjects = {MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT})
    public void sendProgramMessage(MotechEvent event) throws ContentNotFoundException {

        Map params = event.getParameters();
        String patientId = (String) params.get(EventKeys.EXTERNAL_ID_KEY);

        MobileMidwifeEnrollment enrollment = mobileMidwifeService.findBy(patientId);
        String messageKey = (String) event.getParameters().get(EventKeys.MESSAGE_KEY);

        sendMessage(patientId, enrollment, messageKey);
        if (event.isLastEvent()) mobileMidwifeService.unregister(enrollment);
    }

    private void sendMessage(String patientId, MobileMidwifeEnrollment enrollment, String messageKey) throws ContentNotFoundException {
        final Patient patient = patientService.getPatientByMotechId(patientId);

        if (Medium.SMS.equals(enrollment.getMedium())) {
            String template = textMessageService.getSMSTemplate(messageKey);
            SMS sms = SMS.fromTemplate(template).fillPatientDetails(patient.getMotechId(), patient.getFirstName(), patient.getLastName());
            textMessageService.sendSMS(enrollment.getPhoneNumber(), sms);
        }
    }
}
