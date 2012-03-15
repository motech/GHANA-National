package org.motechproject.ghana.national.service.request;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.PNCVisit;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSUser;

public class PNCBabyRequest {
    private Patient patient;
    private MRSUser staff;
    private Facility facility;
    private DateTime date;
    private PNCVisit visit;
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

    public Patient getPatient() {
        return patient;
    }

    public MRSUser getStaff() {
        return staff;
    }

    public Facility getFacility() {
        return facility;
    }

    public DateTime getDate() {
        return date;
    }

    public PNCVisit getVisit() {
        return visit;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getTemperature() {
        return temperature;
    }

    public String getLocation() {
        return location;
    }

    public String getHouse() {
        return house;
    }

    public String getCommunity() {
        return community;
    }

    public Boolean getReferred() {
        return referred;
    }

    public Boolean getMaleInvolved() {
        return maleInvolved;
    }

    public Integer getRespiration() {
        return respiration;
    }

    public Boolean getCordConditionNormal() {
        return cordConditionNormal;
    }

    public Boolean getBabyConditionGood() {
        return babyConditionGood;
    }

    public Boolean getBcg() {
        return bcg;
    }

    public Boolean getOpv0() {
        return opv0;
    }

    public String getComments() {
        return comments;
    }


    public PNCBabyRequest patient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public PNCBabyRequest staff(MRSUser staff) {
        this.staff = staff;
        return this;
    }

    public PNCBabyRequest facility(Facility facility) {
        this.facility = facility;
        return this;
    }

    public PNCBabyRequest date(DateTime date) {
        this.date = date;
        return this;
    }

    public PNCBabyRequest visit(Integer pncVisitNo) {
        this.visit = PNCVisit.byVisitNumber(pncVisitNo);
        return this;
    }

    public PNCBabyRequest weight(Double weight) {
        this.weight = weight;
        return this;
    }

    public PNCBabyRequest temperature(Double temperature) {
        this.temperature = temperature;
        return this;
    }

    public PNCBabyRequest location(String location) {
        this.location = location;
        return this;
    }

    public PNCBabyRequest house(String house) {
        this.house = house;
        return this;
    }

    public PNCBabyRequest community(String community) {
        this.community = community;
        return this;
    }

    public PNCBabyRequest referred(Boolean referred) {
        this.referred = referred;
        return this;
    }

    public PNCBabyRequest maleInvolved(Boolean maleInvolved) {
        this.maleInvolved = maleInvolved;
        return this;
    }

    public PNCBabyRequest respiration(Integer respiration) {
        this.respiration = respiration;
        return this;
    }

    public PNCBabyRequest cordConditionNormal(Boolean cordConditionNormal) {
        this.cordConditionNormal = cordConditionNormal;
        return this;
    }

    public PNCBabyRequest babyConditionGood(Boolean babyConditionGood) {
        this.babyConditionGood = babyConditionGood;
        return this;
    }

    public PNCBabyRequest bcg(Boolean bcg) {
        this.bcg = bcg;
        return this;
    }

    public PNCBabyRequest opv0(Boolean opv0) {
        this.opv0 = opv0;
        return this;
    }

    public PNCBabyRequest comments(String comments) {
        this.comments = comments;
        return this;
    }
}
