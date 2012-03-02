package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.*;

public class ClientQueryForm extends FormBean {

    @Required
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;

    @Required
    private String facilityId;

    @Required
    @RegEx(pattern = MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;

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
}