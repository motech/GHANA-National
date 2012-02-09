package org.motechproject.ghana.national.functional.data;

public class TestMobileMidwifeUnEnrollment {
    private String staffId;
    private String facilityId;
    private String patientId;
    private String region;
    private String district;
    private String subDistrict;
    private String facility;
    private String country;


    public static TestMobileMidwifeUnEnrollment with(String staffId) {
        TestMobileMidwifeUnEnrollment enrollment = new TestMobileMidwifeUnEnrollment();
        enrollment.staffId = staffId;
        enrollment.country = "Ghana";
        enrollment.region = "Central Region";
        enrollment.district = "Awutu Senya";
        enrollment.subDistrict = "Kasoa";
        enrollment.facility = "Papaase CHPS";
        enrollment.facilityId = "13212";
        return enrollment;
    }

    public static TestMobileMidwifeUnEnrollment with(String staffId, String facilityId) {
        TestMobileMidwifeUnEnrollment midwifeEnrollment = TestMobileMidwifeUnEnrollment.with(staffId);
        midwifeEnrollment.facilityId = facilityId;
        return midwifeEnrollment;
    }

    public String region() {
        return region;
    }

    public String district() {
        return district;
    }

    public String subDistrict() {
        return subDistrict;
    }

    public String facility() {
        return facility;
    }

    public String country() {
        return country;
    }

    public String staffId() {
        return staffId;
    }

    public String facilityId() {
        return facilityId;
    }

    public TestMobileMidwifeUnEnrollment staffId(String staffId) {
        this.staffId = staffId;
        return this;
    }

    public TestMobileMidwifeUnEnrollment facilityId(String facilityId) {
        this.facilityId = facilityId;
        return this;
    }

    public TestMobileMidwifeUnEnrollment region(String region) {
        this.region = region;
        return this;
    }

    public TestMobileMidwifeUnEnrollment district(String district) {
        this.district = district;
        return this;
    }

    public TestMobileMidwifeUnEnrollment subDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
        return this;
    }

    public TestMobileMidwifeUnEnrollment facility(String facility) {
        this.facility = facility;
        return this;
    }

    public TestMobileMidwifeUnEnrollment country(String country) {
        this.country = country;
        return this;
    }

    public TestMobileMidwifeUnEnrollment patientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestMobileMidwifeUnEnrollment that = (TestMobileMidwifeUnEnrollment) o;

        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (district != null ? !district.equals(that.district) : that.district != null) return false;
//        if (facility != null ? !facility.equals(that.facility) : that.facility != null) return false;
        if (facilityId != null ? !facilityId.equals(that.facilityId) : that.facilityId != null) return false;
        if (patientId != null ? !patientId.equals(that.patientId) : that.patientId != null) return false;
        if (region != null ? !region.equals(that.region) : that.region != null) return false;
        if (staffId != null ? !staffId.equals(that.staffId) : that.staffId != null) return false;
        if (subDistrict != null ? !subDistrict.equals(that.subDistrict) : that.subDistrict != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = staffId != null ? staffId.hashCode() : 0;
        result = 31 * result + (facilityId != null ? facilityId.hashCode() : 0);
        result = 31 * result + (patientId != null ? patientId.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (district != null ? district.hashCode() : 0);
        result = 31 * result + (subDistrict != null ? subDistrict.hashCode() : 0);
//        result = 31 * result + (facility != null ? facility.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestMobileMidwifeUnEnrollment{" +
                "staffId='" + staffId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", region='" + region + '\'' +
                ", district='" + district + '\'' +
                ", subDistrict='" + subDistrict + '\'' +
                ", facility='" + facility + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
