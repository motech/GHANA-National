package org.motechproject.ghana.national.web.form;

import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;

import java.lang.String;

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

    public MobileMidwifeEnrollmentForm(MobileMidwifeEnrollment enrollment) {
        if (enrollment != null) {
            this.setPatientMotechId(enrollment.getPatientId()).setStaffMotechId(enrollment.getStaffId()).setFacilityMotechId(enrollment.getFacilityId())
                    .setConsent(enrollment.getConsent()).setServiceType(enrollment.getServiceType()).setPhoneOwnership(enrollment.getPhoneOwnership())
                    .setPhoneNumber(enrollment.getPhoneNumber()).setMedium(enrollment.getMedium()).setDayOfWeek(enrollment.getDayOfWeek())
                    .setTimeOfDay(enrollment.getTimeOfDay()).setLanguage(enrollment.getLanguage()).setLearnedFrom(enrollment.getLearnedFrom())
                    .setReasonToJoin(enrollment.getReasonToJoin()).setMessageStartWeek(enrollment.getMessageStartWeek());
        }
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

    public String getFacilityMotechId() {
        return facilityMotechId;
    }

    public MobileMidwifeEnrollmentForm setFacilityMotechId(String facilityMotechId) {
        this.facilityMotechId = facilityMotechId;
        return this;
    }

    public MobileMidwifeEnrollment createEnrollment() {
        MobileMidwifeEnrollment enrollment = new MobileMidwifeEnrollment();
        enrollment.setStaffId(getStaffMotechId()).setPatientId(getPatientMotechId()).setFacilityId(getFacilityMotechId())
                .setConsent(getConsent()).setDayOfWeek(getDayOfWeek()).setLearnedFrom(getLearnedFrom())
                .setLanguage(getLanguage()).setMedium(getMedium()).setMessageStartWeek(getMessageStartWeek()).setPhoneNumber(getPhoneNumber())
                .setPhoneOwnership(getPhoneOwnership()).setReasonToJoin(getReasonToJoin()).setServiceType(getServiceType())
                .setTimeOfDay(getTimeOfDay());
        return enrollment;
    }
}
