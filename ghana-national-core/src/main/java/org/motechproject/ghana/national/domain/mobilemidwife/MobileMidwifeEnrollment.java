package org.motechproject.ghana.national.domain.mobilemidwife;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.model.Time;

@TypeDiscriminator("doc.type === 'MobileMidwifeEnrollment'")
public class MobileMidwifeEnrollment extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "MobileMidwifeEnrollment";
    @JsonProperty
    private String patientId;
    @JsonProperty
    private String staffId;
    @JsonProperty
    private String facilityId;
    @JsonProperty
    private Boolean consent;
    @JsonProperty
    private ServiceType serviceType;
    @JsonProperty
    private PhoneOwnership phoneOwnership;
    @JsonProperty
    private String phoneNumber;
    @JsonProperty
    private Medium medium;
    @JsonProperty
    private DayOfWeek dayOfWeek;
    @JsonProperty
    private Time timeOfDay;
    @JsonProperty
    private Language language;
    @JsonProperty
    private LearnedFrom learnedFrom;
    @JsonProperty
    private ReasonToJoin reasonToJoin;
    @JsonProperty
    private String messageStartWeek;

    public MobileMidwifeEnrollment() {
    }

    public String getPatientId() {
        return patientId;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getFacilityId() {
        return facilityId;
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

    public MobileMidwifeEnrollment setPatientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public MobileMidwifeEnrollment setStaffId(String staffId) {
        this.staffId = staffId;
        return this;
    }

    public MobileMidwifeEnrollment setFacilityId(String facilityId) {
        this.facilityId = facilityId;
        return this;
    }

    public MobileMidwifeEnrollment setConsent(Boolean consent) {
        this.consent = consent;
        return this;
    }

    public MobileMidwifeEnrollment setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public MobileMidwifeEnrollment setPhoneOwnership(PhoneOwnership phoneOwnership) {
        this.phoneOwnership = phoneOwnership;
        return this;
    }

    public MobileMidwifeEnrollment setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public MobileMidwifeEnrollment setMedium(Medium medium) {
        this.medium = medium;
        return this;
    }

    public MobileMidwifeEnrollment setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }

    public MobileMidwifeEnrollment setTimeOfDay(Time timeOfDay) {
        this.timeOfDay = timeOfDay;
        return this;
    }

    public MobileMidwifeEnrollment setLanguage(Language language) {
        this.language = language;
        return this;
    }

    public MobileMidwifeEnrollment setLearnedFrom(LearnedFrom learnedFrom) {
        this.learnedFrom = learnedFrom;
        return this;
    }

    public MobileMidwifeEnrollment setReasonToJoin(ReasonToJoin reasonToJoin) {
        this.reasonToJoin = reasonToJoin;
        return this;
    }

    public MobileMidwifeEnrollment setMessageStartWeek(String messageStartWeek) {
        this.messageStartWeek = messageStartWeek;
        return this;
    }
}
