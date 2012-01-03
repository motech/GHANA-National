package org.motechproject.functional.data;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

public class TestPatient {
    private String region;
    private String district;
    private String subDistrict;
    private String facility;
    private String middleName;
    private String lastName;
    private String preferredName;
    private String address;
    private LocalDate dateOfBirth;
    private String firstName;
    private PATIENT_REGN_MODE registrationMode;
    private PATIENT_TYPE patientType;
    private boolean estimatedDateOfBirth;
    private boolean insured;
    private String motechId;
    private boolean female;

    public static TestPatient with(String firstName) {
        TestPatient testPatient = new TestPatient();
        testPatient.region = "Central Region";
        testPatient.district = "Awutu Senya";
        testPatient.subDistrict = "Kasoa";
        testPatient.facility = "Papaase CHPS";

        testPatient.middleName = "Middle Name";
        testPatient.lastName = "Last Name";
        testPatient.preferredName = "Preferred Name";
        testPatient.dateOfBirth = DateUtil.newDate(2009, 11, 30);
        testPatient.address = "Address";
        testPatient.female = true;

        testPatient.firstName = firstName;
        testPatient.insured = false;
        return testPatient;
    }

    public TestPatient registrationMode(PATIENT_REGN_MODE registrationMode) {
        this.registrationMode = registrationMode;
        return this;
    }

    public TestPatient patientType(PATIENT_TYPE patientType) {
        this.patientType = patientType;
        return this;
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

    public String middleName() {
        return middleName;
    }

    public String lastName() {
        return lastName;
    }

    public String preferredName() {
        return preferredName;
    }

    public String address() {
        return address;
    }

    public LocalDate dateOfBirth() {
        return dateOfBirth;
    }

    public String firstName() {
        return firstName;
    }

    public PATIENT_REGN_MODE registrationMode() {
        return registrationMode;
    }

    public PATIENT_TYPE patientType() {
        return patientType;
    }

    public boolean estimatedDateOfBirth() {
        return estimatedDateOfBirth;
    }

    public TestPatient estimatedDateOfBirth(boolean estimatedDateOfBirth) {
        this.estimatedDateOfBirth = estimatedDateOfBirth;
        return this;
    }

    public boolean insured() {
        return insured;
    }

    public String genderCode() {
        return isFemale() ? "F" : "M";
    }

    public boolean isFemale() {
        return female;
    }

    public TestPatient motechId(String motechId) {
        this.motechId = motechId;
        return this;
    }

    public String motechId() {
        return motechId;
    }

    public boolean hasMotechId() {
        return StringUtils.isNotEmpty(motechId);
    }

    public TestPatient female(boolean female) {
        this.female = female;
        return this;
    }

    public static enum PATIENT_REGN_MODE {AUTO_GENERATE_ID, USE_PREPRINTED_ID}

    public static enum PATIENT_TYPE {PATIENT_MOTHER, CHILD_UNDER_FIVE, OTHER}
}
