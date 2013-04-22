package org.motechproject.ghana.national.bean;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.MaxLength;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.MOTECH_ID_PATTERN;

public class PNCBabyForm extends FormBean {
    public static final String NUMERIC_OR_NOTAPPLICABLE_PATTERN = "([0-9]+(.[0-9]+)?|[nN][aA])";

    @Required
    @MaxLength(size = 50)
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String staffId;

    @Required
    @MaxLength(size = 50)
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String facilityId;
    private DateTime date;

    @Required
    @RegEx(pattern = MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;
    @Required
    private int visitNumber;
    private Double weight;
    private Double temperature;
    private String location;
    private String house;
    private String community;
    private Boolean referred;
    private Boolean maleInvolved;
    private Integer respiration;
    private Boolean cordConditionNormal;
    private Boolean babyConditionGood;
    private Boolean bcg;
    private Boolean opv0;
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

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public Integer getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(Integer visitNumber) {
        this.visitNumber = visitNumber;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public Boolean getReferred() {
        return referred;
    }

    public void setReferred(Boolean referred) {
        this.referred = referred;
    }

    public Boolean getMaleInvolved() {
        return maleInvolved;
    }

    public void setMaleInvolved(Boolean maleInvolved) {
        this.maleInvolved = maleInvolved;
    }

    public Integer getRespiration() {
        return respiration;
    }

    public void setRespiration(Integer respiration) {
        this.respiration = respiration;
    }

    public Boolean getCordConditionNormal() {
        return cordConditionNormal;
    }

    public void setCordConditionNormal(Boolean cordConditionNormal) {
        this.cordConditionNormal = cordConditionNormal;
    }

    public Boolean getBabyConditionGood() {
        return babyConditionGood;
    }

    public void setBabyConditionGood(Boolean babyConditionGood) {
        this.babyConditionGood = babyConditionGood;
    }

    public Boolean getBcg() {
        return bcg;
    }

    public void setBcg(Boolean bcg) {
        this.bcg = bcg;
    }

    public Boolean getOpv0() {
        return opv0;
    }

    public void setOpv0(Boolean opv0) {
        this.opv0 = opv0;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String groupId() {
        return motechId;
    }
}
