package org.motechproject.ghana.national.functional.data;

import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.motechproject.ghana.national.domain.mobilemidwife.*;

import java.util.HashMap;
import java.util.Map;

public class TestMobileMidwifeEnrollment {
    private String staffId;
    private String facilityId;
    private Boolean consent;
    private ServiceType serviceType;
    private PhoneOwnership phoneOwnership;
    private String mmRegPhone;
    private Medium medium;
    private Language language;
    private LearnedFrom learnedFrom;
    private ReasonToJoin reasonToJoin;
    private MessageStartWeek messageStartWeek;
    private String patientId;

    public static TestMobileMidwifeEnrollment with(String staffId, String facilityId){
        TestMobileMidwifeEnrollment testMobileMidwifeEnrollment = new TestMobileMidwifeEnrollment();
        testMobileMidwifeEnrollment.staffId = staffId;
        testMobileMidwifeEnrollment.facilityId = facilityId;
        testMobileMidwifeEnrollment.consent = Boolean.TRUE;
        testMobileMidwifeEnrollment.serviceType = ServiceType.PREGNANCY;
        testMobileMidwifeEnrollment.mmRegPhone = new DataGenerator().randomPhoneNumber();
        testMobileMidwifeEnrollment.phoneOwnership = PhoneOwnership.PERSONAL;
        testMobileMidwifeEnrollment.medium = Medium.SMS;
        testMobileMidwifeEnrollment.language = Language.EN;
        testMobileMidwifeEnrollment.learnedFrom = LearnedFrom.MOTECH_FIELD_AGENT;
        testMobileMidwifeEnrollment.reasonToJoin = ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH;
        testMobileMidwifeEnrollment.messageStartWeek = new MessageStartWeek("10", "Pregnancy-week 10", ServiceType.PREGNANCY);
        return testMobileMidwifeEnrollment;
    }

    public Map<String, String> forMobile(){
        return new HashMap<String, String>(){{
            put("staffId", staffId);
            put("facilityId", facilityId);
            put("patientId",patientId);
            put("consent", toBooleanString(consent));
            put("serviceType", serviceType.getValue());
            put("phoneOwnership", phoneOwnership.getValue());
            put("mmRegPhone", mmRegPhone);
            String mediumForMobile = forMobile(medium, phoneOwnership);
            if(mediumForMobile != null){
                put("format", mediumForMobile);
            }
            put("language", language.getValue());
            put("learnedFrom", learnedFrom.getValue());
            put("reasonToJoin", reasonToJoin.getValue());
            put("messageStartWeek", messageStartWeek.getValue());
        }};
    }

    private String forMobile(Medium medium, PhoneOwnership phoneOwnership) {
        if(medium.equals(Medium.VOICE) && phoneOwnership.equals(PhoneOwnership.PERSONAL)){
            return "PERS_VOICE";
        }else if (medium.equals(Medium.SMS) && phoneOwnership.equals(PhoneOwnership.PERSONAL)){
            return "PERS_TEXT";
        }else if(medium.equals(Medium.VOICE) && phoneOwnership.equals(PhoneOwnership.HOUSEHOLD)){
            return "HSE_VOICE";
        }else if (medium.equals(Medium.SMS) && phoneOwnership.equals(PhoneOwnership.HOUSEHOLD)){
            return "HSE_TEXT";
        }else if (medium.equals(Medium.VOICE) && phoneOwnership.equals(PhoneOwnership.PUBLIC)){
            return "PUB_VOICE";
        }
        return null;
    }

    private String toBooleanString(Boolean bool) {
        return bool ? "Y" : "N";
    }


    public Map<? extends String, ? extends String> forClientRegistrationThroughMobile(TestPatient patient) {
        Map<String, String> enrollmentDetails = forMobile();
        enrollmentDetails.put("staffId", patient.staffId());
        enrollmentDetails.put("facilityId", patient.facilityId());
        enrollmentDetails.put("motechId", patient.motechId());
        enrollmentDetails.put("enroll", "Y");
        return enrollmentDetails;
    }

    public String staffId() {
        return staffId;
    }

    public String facilityId() {
        return facilityId;
    }

    public Boolean consent() {
        return consent;
    }

    public String serviceType() {
        return serviceType.getValue();
    }

    public String phoneOwnership() {
        return phoneOwnership.getValue();
    }

    public String phoneNumber() {
        return mmRegPhone;
    }

    public String medium() {
        return medium.getValue();
    }

    public String language() {
        return language.getValue();
    }

    public String learnedFrom() {
        return learnedFrom.getValue();
    }

    public String reasonToJoin() {
        return reasonToJoin.getValue();
    }

    public String messageStartWeek() {
        return messageStartWeek.getValue();
    }

    public TestMobileMidwifeEnrollment withServiceType(ServiceType serviceType){
        this.serviceType = serviceType;
        return this;
    }

    public TestMobileMidwifeEnrollment withMessageStartWeek(MessageStartWeek messageStartWeek){
        this.messageStartWeek = messageStartWeek;
        return this;
    }

    public TestMobileMidwifeEnrollment staffId(String staffId) {
        this.staffId = staffId;
        return this;
    }

    public TestMobileMidwifeEnrollment facilityId(String facilityId) {
        this.facilityId = facilityId;
        return this;
    }

    public TestMobileMidwifeEnrollment consent(Boolean consent) {
        this.consent = consent;
        return this;
    }

    public TestMobileMidwifeEnrollment phoneOwnership(String phoneOwnership) {
        this.phoneOwnership = PhoneOwnership.valueOf(phoneOwnership);
        return this;
    }

    public TestMobileMidwifeEnrollment phoneNumber(String phoneNumber) {
        this.mmRegPhone = phoneNumber;
        return this;
    }

    public TestMobileMidwifeEnrollment medium(String medium) {
        this.medium = Medium.valueOf(medium);
        return this;
    }

    public TestMobileMidwifeEnrollment language(String language) {
        this.language = Language.valueOf(language);
        return this;
    }

    public TestMobileMidwifeEnrollment learnedFrom(String learnedFrom) {
        this.learnedFrom = LearnedFrom.valueOf(learnedFrom);
        return this;
    }

    public TestMobileMidwifeEnrollment reasonToJoin(String reasonToJoin) {
        this.reasonToJoin = ReasonToJoin.valueOf(reasonToJoin);
        return this;
    }

    public TestMobileMidwifeEnrollment serviceType(String serviceType) {
        this.serviceType = ServiceType.valueOf(serviceType);
        return this;
    }

    public TestMobileMidwifeEnrollment messageStartWeek(MessageStartWeek messageStartWeek) {
        this.messageStartWeek = messageStartWeek;
        return this;
    }

    public TestMobileMidwifeEnrollment patientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestMobileMidwifeEnrollment)) return false;

        TestMobileMidwifeEnrollment that = (TestMobileMidwifeEnrollment) o;

        if (consent != null ? !consent.equals(that.consent) : that.consent != null) return false;
        if (facilityId != null ? !facilityId.equals(that.facilityId) : that.facilityId != null) return false;
        if (language != that.language) return false;
        if (learnedFrom != that.learnedFrom) return false;
        if (medium != that.medium) return false;
        if (messageStartWeek != null ? !messageStartWeek.equals(that.messageStartWeek) : that.messageStartWeek != null)
            return false;
        if (mmRegPhone != null ? !mmRegPhone.equals(that.mmRegPhone) : that.mmRegPhone != null) return false;
        if (patientId != null ? !patientId.equals(that.patientId) : that.patientId != null) return false;
        if (phoneOwnership != that.phoneOwnership) return false;
        if (reasonToJoin != that.reasonToJoin) return false;
        if (serviceType != that.serviceType) return false;
        if (staffId != null ? !staffId.equals(that.staffId) : that.staffId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = staffId != null ? staffId.hashCode() : 0;
        result = 31 * result + (facilityId != null ? facilityId.hashCode() : 0);
        result = 31 * result + (consent != null ? consent.hashCode() : 0);
        result = 31 * result + (serviceType != null ? serviceType.hashCode() : 0);
        result = 31 * result + (phoneOwnership != null ? phoneOwnership.hashCode() : 0);
        result = 31 * result + (mmRegPhone != null ? mmRegPhone.hashCode() : 0);
        result = 31 * result + (medium != null ? medium.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (learnedFrom != null ? learnedFrom.hashCode() : 0);
        result = 31 * result + (reasonToJoin != null ? reasonToJoin.hashCode() : 0);
        result = 31 * result + (messageStartWeek != null ? messageStartWeek.hashCode() : 0);
        result = 31 * result + (patientId != null ? patientId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestMobileMidwifeEnrollment{" +
                "staffId='" + staffId + '\'' +
                ", facilityId='" + facilityId + '\'' +
                ", consent=" + consent +
                ", serviceType=" + serviceType +
                ", phoneOwnership=" + phoneOwnership +
                ", mmRegPhone='" + mmRegPhone + '\'' +
                ", medium=" + medium +
                ", language=" + language +
                ", learnedFrom=" + learnedFrom +
                ", reasonToJoin=" + reasonToJoin +
                ", messageStartWeek=" + messageStartWeek +
                ", patientId='" + patientId + '\'' +
                '}';
    }
}
