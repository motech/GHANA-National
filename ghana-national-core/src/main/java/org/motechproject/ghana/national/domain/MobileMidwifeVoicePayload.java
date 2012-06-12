package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ghana.national.messagegateway.domain.DeliveryStrategy;

public class MobileMidwifeVoicePayload extends VoicePayload {
    public MobileMidwifeVoicePayload() {
    }

    public MobileMidwifeVoicePayload(String clipName, String uniqueId, DateTime generationTime, DeliveryStrategy deliveryStrategy, Period validity) {
        super(clipName, uniqueId, generationTime, deliveryStrategy, validity);
    }
}
