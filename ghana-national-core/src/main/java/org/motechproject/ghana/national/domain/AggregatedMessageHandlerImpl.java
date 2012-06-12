package org.motechproject.ghana.national.domain;

import org.motechproject.ghana.national.messagegateway.domain.Payload;
import org.motechproject.ghana.national.messagegateway.service.AggregatedMessageHandler;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AggregatedMessageHandlerImpl implements AggregatedMessageHandler {
    private SmsService smsService;

    private AllPatientsOutbox allPatientsOutbox;

    @Autowired
    public AggregatedMessageHandlerImpl(SmsService smsService, AllPatientsOutbox allPatientsOutbox) {
        this.smsService = smsService;
        this.allPatientsOutbox = allPatientsOutbox;
    }

    @Override
    public void handle(List<Payload> payload) {
        for (Payload message : payload) {
            if (message instanceof SMSPayload) {
                SMSPayload sms = (SMSPayload) message;
                smsService.sendSMS(sms.getPhoneNumber(), sms.getText());
            } else if (message instanceof VoicePayload) {
                if(message instanceof CareVoicePayload){
                    CareVoicePayload voice = (CareVoicePayload) message;
                    allPatientsOutbox.addCareMessage(voice.getUniqueId(), voice.getClipName(), voice.getValidity(), voice.getScheduleWindow(), voice.getScheduleWindowStart());
                }else if(message instanceof AppointmentVoicePayload){
                    AppointmentVoicePayload voice = (AppointmentVoicePayload) message;
                    allPatientsOutbox.addAppointmentMessage(voice.getUniqueId(), voice.getClipName(), voice.getValidity());
                }else if(message instanceof MobileMidwifeVoicePayload){
                    MobileMidwifeVoicePayload voice = (MobileMidwifeVoicePayload) message;
                    allPatientsOutbox.addMobileMidwifeMessage(voice.getUniqueId(), voice.getClipName(), voice.getValidity());
                }
            }
        }
    }
}
