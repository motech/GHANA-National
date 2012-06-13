package org.motechproject.ghana.national.domain.mobilemidwife;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;

import java.util.Arrays;

import static org.motechproject.util.DateUtil.setTimeZone;

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
    @JsonProperty
    private Boolean active;
    @JsonProperty
    private DateTime enrollmentDateTime;

    private MobileMidwifeEnrollment() {
    }

    public MobileMidwifeEnrollment(DateTime enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public DateTime getEnrollmentDateTime() {
        return enrollmentDateTime == null ? null : setTimeZone(enrollmentDateTime);
    }

    public void setEnrollmentDateTime(DateTime enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
    }

    public static MobileMidwifeEnrollment cloneNew(MobileMidwifeEnrollment midwifeEnrollment) {
        final MobileMidwifeEnrollment newEnrollment = new MobileMidwifeEnrollment(midwifeEnrollment.getEnrollmentDateTime());
        newEnrollment.setMessageStartWeek(midwifeEnrollment.getMessageStartWeek());
        newEnrollment.setDayOfWeek(midwifeEnrollment.getDayOfWeek());
        newEnrollment.setLearnedFrom(midwifeEnrollment.getLearnedFrom());
        newEnrollment.setReasonToJoin(midwifeEnrollment.getReasonToJoin());
        newEnrollment.setServiceType(midwifeEnrollment.getServiceType());
        newEnrollment.setConsent(midwifeEnrollment.getConsent());
        newEnrollment.setFacilityId(midwifeEnrollment.getFacilityId());
        newEnrollment.setStaffId(midwifeEnrollment.getStaffId());
        newEnrollment.setLanguage(midwifeEnrollment.getLanguage());
        newEnrollment.setMedium(midwifeEnrollment.getMedium());
        newEnrollment.setPatientId(midwifeEnrollment.getPatientId());
        newEnrollment.setPhoneNumber(midwifeEnrollment.getPhoneNumber());
        newEnrollment.setPhoneOwnership(midwifeEnrollment.getPhoneOwnership());
        newEnrollment.setTimeOfDay(midwifeEnrollment.getTimeOfDay());
        return newEnrollment;
    }

    public boolean campaignApplicable() {
        return getConsent() && !PhoneOwnership.PUBLIC.equals(getPhoneOwnership());
    }

    public CampaignRequest createCampaignRequestForTextMessage(LocalDate scheduleStartDate) {
        return new CampaignRequest(patientId, serviceType.name() + "_" + medium.name(), null, scheduleStartDate, MessageStartWeek.findBy(messageStartWeek).getWeek());
    }

    public CampaignRequest stopCampaignRequest() {
        return new CampaignRequest(patientId, serviceType.name() + "_" + medium.name(), null, null);
    }

    public CampaignRequest createCampaignRequestForVoiceMessage(LocalDate nextApplicableDay, DayOfWeek dayOfWeek, Time timeOfDay) {
        CampaignRequest campaignRequest = new CampaignRequest(patientId, serviceType.name() + "_" + medium.name(), nextApplicableDay, timeOfDay, Arrays.asList(dayOfWeek));
        campaignRequest.setStartOffset(MessageStartWeek.findBy(messageStartWeek).getWeek());
        return campaignRequest;
    }
}
