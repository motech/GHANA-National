package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ghana.national.messagegateway.domain.DeliveryStrategy;

public class AppointmentVoicePayload extends VoicePayload {
    public AppointmentVoicePayload() {
    }

    public AppointmentVoicePayload(String clipName, String uniqueId, DateTime generationTime, DeliveryStrategy deliveryStrategy, Period validity) {
        super(clipName, uniqueId, generationTime, deliveryStrategy, validity);
    }
}
