package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.LocalDateTime;
import org.motechproject.util.DateUtil;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

public class SMS implements DeliveryTimeAware, Serializable{
    private String phoneNumber;
    private String text;
    private LocalDateTime generationTime;
    private DeliveryStrategy deliveryStrategy;
    private Comparator<String> comparator;

    protected SMS() {
    }

    public static SMS fromTemplate(String template, Map<String, String> runtimeValues, String phoneNumber, LocalDateTime generationTime, DeliveryStrategy deliveryStrategy, Comparator<String> comparator){
        return SMS.fromText(SMS.fill(template, runtimeValues), phoneNumber, generationTime, deliveryStrategy, comparator);
    }

    public static SMS fromText(String text, String phoneNumber, LocalDateTime generationTime, DeliveryStrategy deliveryStrategy, Comparator<String> comparator){
        SMS sms = new SMS();
        sms.phoneNumber = phoneNumber;
        sms.generationTime = generationTime;
        sms.deliveryStrategy = deliveryStrategy;
        sms.text = text;
        sms.comparator = comparator;
        return sms;
    }

    public static String fill(String template, Map<String, String> runTimeValues) {
        String text="";
        for (Map.Entry<String, String> runTimeValue : runTimeValues.entrySet()) {
            text = template.replace(runTimeValue.getKey(), runTimeValue.getValue());
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

    public LocalDateTime getGenerationTime() {
        return generationTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryStrategy.deliveryDate(this);
    }

    @Override
    public Boolean canBeDispatched() {
        return getDeliveryTime().toDate().before(DateUtil.now().toDate());
    }

    public Comparator<String> getComparator() {
        return comparator;
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
        if (!(o instanceof SMS)) return false;

        SMS sms = (SMS) o;

        if (deliveryStrategy != null ? !deliveryStrategy.equals(sms.deliveryStrategy) : sms.deliveryStrategy != null)
            return false;
        if (generationTime != null ? !generationTime.equals(sms.generationTime) : sms.generationTime != null)
            return false;
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
        return result;
    }
}
