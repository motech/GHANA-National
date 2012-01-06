package org.motechproject.ghana.national.web.form;

import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

public class MobileMidwifeEnrollmentForm {
    private String patientMotechId;
    private String staffMotechId;
    private String facilityMotechId;
    private Boolean consent;
    private ServiceType serviceType;
    private PhoneOwnership phoneOwnership;
    private String phoneNumber;
    private Medium medium;
    private DayOfWeek dayOfWeek;
    private Time timeOfDay;
    private Language language;
    private LearnedFrom learnedFrom;
    private ReasonToJoin reasonToJoin;
    private String messageStartWeek;

    public MobileMidwifeEnrollmentForm() {
    }

    public String getStaffMotechId() {
        return staffMotechId;
    }

    public Boolean getConsent() {
        return consent;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public PhoneOwnership getPhoneOwnership() {
        return phoneOwnership;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Medium getMedium() {
        return medium;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public Time getTimeOfDay() {
        return timeOfDay;
    }

    public Language getLanguage() {
        return language;
    }

    public LearnedFrom getLearnedFrom() {
        return learnedFrom;
    }

    public ReasonToJoin getReasonToJoin() {
        return reasonToJoin;
    }

    public String getMessageStartWeek() {
        return messageStartWeek;
    }

    public String getPatientMotechId() {
        return patientMotechId;
    }

    public void setPatientMotechId(String patientMotechId) {
        this.patientMotechId = patientMotechId;
    }

    public void setStaffMotechId(String staffMotechId) {
        this.staffMotechId = staffMotechId;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public void setPhoneOwnership(PhoneOwnership phoneOwnership) {
        this.phoneOwnership = phoneOwnership;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setTimeOfDay(Time timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setLearnedFrom(LearnedFrom learnedFrom) {
        this.learnedFrom = learnedFrom;
    }

    public void setReasonToJoin(ReasonToJoin reasonToJoin) {
        this.reasonToJoin = reasonToJoin;
    }

    public void setMessageStartWeek(String messageStartWeek) {
        this.messageStartWeek = messageStartWeek;
    }

    public String getFacilityMotechId() {
        return facilityMotechId;
    }

    public void setFacilityMotechId(String facilityMotechId) {
        this.facilityMotechId = facilityMotechId;
    }
}
