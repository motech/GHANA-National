package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.MobileMidwifeServiceType;
import org.motechproject.ghana.national.domain.PhoneOwnership;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.model.Time;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import java.util.Date;

public class MobileMidwifeForm extends FormBean {
    public static final String NUMERIC_OR_NOTAPPLICABLE_PATTERN = "([0-9]+(.[0-9]+)?|[nN][aA])";
    public static final String MOTECH_ID_PATTERN = "[0-9]{7}";

    @Required
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;
    @Required
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    @MotechId(validator = VerhoeffValidator.class)
    private String facilityId;
    @Required
    @RegEx(pattern = MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;
    @Required
    private Boolean consent;
    @Required
    private MobileMidwifeServiceType serviceType;
    @Required
    private PhoneOwnership phoneOwnership;
    @RegEx(pattern = "0[0-9]{9}")
    private String phoneNumber;
    @Required
    @RegEx(pattern = "([A-Z]+_VOICE|[A-Z]+_TEXT)")
    private String medium;
    @Required
    private Date dayOfWeek;
    @Required
    private Time timeOfDay;
    @Required
    @RegEx(pattern = "(en|kas|nan|fan)")
    private String language;
    @Required
    @RegEx(pattern = "(GHS_NURSE|MOTECH_FIELD_AGENT|FRIEND|POSTERS_ADS|RADIO)")
    private String howLearned;
    @Required
    @RegEx(pattern = "(CURRENTLY_PREGNANT|RECENTLY_DELIVERED|FAMILY_FRIEND_PREGNANT|FAMILY_FRIEND_DELIVERED|PLANNING_PREGNANCY_INFO|KNOW_MORE_PREGNANCY_CHILDBIRTH|WORK_WITH_WOMEN_NEWBORNS)")
    private String reason;
    @Required
    @RegEx(pattern = "([5-9]{1}|[1-8]{1}[0-9]{1}|9[0-2]{1})")
    private String messageStartWeek;

    public String getMediumStripingOwnership(){
        return medium != null? medium.substring(medium.indexOf("_") + 1): medium;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public Boolean getConsent() {
        return consent;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
    }

    public MobileMidwifeServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(MobileMidwifeServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public PhoneOwnership getPhoneOwnership() {
        return phoneOwnership;
    }

    public void setPhoneOwnership(PhoneOwnership phoneOwnership) {
        this.phoneOwnership = phoneOwnership;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public Date getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Date dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Time getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(Time timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHowLearned() {
        return howLearned;
    }

    public void setHowLearned(String howLearned) {
        this.howLearned = howLearned;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessageStartWeek() {
        return messageStartWeek;
    }

    public void setMessageStartWeek(String messageStartWeek) {
        this.messageStartWeek = messageStartWeek;
    }


}
