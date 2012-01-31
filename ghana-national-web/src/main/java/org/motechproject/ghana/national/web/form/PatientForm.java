package org.motechproject.ghana.national.web.form;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationType;

import java.util.Date;

public class PatientForm {
    private RegistrationType registrationMode;

    private String staffId;
    private String motechId;
    private String parentId;
    private PatientType typeOfPatient;
    private String firstName;
    private String middleName;
    private String lastName;
    private Date dateOfBirth;
    private Boolean estimatedDateOfBirth;
    private String sex;
    private Boolean insured;
    private String nhisNumber;
    private Date nhisExpirationDate;
    private String region;
    private String district;
    private String subDistrict;
    private String facilityId;
    private String address;
    private String facilityName;
    private String phoneNumber;

    public RegistrationType getRegistrationMode() {
        return registrationMode;
    }

    public PatientForm() {
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public PatientForm(Patient patient) {
        this.motechId = patient.getMrsPatient().getMotechId();
        this.firstName = patient.getMrsPatient().getPerson().getFirstName();
        this.middleName = patient.getMrsPatient().getPerson().getMiddleName();
        this.lastName = patient.getMrsPatient().getPerson().getLastName();
        this.dateOfBirth = patient.getMrsPatient().getPerson().getDateOfBirth();
        this.sex = patient.getMrsPatient().getPerson().getGender();
    }

    public PatientForm setRegistrationMode(RegistrationType registrationMode) {
        this.registrationMode = registrationMode;
        return this;
    }

    public String getMotechId() {
        return motechId;
    }

    public PatientForm setMotechId(String motechId) {
        this.motechId = motechId;
        return this;
    }

    public PatientType getTypeOfPatient() {
        return typeOfPatient;
    }

    public PatientForm setTypeOfPatient(PatientType typeOfPatient) {
        this.typeOfPatient = typeOfPatient;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public PatientForm setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public PatientForm setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PatientForm setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public PatientForm setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public Boolean getEstimatedDateOfBirth() {
        return estimatedDateOfBirth;
    }

    public PatientForm setEstimatedDateOfBirth(Boolean estimatedDateOfBirth) {
        this.estimatedDateOfBirth = estimatedDateOfBirth;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public PatientForm setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public Boolean getInsured() {
        return insured;
    }

    public PatientForm setInsured(Boolean insured) {
        this.insured = insured;
        return this;
    }

    public String getNhisNumber() {
        return nhisNumber;
    }

    public PatientForm setNhisNumber(String nhisNumber) {
        this.nhisNumber = nhisNumber;
        return this;
    }

    public Date getNhisExpirationDate() {
        return nhisExpirationDate;
    }

    public PatientForm setNhisExpirationDate(Date nhisExpirationDate) {
        this.nhisExpirationDate = nhisExpirationDate;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public PatientForm setRegion(String region) {
        this.region = region;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public PatientForm setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    public PatientForm setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
        return this;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public PatientForm setFacilityId(String facilityId) {
        this.facilityId = facilityId;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public PatientForm setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public PatientForm setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public PatientForm setFacilityName(String facilityName) {
        this.facilityName = facilityName;
        return this;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public PatientForm setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }
}
