package org.motechproject.ghana.national.handler;

import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.exception.EventHandlerException;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;

@Component
public class MobileMidwifeCampaignEventHandler {

    private Logger logger = LoggerFactory.getLogger(MobileMidwifeCampaignEventHandler.class);

    @Autowired
    private MobileMidwifeService mobileMidwifeService;
    @Autowired
    private SMSGateway smsGateway;
    @Autowired
    private PatientService patientService;

    @MotechListener(subjects = {MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT})
    public void sendProgramMessage(MotechEvent event) {

        try {
            Map params = event.getParameters();
            String patientId = (String) params.get(EventKeys.EXTERNAL_ID_KEY);

            MobileMidwifeEnrollment enrollment = mobileMidwifeService.findActiveBy(patientId);
            String messageKey = (String) event.getParameters().get(EventKeys.MESSAGE_KEY);

            if (event.isLastEvent()) mobileMidwifeService.unRegister(patientId);
            sendMessage(enrollment, messageKey);
        } catch (Exception e) {
            logger.error("<MobileMidwifeEvent>: Encountered error while sending alert: ", e);
            throw new EventHandlerException(event, e);
        }
    }

    public void sendMessage(MobileMidwifeEnrollment enrollment, String messageKey) throws ContentNotFoundException {

        if (Medium.SMS.equals(enrollment.getMedium())) {
            smsGateway.dispatchSMS(messageKey, enrollment.getLanguage().name(), enrollment.getPhoneNumber());
        }
    }
}
