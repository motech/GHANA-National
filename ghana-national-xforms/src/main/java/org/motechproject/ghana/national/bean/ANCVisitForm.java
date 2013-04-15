package org.motechproject.ghana.national.bean;

import org.apache.xpath.operations.String;
import org.motechproject.ghana.national.validator.field.MotechId;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.annotations.MaxLength;
import org.motechproject.mobileforms.api.validator.annotations.RegEx;
import org.motechproject.mobileforms.api.validator.annotations.Required;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;

import java.util.Date;

import static org.motechproject.ghana.national.FormFieldRegExPatterns.MOTECH_ID_PATTERN;

public class ANCVisitForm extends FormBean {
    public static final String NUMERIC_OR_NOTAPPLICABLE_PATTERN = "([0-9]+(.[0-9]+)?|[nN][aA])";

    @Required
    @MaxLength(size = 50)
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String staffId;

    @Required
    @MaxLength(size = 50)
    @RegEx(pattern = NUMERIC_OR_NOTAPPLICABLE_PATTERN)
    private String facilityId;

    private Date date;

    @RegEx(pattern = MOTECH_ID_PATTERN)
    @MotechId(validator = MotechIdVerhoeffValidator.class)
    private String motechId;

    private String serialNumber;

    @Required
    private String visitNumber;

    private Date estDeliveryDate;

    private Integer bpSystolic;

    private Integer bpDiastolic;

    private Double weight;

    @Required
    private String ttdose;

    @Required
    private String iptdose;

    private Boolean iptReactive;

    @Required
    private String itnUse;


    private Double fht;

    private Integer fhr;

    private String urineTestProteinPositive;

    private String urineTestGlucosePositive;

    private Double hemoglobin;

    private String vdrlReactive;

    private String vdrlTreatment;

    @Required
    private String dewormer;

    @Required
    private String pmtct;

    private String preTestCounseled;

    private String hivTestResult;

    private String postTestCounseled;

    private String pmtctTreament;

    @Required
    private String location;

    private String house;

    private String community;

    @Required
    private String referred;

    @Required
    private Boolean maleInvolved;

    @Required
    private Date nextANCDate;

    private String comments;
    private Boolean visitor;

    private String vdltreatment;
    private String hivtreatment;
    private Integer gestationage;
    private String counseledonfp;
    private String births;

    public String getVdltreatment() {
        return vdltreatment;
    }

    public void setVdltreatment(String vdltreatment) {
        this.vdltreatment = vdltreatment;
    }

    public String getHivtreatment() {
        return hivtreatment;
    }

    public void setHivtreatment(String hivtreatment) {
        this.hivtreatment = hivtreatment;
    }

    public Integer getGestationage() {
        return gestationage;
    }

    public void setGestationage(Integer gestationage) {
        this.gestationage = gestationage;
    }

    public String getCounseledonfp() {
        return counseledonfp;
    }

    public void setCounseledonfp(String counseledonfp) {
        this.counseledonfp = counseledonfp;
    }

    public String getBirths() {
        return births;
    }

    public void setBirths(String births) {
        this.births = births;
    }


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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(String visitNumber) {
        this.visitNumber = visitNumber;
    }

    public Date getEstDeliveryDate() {
        return estDeliveryDate;
    }

    public void setEstDeliveryDate(Date estDeliveryDate) {
        this.estDeliveryDate = estDeliveryDate;
    }

    public Integer getBpSystolic() {
        return bpSystolic;
    }

    public void setBpSystolic(Integer bpSystolic) {
        this.bpSystolic = bpSystolic;
    }

    public Integer getBpDiastolic() {
        return bpDiastolic;
    }

    public void setBpDiastolic(Integer bpDiastolic) {
        this.bpDiastolic = bpDiastolic;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getTtdose() {
        return ttdose;
    }

    public void setTtdose(String ttdose) {
        this.ttdose = ttdose;
    }

    public String getIptdose() {
        return iptdose;
    }

    public void setIptdose(String iptdose) {
        this.iptdose = iptdose;
    }

    public Boolean getIptReactive() {
        return iptReactive;
    }

    public void setIptReactive(Boolean iptReactive) {
        this.iptReactive = iptReactive;
    }

    public String getItnUse() {
        return itnUse;
    }

    public void setItnUse(String itnUse) {
        this.itnUse = itnUse;
    }

    public Double getFht() {
        return fht;
    }

    public void setFht(Double fht) {
        this.fht = fht;
    }

    public Integer getFhr() {
        return fhr;
    }

    public void setFhr(Integer fhr) {
        this.fhr = fhr;
    }

    public String getUrineTestProteinPositive() {
        return urineTestProteinPositive;
    }

    public void setUrineTestProteinPositive(String urineTestProteinPositive) {
        this.urineTestProteinPositive = urineTestProteinPositive;
    }

    public String getUrineTestGlucosePositive() {
        return urineTestGlucosePositive;
    }

    public void setUrineTestGlucosePositive(String urineTestGlucosePositive) {
        this.urineTestGlucosePositive = urineTestGlucosePositive;
    }

    public Double getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(Double hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public String getVdrlReactive() {
        return vdrlReactive;
    }

    public void setVdrlReactive(String vdrlReactive) {
        this.vdrlReactive = vdrlReactive;
    }

    public String getVdrlTreatment() {
        return vdrlTreatment;
    }

    public void setVdrlTreatment(String vdrlTreatment) {
        this.vdrlTreatment = vdrlTreatment;
    }

    public String getDewormer() {
        return dewormer;
    }

    public void setDewormer(String dewormer) {
        this.dewormer = dewormer;
    }

    public String getPmtct() {
        return pmtct;
    }

    public void setPmtct(String pmtct) {
        this.pmtct = pmtct;
    }

    public String getPreTestCounseled() {
        return preTestCounseled;
    }

    public void setPreTestCounseled(String preTestCounseled) {
        this.preTestCounseled = preTestCounseled;
    }

    public String getHivTestResult() {
        return hivTestResult;
    }

    public void setHivTestResult(String hivTestResult) {
        this.hivTestResult = hivTestResult;
    }

    public String getPostTestCounseled() {
        return postTestCounseled;
    }

    public void setPostTestCounseled(String postTestCounseled) {
        this.postTestCounseled = postTestCounseled;
    }

    public String getPmtctTreament() {
        return pmtctTreament;
    }

    public void setPmtctTreament(String pmtctTreament) {
        this.pmtctTreament = pmtctTreament;
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

    public String getReferred() {
        return referred;
    }

    public void setReferred(String referred) {
        this.referred = referred;
    }

    public Boolean getMaleInvolved() {
        return maleInvolved;
    }

    public void setMaleInvolved(Boolean maleInvolved) {
        this.maleInvolved = maleInvolved;
    }

    public Date getNextANCDate() {
        return nextANCDate;
    }

    public void setNextANCDate(Date nextANCDate) {
        this.nextANCDate = nextANCDate;
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

    public Boolean getVisitor() {
        return visitor;
    }

    public void setVisitor(Boolean visitor) {
        this.visitor = visitor;
    }
}
