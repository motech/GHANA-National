package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.ClientQueryType;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.MaxLength;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import java.util.Date;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.*;

public class ClientQueryForm extends FormBean {

    @Required
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;

    @Required
    private String facilityId;

    @Required
    private ClientQueryType clientQueryType;

    @RegEx(pattern = MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;

    @MaxLength(size = 100) @RegEx(pattern = NAME_PATTERN)
    private String firstName;

    @MaxLength(size = 100) @RegEx(pattern = NAME_PATTERN)
    private String lastName;

    private Date dateOfBirth;

    @MaxLength(size = 30) @RegEx(pattern = NHIS_NO_PATTERN)
    private String nhis;

    @RegEx(pattern = PHONE_NO_PATTERN)
    private String phoneNumber;

    @Required
    @RegEx(pattern = PHONE_NO_PATTERN)
    private String sender;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNhis() {
        return nhis;
    }

    public void setNhis(String nhis) {
        this.nhis = nhis;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ClientQueryType getClientQueryType() {
        return clientQueryType;
    }

    public void setClientQueryType(ClientQueryType clientQueryType) {
        this.clientQueryType = clientQueryType;
    }
}