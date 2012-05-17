package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.apache.commons.lang.StringUtils.trimToEmpty;

public class SMSPayload implements Payload {
    private String uniqueId;
    private String text;
    private DateTime generationTime;
    private DeliveryStrategy deliveryStrategy;
    private MessageRecipientType messageRecipientType;

    protected SMSPayload() {
    }

    public static SMSPayload fromTemplate(String template, Map<String, String> runtimeValues, String uniqueId, DateTime generationTime, DeliveryStrategy deliveryStrategy, MessageRecipientType recipientType) {
        return SMSPayload.fromText(SMSPayload.fill(template, runtimeValues), uniqueId, generationTime, deliveryStrategy, recipientType);
    }

    public static SMSPayload fromText(String text, String uniqueId, DateTime generationTime, DeliveryStrategy deliveryStrategy, MessageRecipientType recipientType) {
        SMSPayload smsPayload = new SMSPayload();
        smsPayload.uniqueId = uniqueId;
        smsPayload.generationTime = generationTime;
        smsPayload.deliveryStrategy = deliveryStrategy;
        smsPayload.text = text;
        smsPayload.messageRecipientType = recipientType;
        return smsPayload;
    }

    public static String fill(String template, Map<String, String> runTimeValues) {
        String text = template;
        for (Map.Entry<String, String> runTimeValue : runTimeValues.entrySet()) {
            text = template.replace(runTimeValue.getKey(), trimToEmpty(runTimeValue.getValue()));
            template = text;
        }
        return text;
    }

    public String getText() {
        return text;
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

    public MessageRecipientType getMessageRecipientType() {
        return messageRecipientType;
    }

    @Override
    public String toString() {
        return "SMSPayload{" +
                "text='" + text + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SMSPayload smsPayload = (SMSPayload) o;

        if (deliveryStrategy != null ? !deliveryStrategy.equals(smsPayload.deliveryStrategy) : smsPayload.deliveryStrategy != null)
            return false;
        if (generationTime != null ? !generationTime.equals(smsPayload.generationTime) : smsPayload.generationTime != null)
            return false;
        if (messageRecipientType != smsPayload.messageRecipientType) return false;
        if (uniqueId != null ? !uniqueId.equals(smsPayload.uniqueId) : smsPayload.uniqueId != null) return false;
        if (text != null ? !text.equals(smsPayload.text) : smsPayload.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uniqueId != null ? uniqueId.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (generationTime != null ? generationTime.hashCode() : 0);
        result = 31 * result + (deliveryStrategy != null ? deliveryStrategy.hashCode() : 0);
        result = 31 * result + (messageRecipientType != null ? messageRecipientType.hashCode() : 0);
        return result;
    }
}
