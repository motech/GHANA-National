package org.motechproject.ghana.national.bean;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.MaxLength;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.MOTECH_ID_PATTERN;
import static org.motechproject.ghana.national.FormFieldRegExPatterns.NUMERIC_OR_NOTAPPLICABLE_PATTERN;

public class PregnancyTerminationForm extends FormBean {
    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;

    @MaxLength(size = 50)
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String facilityId;

    private Date date;

    @Required
    @RegEx(pattern = MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;

    @Required
    private String terminationType;
    @Required
    private String procedure;
    private String complications;
    @Required
    private Boolean maternalDeath;
    @Required
    private Boolean referred;
    private Boolean postAbortionFPCounseled;
    private Boolean postAbortionFPAccepted;
    private String comments;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getTerminationType() {
        return terminationType;
    }

    public void setTerminationType(String terminationType) {
        this.terminationType = terminationType;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getComplications() {
        return complications;
    }

    public void setComplications(String complications) {
        this.complications = complications;
    }

    public Boolean getMaternalDeath() {
        return maternalDeath;
    }

    public void setMaternalDeath(Boolean maternalDeath) {
        this.maternalDeath = maternalDeath;
    }

    public Boolean getReferred() {
        return referred;
    }

    public void setReferred(Boolean referred) {
        this.referred = referred;
    }

    public Boolean getPostAbortionFPCounseled() {
        return postAbortionFPCounseled;
    }

    public void setPostAbortionFPCounseled(Boolean postAbortionFPCounseled) {
        this.postAbortionFPCounseled = postAbortionFPCounseled;
    }

    public Boolean getPostAbortionFPAccepted() {
        return postAbortionFPAccepted;
    }

    public void setPostAbortionFPAccepted(Boolean postAbortionFPAccepted) {
        this.postAbortionFPAccepted = postAbortionFPAccepted;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<String> getTerminationComplications(){
        List<String> terminationComplications = new ArrayList<String>();
        if (StringUtils.isNotEmpty(complications)) {
            Collections.addAll(terminationComplications, complications.split(" "));
        }
        return terminationComplications;
    }

    @Override
    public String groupId() {
        return motechId;
    }
}
