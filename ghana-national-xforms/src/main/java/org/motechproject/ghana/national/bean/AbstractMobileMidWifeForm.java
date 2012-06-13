package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.*;

public abstract class AbstractMobileMidWifeForm extends FormBean {

    private String serviceType;
    private PhoneOwnership phoneOwnership;
    @RegEx(pattern = PHONE_NO_PATTERN)
    private String mmRegPhone;
    @RegEx(pattern = MM_MESSAGE_FORMAT)
    private String format;
    private DayOfWeek dayOfWeek;
    private Time timeOfDay;
    private Language language;
    private LearnedFrom learnedFrom;
    private ReasonToJoin reasonToJoin;
    @RegEx(pattern = MM_MESSAGE_START_WEEK)
    private String messageStartWeek;

    public Medium getMediumStripingOwnership() {
        return format != null ? Medium.get(format.substring(format.indexOf("_") + 1)) : null;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public PhoneOwnership getPhoneOwnership() {
        return phoneOwnership;
    }

    public void setPhoneOwnership(PhoneOwnership phoneOwnership) {
        this.phoneOwnership = phoneOwnership;
    }

    public String getMmRegPhone() {
        return mmRegPhone;
    }

    public void setMmRegPhone(String mmRegPhone) {
        this.mmRegPhone = mmRegPhone;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Time getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(Time timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getMessageStartWeek() {
        return messageStartWeek;
    }

    public void setMessageStartWeek(String messageStartWeek) {
        this.messageStartWeek = messageStartWeek;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Language getLanguage() {
        return language;
    }

    public LearnedFrom getLearnedFrom() {
        return learnedFrom;
    }

    public void setLearnedFrom(LearnedFrom learnedFrom) {
        this.learnedFrom = learnedFrom;
    }

    public ReasonToJoin getReasonToJoin() {
        return reasonToJoin;
    }

    public void setReasonToJoin(ReasonToJoin reasonToJoin) {
        this.reasonToJoin = reasonToJoin;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public MobileMidwifeEnrollment fillEnrollment(MobileMidwifeEnrollment enrollment) {
        return enrollment.setServiceType(ServiceType.getServiceType(getServiceType(), getMediumStripingOwnership()))
                .setPhoneOwnership(getPhoneOwnership()).setPhoneNumber(getMmRegPhone()).setMedium(getMediumStripingOwnership())
                .setDayOfWeek(getDayOfWeek()).setTimeOfDay(getTimeOfDay()).setLanguage(getLanguage()).setLearnedFrom(getLearnedFrom())
                .setReasonToJoin(getReasonToJoin()).setMessageStartWeek(getMessageStartWeek());
    }
}
