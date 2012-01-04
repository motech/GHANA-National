package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.MobileMidwifeServiceType;
import org.motechproject.ghana.national.domain.PhoneOwnership;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

public class MobileMidwifeForm extends FormBean {
    public static final String NUMERIC_OR_NOTAPPLICABLE_PATTERN = "([0-9]+(.[0-9]+)?|[nN][aA])";
    public static final String MOTECH_ID_PATTERN = "[0-9]{7}";

    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;
    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String facilityId;
    @Required
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String patientId;
    @Required
    private Boolean consent;
    private MobileMidwifeServiceType serviceType;
    private PhoneOwnership phoneOwnership;
    @RegEx(pattern = "0[0-9]{9}")
    private String phoneNumber;
    @RegEx(pattern = "([A-Z]+_VOICE|[A-Z]+_TEXT)")
    private String medium;
    private DayOfWeek dayOfWeek;
    private Time timeOfDay;
    @RegEx(pattern = "(en|kas|nan|fan)")
    private String language;
    @RegEx(pattern = "(GHS_NURSE|MOTECH_FIELD_AGENT|FRIEND|POSTERS_ADS|RADIO)")
    private String howLearned;
    @RegEx(pattern = "(CURRENTLY_PREGNANT|RECENTLY_DELIVERED|FAMILY_FRIEND_PREGNANT|FAMILY_FRIEND_DELIVERED|PLANNING_PREGNANCY_INFO|KNOW_MORE_PREGNANCY_CHILDBIRTH|WORK_WITH_WOMEN_NEWBORNS)")
    private String reason;
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


    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
