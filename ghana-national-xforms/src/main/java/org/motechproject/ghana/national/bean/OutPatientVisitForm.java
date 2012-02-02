package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import java.util.Date;

public class OutPatientVisitForm extends FormBean {

    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;
    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String facilityId;
    @Required
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;

    @Required
    private PatientType registrantType;

    @Required
    private Date visitDate;
    @Required
    private String serialNumber;
    @Required
    private Boolean insured;

    @Required
    private Boolean newCase;

    @Required
    private Boolean newPatient;

    @Required
    private Integer diagnosis;

    private Integer otherDiagnosis;

    private Integer secondDiagnosis;

    private Integer otherSecondaryDiagnosis;

    @Required
    private Boolean referred;

    private String comments;

    private String sex;

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

    public PatientType getRegistrantType() {
        return registrantType;
    }

    public void setRegistrantType(PatientType registrantType) {
        this.registrantType = registrantType;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Boolean getInsured() {
        return insured;
    }

    public void setInsured(Boolean insured) {
        this.insured = insured;
    }

    public Boolean getNewCase() {
        return newCase;
    }

    public void setNewCase(Boolean newCase) {
        this.newCase = newCase;
    }

    public Boolean getNewPatient() {
        return newPatient;
    }

    public void setNewPatient(Boolean newPatient) {
        this.newPatient = newPatient;
    }

    public Integer getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(Integer diagnosis) {
        this.diagnosis = diagnosis;
    }

    public Integer getOtherDiagnosis() {
        return otherDiagnosis;
    }

    public void setOtherDiagnosis(Integer otherDiagnosis) {
        this.otherDiagnosis = otherDiagnosis;
    }

    public Integer getSecondDiagnosis() {
        return secondDiagnosis;
    }

    public void setSecondDiagnosis(Integer secondDiagnosis) {
        this.secondDiagnosis = secondDiagnosis;
    }

    public Integer getOtherSecondaryDiagnosis() {
        return otherSecondaryDiagnosis;
    }

    public void setOtherSecondaryDiagnosis(Integer otherSecondaryDiagnosis) {
        this.otherSecondaryDiagnosis = otherSecondaryDiagnosis;
    }

    public Boolean getReferred() {
        return referred;
    }

    public void setReferred(Boolean referred) {
        this.referred = referred;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
