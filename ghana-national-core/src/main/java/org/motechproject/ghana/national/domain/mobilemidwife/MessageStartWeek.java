package org.motechproject.ghana.national.domain.mobilemidwife;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MessageStartWeek {
    private String key;
    private int week;
    private String displayText;
    private ServiceType serviceType;

    private static LinkedHashMap<String, MessageStartWeek> allWeeks = new LinkedHashMap<String, MessageStartWeek>();

    public MessageStartWeek(String key, int week, String displayText, ServiceType serviceType) {
        this.key = key;
        this.week = week;
        this.displayText = displayText;
        this.serviceType = serviceType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public static MessageStartWeek findBy(String key) {
        return getAllMessageStartWeeks().get(key);
    }

    public Integer getWeek() {
        return week;
    }

    public static ArrayList<MessageStartWeek> messageStartWeeks() {
        return new ArrayList<MessageStartWeek>(getAllMessageStartWeeks().values());
    }
    
    private static LinkedHashMap<String, MessageStartWeek> getAllMessageStartWeeks() {
        if (allWeeks.isEmpty()) {
            for (int week = 5; week < 41; week++) {
                String key = String.valueOf(week);
                allWeeks.put(key, new MessageStartWeek(key, week, "Pregnancy-week " + week, ServiceType.PREGNANCY));
            }
            for (int week = 1; week < 53; week++) {
                String key = String.valueOf(40 + week);
                allWeeks.put(key, new MessageStartWeek(key, week, "Baby-week " + week, ServiceType.CHILD_CARE));
            }
        }
        return allWeeks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageStartWeek that = (MessageStartWeek) o;

        if (week != that.week) return false;
        if (!displayText.equals(that.displayText)) return false;
        if (!key.equals(that.key)) return false;
        if (serviceType != that.serviceType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + week;
        result = 31 * result + displayText.hashCode();
        result = 31 * result + serviceType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MessageStartWeek{" +
                "key='" + key + '\'' +
                ", week=" + week +
                ", displayText='" + displayText + '\'' +
                ", serviceType=" + serviceType +
                '}';
    }
}
