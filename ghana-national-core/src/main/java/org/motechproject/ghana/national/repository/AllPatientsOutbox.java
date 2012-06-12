package org.motechproject.ghana.national.repository;

import ch.lambdaj.function.convert.Converter;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ghana.national.domain.AlertType;
import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.outbox.api.contract.SortKey;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Repository
public class AllPatientsOutbox {

    public static final String AUDIO_CLIP_NAME = "AUDIO_CLIP_NAME";
    public static final String WINDOW = "WINDOW";
    public static final String WINDOW_START = "WINDOW_START";
    public static final String TYPE = "TYPE";

    @Autowired
    VoiceOutboxService voiceOutboxService;

    public void addAppointmentMessage(String motechId, final String clipName, Period validity) {
        HashMap<String, Object> messageAttributes = new HashMap<String, Object>() {{
            put(AUDIO_CLIP_NAME, clipName);
            put(TYPE, AlertType.APPOINTMENT);
        }};
        addMessage(motechId, validity, messageAttributes);
    }

    public void addMobileMidwifeMessage(String motechId, final String clipName, Period validity) {
        HashMap<String, Object> messageAttributes = new HashMap<String, Object>() {{
            put(AUDIO_CLIP_NAME, clipName);
            put(TYPE, AlertType.MOBILE_MIDWIFE);
        }};
        addMessage(motechId, validity, messageAttributes);
    }

    public void addCareMessage(String motechId, final String clipName, Period validity, final AlertWindow window, final DateTime windowStart) {
        HashMap<String, Object> messageAttributes = new HashMap<String, Object>() {{
            put(AUDIO_CLIP_NAME, clipName);
            put(WINDOW, window);
            put(WINDOW_START, windowStart);
            put(TYPE, AlertType.CARE);
        }};
        addMessage(motechId, validity, messageAttributes);
    }

    private void addMessage(String motechId, Period validity, HashMap<String, Object> messageAttributes) {
        DateTime now = DateTime.now();
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setCreationTime(now.toDate());
        outboundVoiceMessage.setExpirationDate(now.plus(validity).toDate());
        outboundVoiceMessage.setExternalId(motechId);
        outboundVoiceMessage.setParameters(messageAttributes);
        voiceOutboxService.addMessage(outboundVoiceMessage);
    }

    public List getAudioFileNames(String externalId) {
        List<OutboundVoiceMessage> messages = voiceOutboxService.getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
        return convert(extract(messages, on(OutboundVoiceMessage.class).getParameters()), new Converter<Object, Object>() {
            @Override
            public Object convert(Object o) {
                Map<String, Object> obj = (Map<String, Object>) o;
                return obj.get(AUDIO_CLIP_NAME);
            }
        });
    }
}
