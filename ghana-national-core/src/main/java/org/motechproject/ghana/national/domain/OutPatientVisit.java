package org.motechproject.ghana.national.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.Date;

@TypeDiscriminator("doc.type === 'OutPatientVisit'")
public class OutPatientVisit extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "OutPatientVisit";
    @JsonProperty
    private String staffId;
    @JsonProperty
    private String facilityId;
    @JsonProperty
    private Date visitDate;
    @JsonProperty
    private String serialNumber;

    @JsonProperty
    private PatientType registrantType;

    @JsonProperty
    private Date dateOfBirth;

    @JsonProperty
    private Boolean insured;

    @JsonProperty
    private String nhis;

    @JsonProperty
    private Date nhisExpires;

    @JsonProperty
    private Boolean newCase;

    @JsonProperty
    private Boolean newPatient;

    @JsonProperty
    private Integer diagnosis;

    @JsonProperty
    private Integer secondDiagnosis;

    @JsonProperty
    private Boolean rdtGiven;

    @JsonProperty
    private Boolean rdtPositive;

    @JsonProperty
    private Boolean actTreated;

    @JsonProperty
    private String gender;

    @JsonProperty
    private Boolean referred;

    @JsonProperty
    private String comments;

    public OutPatientVisit(){

    }

    public String getType() {
        return type;
    }

    public OutPatientVisit setType(String type) {
        this.type = type;
        return this;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public OutPatientVisit setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public Boolean getInsured() {
        return insured;
    }

    public OutPatientVisit setInsured(Boolean insured) {
        this.insured = insured;
        return this;
    }

    public String getNhis() {
        return nhis;
    }

    public OutPatientVisit setNhis(String nhis) {
        this.nhis = nhis;
        return this;
    }

    public Date getNhisExpires() {
        return nhisExpires;
    }

    public OutPatientVisit setNhisExpires(Date nhisExpires) {
        this.nhisExpires = nhisExpires;
        return this;
    }

    public Boolean getNewCase() {
        return newCase;
    }

    public OutPatientVisit setNewCase(Boolean newCase) {
        this.newCase = newCase;
        return this;
    }

    public Boolean getNewPatient() {
        return newPatient;
    }

    public OutPatientVisit setNewPatient(Boolean newPatient) {
        this.newPatient = newPatient;
        return this;
    }

    public Integer getDiagnosis() {
        return diagnosis;
    }

    public OutPatientVisit setDiagnosis(Integer diagnosis) {
        this.diagnosis = diagnosis;
        return this;
    }

    public Integer getSecondDiagnosis() {
        return secondDiagnosis;
    }

    public OutPatientVisit setSecondDiagnosis(Integer secondDiagnosis) {
        this.secondDiagnosis = secondDiagnosis;
        return this;
    }

    public Boolean getRdtGiven() {
        return rdtGiven;
    }

    public OutPatientVisit setRdtGiven(Boolean rdtGiven) {
        this.rdtGiven = rdtGiven;
        return this;
    }

    public Boolean getRdtPositive() {
        return rdtPositive;
    }

    public OutPatientVisit setRdtPositive(Boolean rdtPositive) {
        this.rdtPositive = rdtPositive;
        return this;
    }

    public Boolean getActTreated() {
        return actTreated;
    }

    public OutPatientVisit setActTreated(Boolean actTreated) {
        this.actTreated = actTreated;
        return this;
    }

    public Boolean getReferred() {
        return referred;
    }

    public OutPatientVisit setReferred(Boolean referred) {
        this.referred = referred;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public OutPatientVisit setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public String getStaffId() {
        return staffId;
    }

    public OutPatientVisit setStaffId(String staffId) {
        this.staffId = staffId;
        return this;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public OutPatientVisit setFacilityId(String facilityId) {
        this.facilityId = facilityId;
        return this;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public OutPatientVisit setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
        return this;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public OutPatientVisit setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public PatientType getRegistrantType() {
        return registrantType;
    }

    public OutPatientVisit setRegistrantType(PatientType registrantType) {
        this.registrantType = registrantType;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public OutPatientVisit setGender(String gender) {
        this.gender = gender;
        return this;
    }
}
