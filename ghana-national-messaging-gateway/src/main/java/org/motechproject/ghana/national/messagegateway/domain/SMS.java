package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;

import java.util.Map;

import static org.apache.commons.lang.StringUtils.trimToEmpty;

public class SMS implements Payload {
    private String phoneNumber;
    private String text;
    private DateTime generationTime;
    private DeliveryStrategy deliveryStrategy;
    private MessageRecipientType messageRecipientType;

    protected SMS() {
    }

    public static SMS fromTemplate(String template, Map<String, String> runtimeValues, String phoneNumber, DateTime generationTime, DeliveryStrategy deliveryStrategy, MessageRecipientType recipientType) {
        return SMS.fromText(SMS.fill(template, runtimeValues), phoneNumber, generationTime, deliveryStrategy, recipientType);
    }

    public static SMS fromText(String text, String phoneNumber, DateTime generationTime, DeliveryStrategy deliveryStrategy, MessageRecipientType recipientType) {
        SMS sms = new SMS();
        sms.phoneNumber = phoneNumber;
        sms.generationTime = generationTime;
        sms.deliveryStrategy = deliveryStrategy;
        sms.text = text;
        sms.messageRecipientType = recipientType;
        return sms;
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

    public String getPhoneNumber() {
        return phoneNumber;
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
        return "SMS{" +
                "text='" + text + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SMS sms = (SMS) o;

        if (deliveryStrategy != null ? !deliveryStrategy.equals(sms.deliveryStrategy) : sms.deliveryStrategy != null)
            return false;
        if (generationTime != null ? !generationTime.equals(sms.generationTime) : sms.generationTime != null)
            return false;
        if (messageRecipientType != sms.messageRecipientType) return false;
        if (phoneNumber != null ? !phoneNumber.equals(sms.phoneNumber) : sms.phoneNumber != null) return false;
        if (text != null ? !text.equals(sms.text) : sms.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = phoneNumber != null ? phoneNumber.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (generationTime != null ? generationTime.hashCode() : 0);
        result = 31 * result + (deliveryStrategy != null ? deliveryStrategy.hashCode() : 0);
        result = 31 * result + (messageRecipientType != null ? messageRecipientType.hashCode() : 0);
        return result;
    }
}
