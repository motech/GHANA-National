package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ghana.national.messagegateway.domain.DeliveryStrategy;

public class CareVoicePayload extends VoicePayload {
    private String clipName;
    private AlertWindow scheduleWindow;
    private DateTime scheduleWindowStart;

    protected CareVoicePayload() {
    }

    public CareVoicePayload(String clipName, String uniqueId, DateTime generationTime, DeliveryStrategy deliveryStrategy, Period validity, AlertWindow scheduleWindow, DateTime scheduleWindowStart) {
        super(uniqueId, generationTime, deliveryStrategy, validity);
        this.clipName = clipName;
        this.scheduleWindow = scheduleWindow;
        this.scheduleWindowStart = scheduleWindowStart;
    }

    public String getClipName() {
        return clipName;
    }

    public AlertWindow getScheduleWindow() {
        return scheduleWindow;
    }

    public DateTime getScheduleWindowStart() {
        return scheduleWindowStart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CareVoicePayload that = (CareVoicePayload) o;

        if (clipName != null ? !clipName.equals(that.clipName) : that.clipName != null) return false;
        if (scheduleWindow != that.scheduleWindow) return false;
        if (scheduleWindowStart != null ? !scheduleWindowStart.equals(that.scheduleWindowStart) : that.scheduleWindowStart != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clipName != null ? clipName.hashCode() : 0);
        result = 31 * result + (scheduleWindow != null ? scheduleWindow.hashCode() : 0);
        result = 31 * result + (scheduleWindowStart != null ? scheduleWindowStart.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CareVoicePayload{" +
                "clipName='" + clipName + '\'' +
                ", scheduleWindow=" + scheduleWindow +
                ", scheduleWindowStart=" + scheduleWindowStart +
                '}';
    }
}
