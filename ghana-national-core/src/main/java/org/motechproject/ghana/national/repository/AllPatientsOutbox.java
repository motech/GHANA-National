package org.motechproject.ghana.national.repository;

import ch.lambdaj.function.convert.Converter;
import org.joda.time.DateTime;
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

    public static final String AUDIO_URL = "AUDIO_URL";
    @Autowired
    VoiceOutboxService voiceOutboxService;

    public void addAudioClip(String motechId, final String clipName, DateTime expirationTime) {
        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setCreationTime(DateTime.now().toDate());
        outboundVoiceMessage.setExpirationDate(expirationTime.toDate());
        outboundVoiceMessage.setExternalId(motechId);
        outboundVoiceMessage.setParameters(new HashMap<String, Object>(){{
            put(AUDIO_URL, clipName);
        }});
        voiceOutboxService.addMessage(outboundVoiceMessage);
    }

    public List getAudioUrlsFor(String externalId, String language) {
        List<OutboundVoiceMessage> messages = voiceOutboxService.getMessages(externalId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime);
        return convert(extract(messages, on(OutboundVoiceMessage.class).getParameters()), new Converter<Object, Object>() {
            @Override
            public Object convert(Object o) {
                Map<String, Object> obj = (Map<String, Object>) o;
                return obj.get(AUDIO_URL);
            }
        });
    }
}
