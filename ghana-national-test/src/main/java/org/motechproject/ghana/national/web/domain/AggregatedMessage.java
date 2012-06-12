package org.motechproject.ghana.national.web.domain;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.SMSPayload;
import org.motechproject.ghana.national.domain.VoicePayload;

public class AggregatedMessage {
    private DateTime generationTime;
    private DateTime deliveryTime;
    private String uniqueId;
    private String content;

    public AggregatedMessage(SMSPayload payload) {
        generationTime = payload.getGenerationTime();
        deliveryTime = payload.getDeliveryTime();
        uniqueId = payload.getUniqueId();
        content = payload.getText();
    }

    public AggregatedMessage(VoicePayload voicePayload) {
        generationTime = voicePayload.getGenerationTime();
        deliveryTime = voicePayload.getDeliveryTime();
        uniqueId = voicePayload.getUniqueId();
        content = voicePayload.getClipName();
    }

    public DateTime getGenerationTime() {
        return generationTime;
    }

    public DateTime getDeliveryTime() {
        return deliveryTime;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getContent() {
        return content;
    }

    public void setGenerationTime(DateTime generationTime) {
        this.generationTime = generationTime;
    }

    public void setDeliveryTime(DateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
