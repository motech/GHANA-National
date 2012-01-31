package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.MaxLength;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import java.util.Date;

public class EditClientForm extends FormBean {
    public static final String NUMERIC_OR_NOTAPPLICABLE_PATTERN = "([0-9]+(.[0-9]+)?|[nN][aA])";
    public static final String NAME_PATTERN = "[0-9.\\-\\s]*[a-zA-Z]?[a-zA-Z0-9.\\-\\s]*";

    @Required @RegEx(pattern = Constants.MOTECH_ID_PATTERN) @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;
    @RegEx(pattern = Constants.MOTECH_ID_PATTERN) @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motherMotechId;
    @MaxLength(size = 100) @RegEx(pattern = NAME_PATTERN)
    private String firstName;
    @MaxLength(size = 100) @RegEx(pattern = NAME_PATTERN)
    String middleName;
    @MaxLength(size = 100) @RegEx(pattern = NAME_PATTERN)
    private String lastName;
    @MaxLength(size = 50) @RegEx(pattern = NAME_PATTERN)
    private String prefferedName;
    private Date dateOfBirth;
    @RegEx(pattern = "[MmFf]")
    private String sex;
    @MaxLength(size = 30) @RegEx(pattern = "[a-zA-Z0-9.,'/\\\\-\\_\\s]+")
    private String nhis;
    private Date nhisExpires;
    @MaxLength(size = 50) @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String facilityId;
    @MaxLength(size = 50)
    private String address;
    @Required @MaxLength(size = 50) @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN) @MotechId(validator = VerhoeffValidator.class)
    private String staffId;

    @Required @MaxLength(size = 50) @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String updatePatientFacilityId;

    @Required
    private Date date;
    @RegEx(pattern = "0[0-9]{9}") @MaxLength(size = 50)
    private String phoneNumber;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMotechId() {
        return motechId;
    }

    public String getMotherMotechId() {
        return motherMotechId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPrefferedName() {
        return prefferedName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public String getNhis() {
        return nhis;
    }

    public Date getNhisExpires() {
        return nhisExpires;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getAddress() {
        return address;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public void setMotherMotechId(String motherMotechId) {
        this.motherMotechId = motherMotechId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPrefferedName(String prefferedName) {
        this.prefferedName = prefferedName;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setNhis(String nhis) {
        this.nhis = nhis;
    }

    public void setNhisExpires(Date nhisExpires) {
        this.nhisExpires = nhisExpires;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUpdatePatientFacilityId() {
        return updatePatientFacilityId;
    }

    public void setUpdatePatientFacilityId(String updatePatientFacilityId) {
        this.updatePatientFacilityId = updatePatientFacilityId;
    }
}
