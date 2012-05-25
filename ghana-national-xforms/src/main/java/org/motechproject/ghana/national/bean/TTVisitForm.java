package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.FormFieldRegExPatterns;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.MaxLength;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import java.util.Date;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.NUMERIC_OR_NOTAPPLICABLE_PATTERN;

public class TTVisitForm extends FormBean {


    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;

    @MaxLength(size = 50)
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String facilityId;

    private Date date;

    @Required
    @RegEx(pattern = FormFieldRegExPatterns.MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;

    private String ttDose;

    public String getStaffId() {
        return staffId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public Date getDate() {
        return date;
    }

    public String getMotechId() {
        return motechId;
    }

    public String getTtDose() {
        return ttDose;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public void setTtDose(String ttDose) {
        this.ttDose = ttDose;
    }


    @Override
    public String groupId() {
        return motechId;
    }
}
