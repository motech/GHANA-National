package org.motechproject.functional.data;

import org.motechproject.functional.util.DataGenerator;

public class TestFacility {
    private String facilityId;
    private String name;
    private String country;
    private String region;
    private String district;
    private String subDistrict;
    private String phoneNumber;

    public static TestFacility with(String name){
        TestFacility testFacility = new TestFacility();
        testFacility.name = name;
        testFacility.country = "Ghana";
        testFacility.region = "Central Region";
        testFacility.district = "Awutu Senya";
        testFacility.subDistrict = "Bawjiase";
        testFacility.phoneNumber = new DataGenerator().randomPhoneNumber();
        return testFacility;
    }

    public String name() {
        return name;
    }

    public String country() {
        return country;
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

    public String phoneNumber() {
        return phoneNumber;
    }

    public TestFacility region(String region) {
        this.region = region;
        this.district = null;
        this.subDistrict = null;
        return this;
    }

    public TestFacility phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String facilityId() {
        return facilityId;
    }

    public TestFacility facilityId(String facilityId) {
        this.facilityId = facilityId;
        return this;
    }

}
