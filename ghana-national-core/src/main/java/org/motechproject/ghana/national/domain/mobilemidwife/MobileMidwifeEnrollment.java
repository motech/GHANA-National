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
    private LearnedFrom howLearned;
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

    public LearnedFrom getHowLearned() {
        return howLearned;
    }

    public ReasonToJoin getReasonToJoin() {
        return reasonToJoin;
    }

    public String getMessageStartWeek() {
        return messageStartWeek;
    }
}
