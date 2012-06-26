package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ghana.national.domain.ivr.MobileMidwifeAudioClips;
import org.motechproject.ghana.national.messagegateway.domain.DeliveryStrategy;

public class MobileMidwifeVoicePayload extends VoicePayload {
    MobileMidwifeAudioClips clips;
    public MobileMidwifeVoicePayload() {
    }

    public MobileMidwifeVoicePayload(MobileMidwifeAudioClips clips, String uniqueId, DateTime generationTime, DeliveryStrategy deliveryStrategy, Period validity) {
        super(uniqueId, generationTime, deliveryStrategy, validity);
        this.clips = clips;
    }

    public MobileMidwifeAudioClips getClips() {
        return clips;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MobileMidwifeVoicePayload that = (MobileMidwifeVoicePayload) o;

        if (clips != that.clips) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clips != null ? clips.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MobileMidwifeVoicePayload{" +
                "clips=" + clips +
                '}';
    }
}
