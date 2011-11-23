package org.motechproject.ghana.national.web.form;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.Facility;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.motechproject.ghana.national.tools.Utility.safeToString;

public class CreatePatientForm {
    private RegistrationType registrationMode;
    private String motechId;
    private String parentId;
    private PatientType typeOfPatient;
    private String firstName;
    private String middleName;
    private String lastName;
    private String preferredName;
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

    public RegistrationType getRegistrationMode() {
        return registrationMode;
    }

    public void setRegistrationMode(RegistrationType registrationMode) {
        this.registrationMode = registrationMode;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public PatientType getTypeOfPatient() {
        return typeOfPatient;
    }

    public void setTypeOfPatient(PatientType typeOfPatient) {
        this.typeOfPatient = typeOfPatient;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getEstimatedDateOfBirth() {
        return estimatedDateOfBirth;
    }

    public void setEstimatedDateOfBirth(Boolean estimatedDateOfBirth) {
        this.estimatedDateOfBirth = estimatedDateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Boolean getInsured() {
        return insured;
    }

    public void setInsured(Boolean insured) {
        this.insured = insured;
    }

    public String getNhisNumber() {
        return nhisNumber;
    }

    public void setNhisNumber(String nhisNumber) {
        this.nhisNumber = nhisNumber;
    }

    public Date getNhisExpirationDate() {
        return nhisExpirationDate;
    }

    public void setNhisExpirationDate(Date nhisExpirationDate) {
        this.nhisExpirationDate = nhisExpirationDate;
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

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Patient getPatient(String patientID) {
        List<Attribute> attributes = Arrays.asList(
                new Attribute(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute(), safeToString(this.getNhisExpirationDate())),
                new Attribute(PatientAttributes.NHIS_NUMBER.getAttribute(), this.getNhisNumber()),
                new Attribute(PatientAttributes.INSURED.getAttribute(), safeToString(this.getInsured())));
        if (patientID.equals(""))
            patientID = this.getMotechId();
        org.motechproject.mrs.model.Patient mrsPatient = new org.motechproject.mrs.model.Patient(patientID, this.getFirstName(),
                this.getMiddleName(), this.getLastName(), this.getPreferredName(), this.getDateOfBirth(), this.getEstimatedDateOfBirth(), this.getSex(),
                this.getAddress(), attributes, new Facility(this.getFacilityId()));

        return new Patient(mrsPatient);
    }
}
