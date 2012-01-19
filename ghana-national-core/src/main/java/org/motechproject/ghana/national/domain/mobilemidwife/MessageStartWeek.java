package org.motechproject.ghana.national.domain.mobilemidwife;

import ca.uhn.hl7v2.model.v25.datatype.ST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MessageStartWeek {
    private String value;
    private String displayText;
    private ServiceType serviceType;

    public MessageStartWeek(String value, String displayText) {
        this.value = value;
        this.displayText = displayText;
    }

    public MessageStartWeek(String value, String displayText, ServiceType serviceType) {
        this.value = value;
        this.displayText = displayText;
        this.serviceType = serviceType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public static MessageStartWeek valueOf(String value){
        int messageStartWeek = Integer.parseInt(value);
        if(messageStartWeek >=5 && messageStartWeek <=40){
            return new MessageStartWeek(value, "Pregnancy-week " + value, ServiceType.PREGNANCY);
        }else if(messageStartWeek >= 41 && messageStartWeek <= 92){
            return new MessageStartWeek(value, "Baby-week " + value, ServiceType.CHILD_CARE);
        }else {
            return null;
        }
    }

    public static List<MessageStartWeek> messageStartWeeks(){
        ArrayList<MessageStartWeek> messageStartWeeks = new ArrayList<MessageStartWeek>();
        for (int i = 5; i < 41; i++) {
            messageStartWeeks.add(new MessageStartWeek(String.valueOf(i), "Pregnancy-week " + i, ServiceType.PREGNANCY));
        }
        for (int i = 1; i < 53; i++) {
            messageStartWeeks.add(new MessageStartWeek(String.valueOf(40 + i), "Baby-week " + i, ServiceType.CHILD_CARE));
        }
        return messageStartWeeks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageStartWeek)) return false;

        MessageStartWeek that = (MessageStartWeek) o;

        if (displayText != null ? !displayText.equals(that.displayText) : that.displayText != null) return false;
        if (serviceType != that.serviceType) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (displayText != null ? displayText.hashCode() : 0);
        result = 31 * result + (serviceType != null ? serviceType.hashCode() : 0);
        return result;
    }
}
