package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.apache.commons.lang.StringUtils.trimToEmpty;

public class VoicePayload implements Payload {
    private String uniqueId;
    private String clipName;
    private DateTime generationTime;
    private DeliveryStrategy deliveryStrategy;
    private DateTime expirationTime;

    protected VoicePayload() {
    }

    public VoicePayload(String clipName, String uniqueId, DateTime generationTime, DeliveryStrategy deliveryStrategy, DateTime expirationTime) {
        this.clipName = clipName;
        this.uniqueId = uniqueId;
        this.generationTime = generationTime;
        this.deliveryStrategy = deliveryStrategy;
        this.expirationTime = expirationTime;
    }

    public String getClipName() {
        return clipName;
    }

    public DeliveryStrategy getDeliveryStrategy() {
        return deliveryStrategy;
    }

    public DateTime getGenerationTime() {
        return generationTime;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public DateTime getDeliveryTime() {
        return deliveryStrategy.deliveryDate(this);
    }

    @Override
    public Boolean canBeDispatched() {
        return getDeliveryTime().toDate().before(DateUtil.now().toDate());
    }

    public DateTime getExpirationTime() {
        return expirationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoicePayload that = (VoicePayload) o;

        if (clipName != null ? !clipName.equals(that.clipName) : that.clipName != null) return false;
        if (deliveryStrategy != null ? !deliveryStrategy.equals(that.deliveryStrategy) : that.deliveryStrategy != null)
            return false;
        if (expirationTime != null ? !expirationTime.equals(that.expirationTime) : that.expirationTime != null)
            return false;
        if (generationTime != null ? !generationTime.equals(that.generationTime) : that.generationTime != null)
            return false;
        if (uniqueId != null ? !uniqueId.equals(that.uniqueId) : that.uniqueId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uniqueId != null ? uniqueId.hashCode() : 0;
        result = 31 * result + (clipName != null ? clipName.hashCode() : 0);
        result = 31 * result + (generationTime != null ? generationTime.hashCode() : 0);
        result = 31 * result + (deliveryStrategy != null ? deliveryStrategy.hashCode() : 0);
        result = 31 * result + (expirationTime != null ? expirationTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VoicePayload{" +
                "uniqueId='" + uniqueId + '\'' +
                ", clipName='" + clipName + '\'' +
                ", generationTime=" + generationTime +
                ", deliveryStrategy=" + deliveryStrategy +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
