package org.motechproject.ghana.national.web.form;

import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

public class MobileMidwifeEnrollmentForm {
    private String patientMotechId;
    private String staffMotechId;
    private FacilityForm facilityForm;
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

    public FacilityForm getFacilityForm() {
        return facilityForm;
    }

    public MobileMidwifeEnrollmentForm setFacilityForm(FacilityForm facilityForm) {
        this.facilityForm = facilityForm;
        return this;
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

    public MobileMidwifeEnrollmentForm setPatientMotechId(String patientMotechId) {
        this.patientMotechId = patientMotechId;
        return this;
    }

    public MobileMidwifeEnrollmentForm setStaffMotechId(String staffMotechId) {
        this.staffMotechId = staffMotechId;
        return this;
    }

    public MobileMidwifeEnrollmentForm setConsent(Boolean consent) {
        this.consent = consent;
        return this;
    }

    public MobileMidwifeEnrollmentForm setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public MobileMidwifeEnrollmentForm setPhoneOwnership(PhoneOwnership phoneOwnership) {
        this.phoneOwnership = phoneOwnership;
        return this;
    }

    public MobileMidwifeEnrollmentForm setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public MobileMidwifeEnrollmentForm setMedium(Medium medium) {
        this.medium = medium;
        return this;
    }

    public MobileMidwifeEnrollmentForm setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public MobileMidwifeEnrollmentForm setTimeOfDay(Time timeOfDay) {
        this.timeOfDay = timeOfDay;
        return this;
    }

    public MobileMidwifeEnrollmentForm setLanguage(Language language) {
        this.language = language;
        return this;
    }

    public MobileMidwifeEnrollmentForm setLearnedFrom(LearnedFrom learnedFrom) {
        this.learnedFrom = learnedFrom;
        return this;
    }

    public MobileMidwifeEnrollmentForm setReasonToJoin(ReasonToJoin reasonToJoin) {
        this.reasonToJoin = reasonToJoin;
        return this;
    }

    public MobileMidwifeEnrollmentForm setMessageStartWeek(String messageStartWeek) {
        this.messageStartWeek = messageStartWeek;
        return this;
    }

}
