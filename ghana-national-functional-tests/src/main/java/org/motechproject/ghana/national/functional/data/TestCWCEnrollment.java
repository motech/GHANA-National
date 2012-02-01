package org.motechproject.ghana.national.functional.data;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.util.DateUtil;

import java.util.*;

import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.motechproject.ghana.national.functional.util.MobileFormUtils.updateFieldByNameAndValue;
import static org.motechproject.ghana.national.functional.util.MobileFormUtils.updateFieldName;

public class TestCWCEnrollment implements CareEnrollment {

    private String staffId;
    private String facilityId;
    private String motechPatientId;
    private LocalDate registrationDate;
    private String serialNumber;
    private Boolean addHistory;
    private List<CwcCareHistory> addCareHistory;
    private LocalDate bcgDate;
    private LocalDate lastOPVDate;
    private LocalDate lastVitaminADate;
    private LocalDate lastIPTiDate;
    private LocalDate yellowFeverDate;
    private LocalDate lastPentaDate;
    private LocalDate measlesDate;
    private Integer lastOPV;
    private Integer lastIPTi;
    private Integer lastPenta;
    private String country;
    private String region;
    private String district;
    private String subDistrict;
    private String facility;

    @Override
    public Map<String, String> forClientRegistrationThroughMobile(TestPatient patient) {
        Map<String, String> enrollmentDetails = forMobile();
        updateFieldName(enrollmentDetails, "addCareHistory", "addChildHistory");
        updateFieldName(enrollmentDetails, "serialNumber", "cwcRegNumber");
        updateFieldByNameAndValue(enrollmentDetails, "registrationDate", "date", patient.getRegistrationDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
        enrollmentDetails.put("staffId", patient.staffId());
        enrollmentDetails.put("facilityId", patient.facilityId());
        enrollmentDetails.put("motechId", patient.motechId());
        return enrollmentDetails;
    }

    public static TestCWCEnrollment create() {
        final TestCWCEnrollment enrollment = new TestCWCEnrollment();

        enrollment.registrationDate = new LocalDate(2011, 2, 2);
        enrollment.serialNumber = "serialNumber";
        enrollment.addHistory = true;
        enrollment.addCareHistory = Arrays.asList(CwcCareHistory.values());
        enrollment.bcgDate = DateUtil.newDate(2000, 11, 1);
        enrollment.lastOPVDate = DateUtil.newDate(2000, 11, 2);
        enrollment.lastVitaminADate = DateUtil.newDate(2000, 11, 3);
        enrollment.lastIPTiDate = DateUtil.newDate(2000, 11, 4);
        enrollment.yellowFeverDate = DateUtil.newDate(2000, 11, 5);
        enrollment.lastPentaDate = DateUtil.newDate(2000, 11, 6);
        enrollment.measlesDate = DateUtil.newDate(2000, 11, 7);
        enrollment.lastOPV = 1;
        enrollment.lastIPTi = 2;
        enrollment.lastPenta = 3;
        enrollment.country = "Ghana";
        enrollment.region = "Central Region";
        enrollment.district = "Awutu Senya";
        enrollment.subDistrict = "Kasoa";
        enrollment.facility = "Papaase CHPS";
        enrollment.facilityId = "13212";
        return enrollment;
    }


    public Map<String, String> forMobile() {
        return new HashMap<java.lang.String, java.lang.String>() {{
            put("staffId", staffId);
            put("facilityId", facilityId);
            put("motechId", motechPatientId);
            put("registrationDate", registrationDate.toString(forPattern("yyyy-MM-dd")));
            put("serialNumber", serialNumber);
            put("addHistory", booleanCodeForAddHistory(addHistory));
            List<String> concatenatedCareHistories = new ArrayList<String>();
            for (CwcCareHistory history : addCareHistory) {
                concatenatedCareHistories.add(history.name());
            }
            put("addCareHistory", StringUtils.join(concatenatedCareHistories, ","));
            put("bcgDate", bcgDate.toString(forPattern("yyyy-MM-dd")));
            put("lastOPVDate", lastOPVDate.toString(forPattern("yyyy-MM-dd")));
            put("lastVitaminADate", lastVitaminADate.toString(forPattern("yyyy-MM-dd")));
            put("lastIPTiDate", lastIPTiDate.toString(forPattern("yyyy-MM-dd")));
            put("yellowFeverDate", yellowFeverDate.toString(forPattern("yyyy-MM-dd")));
            put("lastPentaDate", lastPentaDate.toString(forPattern("yyyy-MM-dd")));
            put("measlesDate", measlesDate.toString(forPattern("yyyy-MM-dd")));
            put("lastPenta", lastPenta.toString());
            put("lastOPV", lastOPV.toString());
            put("lastIPTi", lastIPTi.toString());

        }};

    }

    public String booleanCodeForAddHistory(boolean value) {
        return value ? "1" : "0";
    }

    public String getStaffId() {
        return staffId;
    }

    public TestCWCEnrollment withStaffId(String staffId) {
        this.staffId = staffId;
        return this;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public TestCWCEnrollment withFacilityId(String facilityId) {
        this.facilityId = facilityId;
        return this;
    }

    public String getMotechPatientId() {
        return motechPatientId;
    }

    public TestCWCEnrollment withMotechPatientId(String motechPatientId) {
        this.motechPatientId = motechPatientId;
        return this;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public TestCWCEnrollment withRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
        return this;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public TestCWCEnrollment withSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public Boolean getAddHistory() {
        return addHistory;
    }

    public TestCWCEnrollment withAddHistory(Boolean addHistory) {
        this.addHistory = addHistory;
        return this;
    }

    public List<CwcCareHistory> getAddCareHistory() {
        return addCareHistory;
    }

    public TestCWCEnrollment withAddCareHistory(List<CwcCareHistory> addCareHistory) {
        this.addCareHistory = addCareHistory;
        return this;
    }

    public LocalDate getBcgDate() {
        return bcgDate;
    }

    public TestCWCEnrollment withBcgDate(LocalDate bcgDate) {
        this.bcgDate = bcgDate;
        return this;
    }

    public LocalDate getLastOPVDate() {
        return lastOPVDate;
    }

    public TestCWCEnrollment withLastOPVDate(LocalDate lastOPVDate) {
        this.lastOPVDate = lastOPVDate;
        return this;
    }

    public LocalDate getLastVitaminADate() {
        return lastVitaminADate;
    }

    public TestCWCEnrollment withLastVitaminADate(LocalDate lastVitaminADate) {
        this.lastVitaminADate = lastVitaminADate;
        return this;
    }

    public LocalDate getLastIPTiDate() {
        return lastIPTiDate;
    }

    public TestCWCEnrollment withLastIPTiDate(LocalDate lastIPTiDate) {
        this.lastIPTiDate = lastIPTiDate;
        return this;
    }

    public LocalDate getYellowFeverDate() {
        return yellowFeverDate;
    }

    public TestCWCEnrollment withYellowFeverDate(LocalDate yellowFeverDate) {
        this.yellowFeverDate = yellowFeverDate;
        return this;
    }

    public LocalDate getLastPentaDate() {
        return lastPentaDate;
    }

    public TestCWCEnrollment withLastPentaDate(LocalDate lastPentaDate) {
        this.lastPentaDate = lastPentaDate;
        return this;
    }

    public LocalDate getMeaslesDate() {
        return measlesDate;
    }

    public TestCWCEnrollment withMeaslesDate(LocalDate measlesDate) {
        this.measlesDate = measlesDate;
        return this;
    }

    public Integer getLastOPV() {
        return lastOPV;
    }

    public TestCWCEnrollment withLastOPV(Integer lastOPV) {
        this.lastOPV = lastOPV;
        return this;
    }

    public Integer getLastIPTi() {
        return lastIPTi;
    }

    public TestCWCEnrollment withLastIPTi(Integer lastIPTi) {
        this.lastIPTi = lastIPTi;
        return this;
    }

    public Integer getLastPenta() {
        return lastPenta;
    }

    public TestCWCEnrollment withLastPenta(Integer lastPenta) {
        this.lastPenta = lastPenta;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    public void setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }
}



