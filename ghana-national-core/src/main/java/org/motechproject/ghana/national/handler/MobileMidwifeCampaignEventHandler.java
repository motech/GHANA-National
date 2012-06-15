package org.motechproject.ghana.national.handler;

import org.joda.time.Period;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.exception.EventHandlerException;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.repository.IVRGateway;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

@Component
public class MobileMidwifeCampaignEventHandler {

    private Logger logger = LoggerFactory.getLogger(MobileMidwifeCampaignEventHandler.class);

    @Autowired
    private MobileMidwifeService mobileMidwifeService;
    @Autowired
    private AllPatientsOutbox allPatientsOutbox;
    @Autowired
    private SMSGateway smsGateway;
    @Autowired
    private PatientService patientService;
    @Autowired
    private IVRGateway ivrGateway;
    @Value("#{ghanaNationalProperties['host']}")
    private String host;
    @Value("#{ghanaNationalProperties['port']}")
    private String port;
    @Value("#{ghanaNationalProperties['context.path']}")
    private String contextPath;

    @MotechListener(subjects = {MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT})
    public void sendProgramMessage(MotechEvent event) {
        try {
            Map params = event.getParameters();
            String patientId = (String) params.get(EventKeys.EXTERNAL_ID_KEY);

            MobileMidwifeEnrollment enrollment = mobileMidwifeService.findActiveBy(patientId);
            String generatedMessageKey = (String) event.getParameters().get(EventKeys.GENERATED_MESSAGE_KEY);

            if (event.isLastEvent()) mobileMidwifeService.unRegister(patientId);
            sendMessage(enrollment, generatedMessageKey);
        } catch (Exception e) {
            logger.error("<MobileMidwifeEvent>: Encountered error while sending alert: ", e);
            throw new EventHandlerException(event, e);
        }
    }

    public void sendMessage(MobileMidwifeEnrollment enrollment, String messageKey) throws ContentNotFoundException {
        if (Medium.SMS.equals(enrollment.getMedium())) {
            smsGateway.dispatchSMS(messageKey, enrollment.getLanguage().name(), enrollment.getPhoneNumber());
        } else if (Medium.VOICE.equals(enrollment.getMedium())) {
            placeMobileMidwifeMessagesToOutbox(enrollment, messageKey);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("callback_url", "http://" + host + ":" + port + "/" + contextPath + "/outgoing/call?motechId=" + enrollment.getPatientId() + "&ln=" + enrollment.getLanguage().name());
            ivrGateway.placeCall(enrollment.getPhoneNumber(), params);
        }
    }

    private void placeMobileMidwifeMessagesToOutbox(MobileMidwifeEnrollment enrollment, String messageKey) {
        allPatientsOutbox.addAudioFileName(enrollment.getPatientId(), AudioPrompts.fileNameForMobileMidwife(enrollment.getServiceType().getValue(), messageKey), Period.weeks(1));
    }
}
