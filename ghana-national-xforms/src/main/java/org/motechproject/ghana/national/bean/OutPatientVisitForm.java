package org.motechproject.ghana.national.bean;

import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.MaxLength;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.openmrs.omod.validator.VerhoeffValidator;

import java.util.Date;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.MOTECH_ID_PATTERN;
import static org.motechproject.ghana.national.FormFieldRegExPatterns.NHIS_NO_PATTERN;

public class OutPatientVisitForm extends FormBean {

    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String staffId;

    @Required
    @MotechId(validator = VerhoeffValidator.class)
    private String facilityId;

    @RegEx(pattern = MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;

    @Required
    private PatientType registrantType;

    @Required
    private Boolean visitor;

    @Required
    private Date visitDate;

    private Date dateOfBirth;

    @Required
    private String serialNumber;

    private Boolean insured;

    @MaxLength(size = 30)
    @RegEx(pattern = NHIS_NO_PATTERN)
    private String nhis;

    private Date nhisExpires;

    @Required
    private Boolean newCase;

    @Required
    private Boolean newPatient;

    @Required
    private Integer diagnosis;

    private Integer otherDiagnosis;

    private Integer secondDiagnosis;

    private Integer otherSecondaryDiagnosis;

    private Boolean rdtGiven;

    private Boolean rdtPositive;

    private Boolean actTreated;

    @Required
    private Boolean referred;

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

    public Boolean isVisitor() {
        return visitor;
    }

    public void setVisitor(Boolean visitor) {
        this.visitor = visitor;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public String getNhis() {
        return nhis;
    }

    public void setNhis(String nhis) {
        this.nhis = nhis;
    }

    public Date getNhisExpires() {
        return nhisExpires;
    }

    public void setNhisExpires(Date nhisExpires) {
        this.nhisExpires = nhisExpires;
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

    public Boolean getRdtGiven() {
        return rdtGiven;
    }

    public void setRdtGiven(Boolean rdtGiven) {
        this.rdtGiven = rdtGiven;
    }

    public Boolean getRdtPositive() {
        return rdtPositive;
    }

    public void setRdtPositive(Boolean rdtPositive) {
        this.rdtPositive = rdtPositive;
    }

    public Boolean getActTreated() {
        return actTreated;
    }

    public void setActTreated(Boolean actTreated) {
        this.actTreated = actTreated;
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

}
