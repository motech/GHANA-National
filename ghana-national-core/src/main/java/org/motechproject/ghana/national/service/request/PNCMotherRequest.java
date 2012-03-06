package org.motechproject.ghana.national.service.request;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSUser;

public class PNCMotherRequest {
    private Patient patient;
    private MRSUser staff;
    private Facility facility;
    private DateTime date;
    private String visitNumber;
    private String vitaminA;
    private String ttDose;
    private String lochiaColour;
    private Boolean lochiaOdourFoul;
    private Boolean lochiaAmountExcess;
    private String location;
    private String house;
    private String community;
    private Boolean referred;
    private Boolean maleInvolved;
    private Double temperature;
    private String fht;
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

    public String getVisitNumber() {
        return visitNumber;
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

    public String getComments() {
        return comments;
    }

    public PNCMotherRequest patient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public PNCMotherRequest staff(MRSUser staff) {
        this.staff = staff;
        return this;
    }

    public PNCMotherRequest facility(Facility facility) {
        this.facility = facility;
        return this;
    }

    public PNCMotherRequest date(DateTime date) {
        this.date = date;
        return this;
    }

    public PNCMotherRequest visitNumber(String visitNumber) {
        this.visitNumber = visitNumber;
        return this;
    }

    public PNCMotherRequest temperature(Double temperature) {
        this.temperature = temperature;
        return this;
    }

    public PNCMotherRequest location(String location) {
        this.location = location;
        return this;
    }

    public PNCMotherRequest house(String house) {
        this.house = house;
        return this;
    }

    public PNCMotherRequest community(String community) {
        this.community = community;
        return this;
    }

    public PNCMotherRequest referred(Boolean referred) {
        this.referred = referred;
        return this;
    }

    public PNCMotherRequest maleInvolved(Boolean maleInvolved) {
        this.maleInvolved = maleInvolved;
        return this;
    }

    public PNCMotherRequest comments(String comments) {
        this.comments = comments;
        return this;
    }

    public String getVitaminA() {
        return vitaminA;
    }

    public String getTtDose() {
        return ttDose;
    }

    public String getLochiaColour() {
        return lochiaColour;
    }

    public Boolean getLochiaOdourFoul() {
        return lochiaOdourFoul;
    }

    public Boolean getLochiaAmountExcess() {
        return lochiaAmountExcess;
    }

    public String getFht() {
        return fht;
    }

    public PNCMotherRequest vitaminA(String vitaminA) {
        this.vitaminA = vitaminA;
        return this;
    }

    public PNCMotherRequest ttDose(String ttDose) {
        this.ttDose = ttDose;
        return this;
    }

    public PNCMotherRequest lochiaColour(String lochiaColor) {
        this.lochiaColour = lochiaColor;
        return this;
    }

    public PNCMotherRequest lochiaOdourFoul(Boolean lochiaOdourFoul) {
        this.lochiaOdourFoul = lochiaOdourFoul;
        return this;
    }

    public PNCMotherRequest lochiaAmountExcess(Boolean lochiaAmountExcess) {
        this.lochiaAmountExcess = lochiaAmountExcess;
        return this;
    }

    public PNCMotherRequest fht(String fht) {
        this.fht = fht;
        return this;
    }
}
