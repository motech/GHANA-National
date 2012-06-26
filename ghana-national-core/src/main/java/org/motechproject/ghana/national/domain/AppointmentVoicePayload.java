package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ghana.national.messagegateway.domain.DeliveryStrategy;

public class AppointmentVoicePayload extends VoicePayload {
    private String clipName;

    public AppointmentVoicePayload() {
    }

    public AppointmentVoicePayload(String clipName, String uniqueId, DateTime generationTime, DeliveryStrategy deliveryStrategy, Period validity) {
        super(uniqueId, generationTime, deliveryStrategy, validity);
        this.clipName = clipName;
    }

    public String getClipName() {
        return clipName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AppointmentVoicePayload that = (AppointmentVoicePayload) o;

        if (clipName != null ? !clipName.equals(that.clipName) : that.clipName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clipName != null ? clipName.hashCode() : 0);
        return result;
    }

    @Override
    public String   toString() {
        return "AppointmentVoicePayload{" +
                "clipName='" + clipName + '\'' +
                '}';
    }
}
