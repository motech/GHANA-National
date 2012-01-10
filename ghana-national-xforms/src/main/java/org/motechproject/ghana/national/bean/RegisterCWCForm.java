package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.MaxLength;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;

import java.util.Date;

public class RegisterCWCForm extends FormBean {
    public static final String NUMERIC_OR_NOTAPPLICABLE_PATTERN = "([0-9]+(.[0-9]+)?|[nN][aA])";
    public static final String MOTECH_ID_PATTERN = "[0-9]{7}";

    @Required
    @MaxLength(size = 50)
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String staffId;

    @Required
    @MaxLength(size = 50)
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String facilityId;

    @Required
    private Date registrationDate;

    @Required
    @RegEx(pattern = MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;

    @Required
    private String serialNumber;

    @Required
    private RegistrationToday registrationToday;

    @Required
    private Boolean addHistory;

    @Required
    private String addCareHistory;

    @RegEx(pattern = "0[0-9]{9}")
    private String regPhone;

    public String getRegPhone() {
        return regPhone;
    }

    public void setRegPhone(String regPhone) {
        this.regPhone = regPhone;
    }


    public void setRegistrationToday(RegistrationToday registrationToday) {
        this.registrationToday = registrationToday;
    }

    public RegistrationToday getRegistrationToday() {
        return registrationToday;
    }

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

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Boolean getAddHistory() {
        return addHistory;
    }

    public void setAddHistory(Boolean addHistory) {
        this.addHistory = addHistory;
    }

    public String getAddCareHistory() {
        return addCareHistory;
    }

    public void setAddCareHistory(String addCareHistory) {
        this.addCareHistory = addCareHistory;
    }

}
