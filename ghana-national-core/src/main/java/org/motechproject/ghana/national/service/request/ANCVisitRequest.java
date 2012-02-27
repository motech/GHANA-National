package org.motechproject.ghana.national.service.request;

import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSUser;

import java.util.Date;

public class ANCVisitRequest {
    private Date date;
    private String serialNumber;
    private String visitNumber;
    private Date estDeliveryDate;
    private Integer bpSystolic;
    private Integer bpDiastolic;
    private Double weight;
    private String ttdose;
    private String iptdose;
    private Boolean iptReactive;
    private String itnUse;
    private Double fht;
    private Integer fhr;
    private String urineTestProteinPositive;
    private String urineTestGlucosePositive;
    private Double hemoglobin;
    private String vdrlReactive;
    private String vdrlTreatment;
    private String dewormer;
    private String pmtct;
    private String preTestCounseled;
    private String hivTestResult;
    private String postTestCounseled;
    private String pmtctTreament;
    private String location;
    private String house;
    private String community;
    private String referred;
    private Boolean maleInvolved;
    private Date nextANCDate;
    private String comments;
    private Patient patient;
    private MRSUser staff;
    private Facility facility;

    public ANCVisitRequest facility(Facility facility) {
        this.facility = facility;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public ANCVisitRequest date(Date date) {
        this.date = date;
        return this;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public ANCVisitRequest serialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public String getVisitNumber() {
        return visitNumber;
    }

    public ANCVisitRequest visitNumber(String visitNumber) {
        this.visitNumber = visitNumber;
        return this;
    }

    public Date getEstDeliveryDate() {
        return estDeliveryDate;
    }

    public ANCVisitRequest estDeliveryDate(Date estDeliveryDate) {
        this.estDeliveryDate = estDeliveryDate;
        return this;
    }

    public Integer getBpSystolic() {
        return bpSystolic;
    }

    public ANCVisitRequest bpSystolic(Integer bpSystolic) {
        this.bpSystolic = bpSystolic;
        return this;
    }

    public Integer getBpDiastolic() {
        return bpDiastolic;
    }

    public ANCVisitRequest bpDiastolic(Integer bpDiastolic) {
        this.bpDiastolic = bpDiastolic;
        return this;
    }

    public Double getWeight() {
        return weight;
    }

    public ANCVisitRequest weight(Double weight) {
        this.weight = weight;
        return this;
    }

    public String getTtdose() {
        return ttdose;
    }

    public ANCVisitRequest ttdose(String ttdose) {
        this.ttdose = ttdose;
        return this;
    }

    public String getIptdose() {
        return iptdose;
    }

    public ANCVisitRequest iptdose(String iptdose) {
        this.iptdose = iptdose;
        return this;
    }

    public Boolean getIptReactive() {
        return iptReactive;
    }

    public ANCVisitRequest iptReactive(Boolean iptReactive) {
        this.iptReactive = iptReactive;
        return this;
    }

    public String getItnUse() {
        return itnUse;
    }

    public ANCVisitRequest itnUse(String itnUse) {
        this.itnUse = itnUse;
        return this;
    }

    public Double getFht() {
        return fht;
    }

    public ANCVisitRequest fht(Double fht) {
        this.fht = fht;
        return this;
    }

    public Integer getFhr() {
        return fhr;
    }

    public ANCVisitRequest fhr(Integer fhr) {
        this.fhr = fhr;
        return this;
    }

    public String getUrineTestProteinPositive() {
        return urineTestProteinPositive;
    }

    public ANCVisitRequest urineTestProteinPositive(String urineTestProteinPositive) {
        this.urineTestProteinPositive = urineTestProteinPositive;
        return this;
    }

    public String getUrineTestGlucosePositive() {
        return urineTestGlucosePositive;
    }

    public ANCVisitRequest urineTestGlucosePositive(String urineTestGlucosePositive) {
        this.urineTestGlucosePositive = urineTestGlucosePositive;
        return this;
    }

    public Double getHemoglobin() {
        return hemoglobin;
    }

    public ANCVisitRequest hemoglobin(Double hemoglobin) {
        this.hemoglobin = hemoglobin;
        return this;
    }

    public String getVdrlReactive() {
        return vdrlReactive;
    }

    public ANCVisitRequest vdrlReactive(String vdrlReactive) {
        this.vdrlReactive = vdrlReactive;
        return this;
    }

    public String getVdrlTreatment() {
        return vdrlTreatment;
    }

    public ANCVisitRequest vdrlTreatment(String vdrlTreatment) {
        this.vdrlTreatment = vdrlTreatment;
        return this;
    }

    public String getDewormer() {
        return dewormer;
    }

    public ANCVisitRequest dewormer(String dewormer) {
        this.dewormer = dewormer;
        return this;
    }

    public String getPmtct() {
        return pmtct;
    }

    public ANCVisitRequest pmtct(String pmtct) {
        this.pmtct = pmtct;
        return this;
    }

    public String getPreTestCounseled() {
        return preTestCounseled;
    }

    public ANCVisitRequest preTestCounseled(String preTestCounseled) {
        this.preTestCounseled = preTestCounseled;
        return this;
    }

    public String getHivTestResult() {
        return hivTestResult;
    }

    public ANCVisitRequest hivTestResult(String hivTestResult) {
        this.hivTestResult = hivTestResult;
        return this;
    }

    public String getPostTestCounseled() {
        return postTestCounseled;
    }

    public ANCVisitRequest postTestCounseled(String postTestCounseled) {
        this.postTestCounseled = postTestCounseled;
        return this;
    }

    public String getPmtctTreament() {
        return pmtctTreament;
    }

    public ANCVisitRequest pmtctTreament(String pmtctTreament) {
        this.pmtctTreament = pmtctTreament;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public ANCVisitRequest location(String location) {
        this.location = location;
        return this;
    }

    public String getHouse() {
        return house;
    }

    public ANCVisitRequest house(String house) {
        this.house = house;
        return this;
    }

    public String getCommunity() {
        return community;
    }

    public ANCVisitRequest community(String community) {
        this.community = community;
        return this;
    }

    public String getReferred() {
        return referred;
    }

    public ANCVisitRequest referred(String referred) {
        this.referred = referred;
        return this;
    }

    public Boolean getMaleInvolved() {
        return maleInvolved;
    }

    public ANCVisitRequest maleInvolved(Boolean maleInvolved) {
        this.maleInvolved = maleInvolved;
        return this;
    }

    public Date getNextANCDate() {
        return nextANCDate;
    }

    public ANCVisitRequest nextANCDate(Date nextANCDate) {
        this.nextANCDate = nextANCDate;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public ANCVisitRequest comments(String comments) {
        this.comments = comments;
        return this;
    }

    public Patient getPatient() {
        return patient;
    }

    public ANCVisitRequest patient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public MRSUser getStaff() {
        return staff;
    }

    public ANCVisitRequest staff(MRSUser staff) {
        this.staff = staff;
        return this;
    }

    public Facility getFacility() {
        return facility;
    }
}
