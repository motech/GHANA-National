package org.motechproject.ghana.national.functional.data;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.motechproject.ghana.national.functional.util.MobileFormUtils.updateFieldByNameAndValue;
import static org.motechproject.ghana.national.functional.util.MobileFormUtils.updateFieldName;

public class TestANCEnrollment implements CareEnrollment {
    private String staffId;
    private String facilityId;
    private String motechPatientId;
    private LocalDate registrationDate;
    private RegistrationToday registrationToday;
    private String serialNumber;
    private LocalDate estimatedDateOfDelivery;
    private Boolean addHistory;
    private List<ANCCareHistory> addCareHistory;
    private String height;
    private String gravida;
    private String parity;
    private Boolean deliveryDateConfirmed;
    private String lastIPT;
    private String lastTT;
    private LocalDate lastIPTDate;
    private LocalDate lastTTDate;
    private String region;
    private String district;
    private String subDistrict;
    private String facility;
    private String country;

    public static TestANCEnrollment create() {
        final TestANCEnrollment enrollment = new TestANCEnrollment();

        enrollment.registrationDate = DateUtil.today();
        enrollment.registrationToday = RegistrationToday.TODAY;
        enrollment.serialNumber = "serialNumber";
        enrollment.estimatedDateOfDelivery = new LocalDate(2012, 2, 3);
        enrollment.addHistory = true;
        enrollment.addCareHistory = Arrays.asList(ANCCareHistory.values());
        enrollment.height = "124.0";
        enrollment.gravida = "3";
        enrollment.parity = "4";
        enrollment.deliveryDateConfirmed = true;
        enrollment.lastIPT = "1";
        enrollment.lastTT = "1";
        enrollment.lastIPTDate = new LocalDate(2011, 2, 3);
        enrollment.lastTTDate = new LocalDate(2011, 2, 4);
        enrollment.country = "Ghana";
        enrollment.region = "Central Region";
        enrollment.district = "Awutu Senya";
        enrollment.subDistrict = "Kasoa";
        enrollment.facility = "Papaase CHPS";
        enrollment.facilityId = "13212";
        return enrollment;
    }

    public Map<String, String> forMobile() {
        return new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", facilityId);
            put("motechId", motechPatientId);
            put("registrationDate", registrationDate.toString(forPattern("yyyy-MM-dd")));
            put("regDateToday", registrationToday.name());
            put("ancRegNumber", serialNumber);
            put("estDeliveryDate", estimatedDateOfDelivery.toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
            put("height", height);
            put("gravida", gravida);
            put("parity", parity);
            put("addHistory", booleanCodeForAddHistory(addHistory));
            put("deliveryDateConfirmed", booleanCodeForDateConfirmed(deliveryDateConfirmed));
            put("addCareHistory", "IPT,TT");
            put("lastIPT", lastIPT);
            put("lastTT", lastTT);
            put("lastIPTDate", lastIPTDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
            put("lastTTDate", lastTTDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
            put("serialNumber", serialNumber);
        }};
    }

    public Map<String, String> withMobileMidwifeEnrollmentThroughMobile(TestMobileMidwifeEnrollment mmEnrollmentDetails) {
        Map<String, String> enrollmentDetails = mmEnrollmentDetails.forMobile();
        enrollmentDetails.putAll(forMobile());
        enrollmentDetails.put("enroll", "Y");
        return enrollmentDetails;
    }

    @Override
    public Map<String, String> forClientRegistrationThroughMobile(TestPatient patient) {
        Map<String, String> enrollmentDetails = forMobile();
        updateFieldName(enrollmentDetails, "addCareHistory", "addMotherHistory");
        updateFieldName(enrollmentDetails, "serialNumber", "ancRegNumber");
        updateFieldName(enrollmentDetails, "estDeliveryDate", "expDeliveryDate");
        updateFieldByNameAndValue(enrollmentDetails, "registrationDate", "date", patient.getRegistrationDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
        enrollmentDetails.put("staffId", patient.staffId());
        enrollmentDetails.put("facilityId", patient.facilityId());
        enrollmentDetails.put("motechId", patient.motechId());
        return enrollmentDetails;
    }

    public String booleanCodeForAddHistory(boolean value) {
        return value ? "1" : "0";
    }

    public String booleanCodeForDateConfirmed(boolean value) {
        return value ? "Y" : "N";
    }

    public Boolean addHistory() {
        return addHistory;
    }

    public TestANCEnrollment withAddHistory(Boolean addHistory) {
        this.addHistory = addHistory;
        return this;
    }

    public List<ANCCareHistory> careHistory() {
        return addCareHistory;
    }

    public TestANCEnrollment withCareHistory(List<ANCCareHistory> careHistory) {
        this.addCareHistory = careHistory;
        return this;

    }

    public Boolean deliveryDateConfirmed() {
        return deliveryDateConfirmed;
    }

    public TestANCEnrollment withDeliveryDateConfirmed(Boolean deliveryDateConfirmed) {
        this.deliveryDateConfirmed = deliveryDateConfirmed;
        return this;

    }

    public LocalDate estimatedDateOfDelivery() {
        return estimatedDateOfDelivery;
    }

    public TestANCEnrollment withEstimatedDateOfDelivery(LocalDate estimatedDateOfDelivery) {
        this.estimatedDateOfDelivery = estimatedDateOfDelivery;
        return this;

    }

    public String facilityId() {
        return facilityId;
    }

    public TestANCEnrollment withFacilityId(String facilityId) {
        this.facilityId = facilityId;
        return this;

    }

    public String gravida() {
        return String.valueOf(gravida);
    }

    public TestANCEnrollment withGravida(String gravida) {
        this.gravida = gravida;
        return this;

    }

    public String height() {
        return height;
    }

    public TestANCEnrollment withHeight(String height) {
        this.height = height;
        return this;

    }

    public String lastTT() {
        return lastTT;
    }

    public TestANCEnrollment withLastTT(String last) {
        this.lastTT = last;
        return this;

    }

    public LocalDate lastTTDate() {
        return lastTTDate;
    }

    public TestANCEnrollment withLastTTDate(LocalDate lastDate) {
        this.lastTTDate = lastDate;
        return this;

    }

    public String lastIPT() {
        return lastIPT;
    }

    public TestANCEnrollment withLastIPT(String lastIPT) {
        this.lastIPT = lastIPT;
        return this;

    }

    public LocalDate lastIPTDate() {
        return lastIPTDate;
    }

    public TestANCEnrollment withLastIPTDate(LocalDate lastIPTDate) {
        this.lastIPTDate = lastIPTDate;
        return this;

    }

    public String motechPatientId() {
        return motechPatientId;
    }

    public TestANCEnrollment withMotechPatientId(String motechPatientId) {
        this.motechPatientId = motechPatientId;
        return this;

    }

    public String parity() {
        return String.valueOf(parity);
    }

    public TestANCEnrollment withParity(String parity) {
        this.parity = parity;
        return this;

    }

    public LocalDate registrationDate() {
        return registrationDate;
    }

    public TestANCEnrollment withRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
        return this;

    }

    public RegistrationToday registrationToday() {
        return registrationToday;
    }

    public TestANCEnrollment withRegistrationToday(RegistrationToday registrationToday) {
        this.registrationToday = registrationToday;
        return this;

    }

    public String serialNumber() {
        return serialNumber;
    }

    public TestANCEnrollment withSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public String staffId() {
        return staffId;
    }

    public TestANCEnrollment withStaffId(String staffId) {
        this.staffId = staffId;
        return this;
    }

    public String region() {
        return region;
    }

    public TestANCEnrollment withRegion(String region) {
        this.region = region;
        return this;
    }

    public String district() {
        return district;
    }

    public TestANCEnrollment withDistrict(String district) {
        this.district = district;
        return this;
    }

    public String subDistrict() {
        return subDistrict;
    }

    public TestANCEnrollment withSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
        return this;
    }

    public String facility() {
        return facility;
    }

    public TestANCEnrollment withFacility(String facility) {
        this.facility = facility;
        return this;
    }

    public Boolean hasIPTHistory() {
        return addCareHistory.contains(ANCCareHistory.IPT);
    }

    public Boolean hasTTHistory() {
        return addCareHistory.contains(ANCCareHistory.TT);
    }

    public String country() {
        return country;
    }
}

