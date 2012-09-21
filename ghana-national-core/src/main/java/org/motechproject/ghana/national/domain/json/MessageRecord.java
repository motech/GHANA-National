package org.motechproject.ghana.national.domain.json;

import java.util.List;

public class MessageRecord {

    private String name;
    private List<String> formats;
    private List<String> languages;
    private String messageKey;
    private String calendarStartOfWeek;
    private List<String> repeatOn;
    private String deliverTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getCalendarStartOfWeek() {
        return calendarStartOfWeek;
    }

    public void setCalendarStartOfWeek(String calendarStartOfWeek) {
        this.calendarStartOfWeek = calendarStartOfWeek;
    }

    public List<String> getRepeatOn() {
        return repeatOn;
    }

    public void setRepeatOn(List<String> weekDaysApplicable) {
        this.repeatOn = weekDaysApplicable;
    }

    public String getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(String deliverTime) {
        this.deliverTime = deliverTime;
    }

    @Override
    public String toString() {
        return "MessageRecord{" +
                "name='" + name + '\'' +
                ", formats=" + formats +
                ", languages=" + languages +
                ", messageKey='" + messageKey + '\'' +
                ", calendarStartOfWeek='" + calendarStartOfWeek + '\'' +
                ", repeatOn=" + repeatOn +
                ", deliverTime='" + deliverTime + '\'' +
                '}';
    }
}
