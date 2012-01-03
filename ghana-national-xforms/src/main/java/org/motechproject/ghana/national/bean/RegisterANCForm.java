package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.MaxLength;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;

import java.util.Date;

public class RegisterANCForm extends FormBean {
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
    private RegistrationToday registrationToday;
    
    @Required
    private Date registrationDate;
    
    @RegEx(pattern = MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;
    
    @Required
    private Date expectedDeliveryDate;
    
    @Required
    private Integer gravida;

    @Required
    private Integer parity;

    @Required
    private Boolean deliveryDateConfirmedByCHW;
    
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

    public Date getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Integer getGravida() {
        return gravida;
    }

    public void setGravida(Integer gravida) {
        this.gravida = gravida;
    }

    public Integer getParity() {
        return parity;
    }

    public void setParity(Integer parity) {
        this.parity = parity;
    }

    public Boolean getDeliveryDateConfirmedByCHW() {
        return deliveryDateConfirmedByCHW;
    }

    public void setDeliveryDateConfirmedByCHW(Boolean deliveryDateConfirmedByCHW) {
        this.deliveryDateConfirmedByCHW = deliveryDateConfirmedByCHW;
    }
}
