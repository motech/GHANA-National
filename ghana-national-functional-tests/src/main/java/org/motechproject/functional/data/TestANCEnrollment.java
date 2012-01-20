package org.motechproject.functional.data;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.RegistrationToday;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.joda.time.format.DateTimeFormat.forPattern;

public class TestANCEnrollment {
    private String staffId;
    private String facilityId;
    private String motechPatientId;
    private LocalDate registrationDate;
    private RegistrationToday registrationToday;
    private String serialNumber;
    private LocalDate estimatedDateOfDelivery;
    private Double height;
    private Integer gravida;
    private Integer parity;
    private Boolean addHistory;
    private Boolean deliveryDateConfirmed;
    private List<ANCCareHistory> careHistory;
    private String lastIPT;
    private String lastTT;
    private LocalDate lastIPTDate;
    private LocalDate lastTTDate;

    public TestANCEnrollment with(String motechId, String staffId, String facilityId) {
        final TestANCEnrollment enrollment = new TestANCEnrollment();

        enrollment.staffId = staffId;
        enrollment.facilityId = facilityId;
        enrollment.motechPatientId = motechId;
        enrollment.registrationDate = new LocalDate(2011, 2, 2);
        enrollment.registrationToday = RegistrationToday.TODAY;
        enrollment.serialNumber = "serialNumber";
        enrollment.estimatedDateOfDelivery = new LocalDate(2011, 2, 2);
        enrollment.height = 124.0;
        enrollment.gravida = 3;
        enrollment.parity = 4;
        enrollment.addHistory = true;
        enrollment.deliveryDateConfirmed = true;
        enrollment.careHistory = Arrays.asList(ANCCareHistory.values());
        enrollment.lastIPT = "1";
        enrollment.lastTT = "1";
        enrollment.lastIPTDate = new LocalDate(2011, 2, 2);
        enrollment.lastTTDate = new LocalDate(2011, 2, 2);
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
            put("height", height.toString());
            put("gravida", gravida.toString());
            put("parity", parity.toString());
            put("addHistory", booleanCodeForAddHistory(addHistory));
            put("deliveryDateConfirmed", booleanCodeForDateConfirmed(deliveryDateConfirmed));
            put("careHistory", "TT IPT");
            put("lastIPT", lastIPT);
            put("last", lastTT);
            put("lastIPTDate", lastIPTDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
            put("lastDate", lastTTDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
        }};
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
        return careHistory;
    }

    public TestANCEnrollment withCareHistory(List<ANCCareHistory> careHistory) {
        this.careHistory = careHistory;
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

    public Integer gravida() {
        return gravida;
    }

    public TestANCEnrollment withGravida(Integer gravida) {
        this.gravida = gravida;
        return this;

    }

    public Double height() {
        return height;
    }

    public TestANCEnrollment withHeight(Double height) {
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

    public Integer parity() {
        return parity;
    }

    public TestANCEnrollment withParity(Integer parity) {
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
}

