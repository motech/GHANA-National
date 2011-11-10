package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.mobileforms.api.domain.FormBean;

import java.util.Date;

public class RegisterClientForm extends FormBean{
    private String registrationMode;
    private String motechId;
    private String motherMotechId;
    private PatientType registrantType;
    private String firstName;
    private String middleName;
    private String lastName;
    private String prefferedName;
    private Date dateOfBirth;
    private Boolean estimatedBirthDate;
    private String sex;
    private Boolean insured;
    private String nhis;
    private Date nhisExpires;
    private String region;
    private String district;
    private String subDistrict;
    private String facilityId;
    private String address;
    private String sender;

    public String getRegistrationMode() {
        return registrationMode;
    }

    public String getMotechId() {
        return motechId;
    }

    public String getMotherMotechId() {
        return motherMotechId;
    }

    public PatientType getRegistrantType() {
        return registrantType;
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

    public Boolean getEstimatedBirthDate() {
        return estimatedBirthDate;
    }

    public String getSex() {
        return sex;
    }

    public Boolean getInsured() {
        return insured;
    }

    public String getNhis() {
        return nhis;
    }

    public Date getNhisExpires() {
        return nhisExpires;
    }

    public String getRegion() {
        return region;
    }

    public String getDistrict() {
        return district;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getAddress() {
        return address;
    }

    public String getSender() {
        return sender;
    }

}
