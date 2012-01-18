package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

public abstract class MobileMidWifeIncludeForm<T> extends FormBean {

    @Required
    private Boolean enroll;

    private Boolean consent;

    private ServiceType serviceType;
    private PhoneOwnership phoneOwnership;
    @RegEx(pattern = "0[0-9]{9}")
    private String phoneNumber;
    @RegEx(pattern = "([A-Z]+_VOICE|[A-Z]+_TEXT)")
    private String medium;
    private DayOfWeek dayOfWeek;
    private Time timeOfDay;
    private Language language;
    private LearnedFrom learnedFrom;
    @RegEx(pattern = "(CURRENTLY_PREGNANT|RECENTLY_DELIVERED|FAMILY_FRIEND_PREGNANT|FAMILY_FRIEND_DELIVERED|PLANNING_PREGNANCY_INFO|KNOW_MORE_PREGNANCY_CHILDBIRTH|WORK_WITH_WOMEN_NEWBORNS)")
    private ReasonToJoin reasonToJoin;
    @RegEx(pattern = "([5-9]{1}|[1-8]{1}[0-9]{1}|9[0-2]{1})")
    private String messageStartWeek;

    public Boolean getConsent() {
        return consent;
    }

    public Medium getMediumStripingOwnership() {
        return medium != null ? Medium.valueOf(medium.substring(medium.indexOf("_") + 1)) : null;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public MobileMidWifeIncludeForm setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public PhoneOwnership getPhoneOwnership() {
        return phoneOwnership;
    }

    public MobileMidWifeIncludeForm setPhoneOwnership(PhoneOwnership phoneOwnership) {
        this.phoneOwnership = phoneOwnership;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public MobileMidWifeIncludeForm setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getMedium() {
        return medium;
    }

    public MobileMidWifeIncludeForm setMedium(String medium) {
        this.medium = medium;
        return this;
    }

    public MobileMidWifeIncludeForm setConsent(Boolean consent) {
        this.consent = consent;
        return this;
    }

    public Time getTimeOfDay() {
        return timeOfDay;
    }

    public MobileMidWifeIncludeForm setTimeOfDay(Time timeOfDay) {
        this.timeOfDay = timeOfDay;
        return this;
    }

    public String getMessageStartWeek() {
        return messageStartWeek;
    }

    public MobileMidWifeIncludeForm setMessageStartWeek(String messageStartWeek) {
        this.messageStartWeek = messageStartWeek;
        return this;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public MobileMidWifeIncludeForm setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public LearnedFrom getLearnedFrom() {
        return learnedFrom;
    }

    public MobileMidWifeIncludeForm setLearnedFrom(LearnedFrom learnedFrom) {
        this.learnedFrom = learnedFrom;
        return this;
    }

    public ReasonToJoin getReasonToJoin() {
        return reasonToJoin;
    }

    public MobileMidWifeIncludeForm setReasonToJoin(ReasonToJoin reasonToJoin) {
        this.reasonToJoin = reasonToJoin;
        return this;
    }

    public MobileMidWifeIncludeForm setLanguage(Language language) {
        this.language = language;
        return this;
    }

    public Boolean isEnrolledForProgram() {
        return enroll;
    }

    public MobileMidWifeIncludeForm setEnroll(Boolean enrollForProgram) {
        this.enroll = enrollForProgram;
        return this;
    }

    public MobileMidwifeEnrollment fillEnrollment(MobileMidwifeEnrollment enrollment) {
        return enrollment.setConsent(getConsent()).setServiceType(getServiceType()).setPhoneOwnership(getPhoneOwnership())
                .setPhoneNumber(getPhoneNumber()).setMedium(Medium.value(getMedium())).setDayOfWeek(getDayOfWeek())
                .setTimeOfDay(getTimeOfDay()).setLanguage(getLanguage()).setLearnedFrom(getLearnedFrom())
                .setReasonToJoin(getReasonToJoin()).setMessageStartWeek(getMessageStartWeek());
    }
}
