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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutPatientVisit that = (OutPatientVisit) o;

        if (actTreated != null ? !actTreated.equals(that.actTreated) : that.actTreated != null) return false;
        if (comments != null ? !comments.equals(that.comments) : that.comments != null) return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(that.dateOfBirth) : that.dateOfBirth != null) return false;
        if (diagnosis != null ? !diagnosis.equals(that.diagnosis) : that.diagnosis != null) return false;
        if (facilityId != null ? !facilityId.equals(that.facilityId) : that.facilityId != null) return false;
        if (gender != null ? !gender.equals(that.gender) : that.gender != null) return false;
        if (insured != null ? !insured.equals(that.insured) : that.insured != null) return false;
        if (newCase != null ? !newCase.equals(that.newCase) : that.newCase != null) return false;
        if (newPatient != null ? !newPatient.equals(that.newPatient) : that.newPatient != null) return false;
        if (nhis != null ? !nhis.equals(that.nhis) : that.nhis != null) return false;
        if (nhisExpires != null ? !nhisExpires.equals(that.nhisExpires) : that.nhisExpires != null) return false;
        if (rdtGiven != null ? !rdtGiven.equals(that.rdtGiven) : that.rdtGiven != null) return false;
        if (rdtPositive != null ? !rdtPositive.equals(that.rdtPositive) : that.rdtPositive != null) return false;
        if (referred != null ? !referred.equals(that.referred) : that.referred != null) return false;
        if (registrantType != that.registrantType) return false;
        if (secondDiagnosis != null ? !secondDiagnosis.equals(that.secondDiagnosis) : that.secondDiagnosis != null)
            return false;
        if (serialNumber != null ? !serialNumber.equals(that.serialNumber) : that.serialNumber != null) return false;
        if (staffId != null ? !staffId.equals(that.staffId) : that.staffId != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (visitDate != null ? !visitDate.equals(that.visitDate) : that.visitDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (staffId != null ? staffId.hashCode() : 0);
        result = 31 * result + (facilityId != null ? facilityId.hashCode() : 0);
        result = 31 * result + (visitDate != null ? visitDate.hashCode() : 0);
        result = 31 * result + (serialNumber != null ? serialNumber.hashCode() : 0);
        result = 31 * result + (registrantType != null ? registrantType.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (insured != null ? insured.hashCode() : 0);
        result = 31 * result + (nhis != null ? nhis.hashCode() : 0);
        result = 31 * result + (nhisExpires != null ? nhisExpires.hashCode() : 0);
        result = 31 * result + (newCase != null ? newCase.hashCode() : 0);
        result = 31 * result + (newPatient != null ? newPatient.hashCode() : 0);
        result = 31 * result + (diagnosis != null ? diagnosis.hashCode() : 0);
        result = 31 * result + (secondDiagnosis != null ? secondDiagnosis.hashCode() : 0);
        result = 31 * result + (rdtGiven != null ? rdtGiven.hashCode() : 0);
        result = 31 * result + (rdtPositive != null ? rdtPositive.hashCode() : 0);
        result = 31 * result + (actTreated != null ? actTreated.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (referred != null ? referred.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        return result;
    }
}
