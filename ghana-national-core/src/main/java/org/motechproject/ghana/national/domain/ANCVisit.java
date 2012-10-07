package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.mrs.model.MRSUser;

import java.util.Date;

@TypeDiscriminator("doc.type === 'ANCVisit'")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ANCVisit extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "ANCVisit";
    @JsonProperty
    private Date date;
    @JsonProperty
    private String serialNumber;
    @JsonProperty
    private String visitNumber;
    @JsonProperty
    private Date estDeliveryDate;
    @JsonProperty
    private Integer bpSystolic;
    @JsonProperty
    private Integer bpDiastolic;
    @JsonProperty
    private Double weight;
    @JsonProperty
    private String ttdose;
    @JsonProperty
    private String iptdose;
    @JsonProperty
    private Boolean iptReactive;
    @JsonProperty
    private String itnUse;
    @JsonProperty
    private Double fht;
    @JsonProperty
    private Integer fhr;
    @JsonProperty
    private String urineTestProteinPositive;
    @JsonProperty
    private String urineTestGlucosePositive;
    @JsonProperty
    private Double hemoglobin;
    @JsonProperty
    private String vdrlReactive;
    @JsonProperty
    private String vdrlTreatment;
    @JsonProperty
    private String dewormer;
    @JsonProperty
    private String pmtct;
    @JsonProperty
    private String preTestCounseled;
    @JsonProperty
    private String hivTestResult;
    @JsonProperty
    private String postTestCounseled;
    @JsonProperty
    private String pmtctTreament;
    @JsonProperty
    private String location;
    @JsonProperty
    private String house;
    @JsonProperty
    private String community;
    @JsonProperty
    private String referred;
    @JsonProperty
    private Boolean maleInvolved;
    @JsonProperty
    private Date nextANCDate;
    @JsonProperty
    private String comments;
    @JsonIgnore
    private Boolean visitor;
    @JsonIgnore
    private Patient patient;
    @JsonIgnore
    private MRSUser staff;
    @JsonIgnore
    private Facility facility;
    @JsonProperty
    private String staffId;
    @JsonProperty
    private String facilityId;

    public ANCVisit facility(Facility facility) {
        this.facility = facility;
        this.facilityId = facility.mrsFacilityId();
        return this;
    }

    public Date getDate() {
        return date;
    }

    public ANCVisit date(Date date) {
        this.date = date;
        return this;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public ANCVisit serialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public String getVisitNumber() {
        return visitNumber;
    }

    public ANCVisit visitNumber(String visitNumber) {
        this.visitNumber = visitNumber;
        return this;
    }

    public Date getEstDeliveryDate() {
        return estDeliveryDate;
    }

    public ANCVisit estDeliveryDate(Date estDeliveryDate) {
        this.estDeliveryDate = estDeliveryDate;
        return this;
    }

    public Integer getBpSystolic() {
        return bpSystolic;
    }

    public ANCVisit bpSystolic(Integer bpSystolic) {
        this.bpSystolic = bpSystolic;
        return this;
    }

    public Integer getBpDiastolic() {
        return bpDiastolic;
    }

    public ANCVisit bpDiastolic(Integer bpDiastolic) {
        this.bpDiastolic = bpDiastolic;
        return this;
    }

    public Double getWeight() {
        return weight;
    }

    public ANCVisit weight(Double weight) {
        this.weight = weight;
        return this;
    }

    public String getTtdose() {
        return ttdose;
    }

    public ANCVisit ttdose(String ttdose) {
        this.ttdose = ttdose;
        return this;
    }

    public String getIptdose() {
        return iptdose;
    }

    public ANCVisit iptdose(String iptdose) {
        this.iptdose = iptdose;
        return this;
    }

    public Boolean getIptReactive() {
        return iptReactive;
    }

    public ANCVisit iptReactive(Boolean iptReactive) {
        this.iptReactive = iptReactive;
        return this;
    }

    public String getItnUse() {
        return itnUse;
    }

    public ANCVisit itnUse(String itnUse) {
        this.itnUse = itnUse;
        return this;
    }

    public Double getFht() {
        return fht;
    }

    public ANCVisit fht(Double fht) {
        this.fht = fht;
        return this;
    }

    public Integer getFhr() {
        return fhr;
    }

    public ANCVisit fhr(Integer fhr) {
        this.fhr = fhr;
        return this;
    }

    public String getUrineTestProteinPositive() {
        return urineTestProteinPositive;
    }

    public ANCVisit urineTestProteinPositive(String urineTestProteinPositive) {
        this.urineTestProteinPositive = urineTestProteinPositive;
        return this;
    }

    public String getUrineTestGlucosePositive() {
        return urineTestGlucosePositive;
    }

    public ANCVisit urineTestGlucosePositive(String urineTestGlucosePositive) {
        this.urineTestGlucosePositive = urineTestGlucosePositive;
        return this;
    }

    public Double getHemoglobin() {
        return hemoglobin;
    }

    public ANCVisit hemoglobin(Double hemoglobin) {
        this.hemoglobin = hemoglobin;
        return this;
    }

    public String getVdrlReactive() {
        return vdrlReactive;
    }

    public ANCVisit vdrlReactive(String vdrlReactive) {
        this.vdrlReactive = vdrlReactive;
        return this;
    }

    public String getVdrlTreatment() {
        return vdrlTreatment;
    }

    public ANCVisit vdrlTreatment(String vdrlTreatment) {
        this.vdrlTreatment = vdrlTreatment;
        return this;
    }

    public String getDewormer() {
        return dewormer;
    }

    public ANCVisit dewormer(String dewormer) {
        this.dewormer = dewormer;
        return this;
    }

    public String getPmtct() {
        return pmtct;
    }

    public ANCVisit pmtct(String pmtct) {
        this.pmtct = pmtct;
        return this;
    }

    public String getPreTestCounseled() {
        return preTestCounseled;
    }

    public ANCVisit preTestCounseled(String preTestCounseled) {
        this.preTestCounseled = preTestCounseled;
        return this;
    }

    public String getHivTestResult() {
        return hivTestResult;
    }

    public ANCVisit hivTestResult(String hivTestResult) {
        this.hivTestResult = hivTestResult;
        return this;
    }

    public String getPostTestCounseled() {
        return postTestCounseled;
    }

    public ANCVisit postTestCounseled(String postTestCounseled) {
        this.postTestCounseled = postTestCounseled;
        return this;
    }

    public String getPmtctTreament() {
        return pmtctTreament;
    }

    public ANCVisit pmtctTreament(String pmtctTreament) {
        this.pmtctTreament = pmtctTreament;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public ANCVisit location(String location) {
        this.location = location;
        return this;
    }

    public String getHouse() {
        return house;
    }

    public ANCVisit house(String house) {
        this.house = house;
        return this;
    }

    public String getCommunity() {
        return community;
    }

    public ANCVisit community(String community) {
        this.community = community;
        return this;
    }

    public String getReferred() {
        return referred;
    }

    public ANCVisit referred(String referred) {
        this.referred = referred;
        return this;
    }

    public Boolean getMaleInvolved() {
        return maleInvolved;
    }

    public ANCVisit maleInvolved(Boolean maleInvolved) {
        this.maleInvolved = maleInvolved;
        return this;
    }

    public Date getNextANCDate() {
        return nextANCDate;
    }

    public ANCVisit nextANCDate(Date nextANCDate) {
        this.nextANCDate = nextANCDate;
        return this;
    }

    public String getComments() {
        return comments;
    }

    public ANCVisit comments(String comments) {
        this.comments = comments;
        return this;
    }

    public Patient getPatient() {
        return patient;
    }

    public ANCVisit patient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public MRSUser getStaff() {
        return staff;
    }

    public ANCVisit staff(MRSUser staff) {
        this.staff = staff;
        this.staffId = staff.getSystemId();
        return this;
    }

    public Facility getFacility() {
        return facility;
    }

    public Boolean getVisitor() {
        return visitor;
    }

    public ANCVisit visitor(Boolean visitor) {
        this.visitor = visitor;
        return this;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getFacilityId() {
        return facilityId;
    }
}
