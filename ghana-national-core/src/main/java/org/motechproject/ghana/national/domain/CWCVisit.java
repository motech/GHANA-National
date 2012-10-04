package org.motechproject.ghana.national.domain;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.mrs.model.MRSUser;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@TypeDiscriminator("doc.type === 'CWCVisit'")
public class CWCVisit extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "CWCVisit";
    @JsonIgnore
    private MRSUser staff;
    @JsonIgnore
    private Facility facility;
    @JsonProperty
    private Date date;
    @JsonIgnore
    private Patient patient;
    @JsonProperty
    private String serialNumber;
    @JsonProperty
    private List<String> immunizations;
    @JsonProperty
    private String opvdose;
    @JsonProperty
    private String pentadose;
    @JsonProperty
    private String iptidose;
    @JsonProperty
    private String rotavirusdose;
    @JsonProperty
    private String pneumococcaldose;
    @JsonProperty
    private Double weight;
    @JsonProperty
    private Double muac;
    @JsonProperty
    private Double height;
    @JsonProperty
    private Boolean maleInvolved;
    @JsonProperty
    private String cwcLocation;
    @JsonProperty
    private String house;
    @JsonProperty
    private String community;
    @JsonProperty
    private String comments;
    @JsonIgnore
    private Boolean visitor;


    public MRSUser getStaff() {
        return staff;
    }

    public Facility getFacility() {
        return facility;
    }

    public Date getDate() {
        return date;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public List getImmunizations() {
        return CollectionUtils.isEmpty(immunizations) ? Collections.EMPTY_LIST : immunizations;
    }

    public String getOpvdose() {
        return opvdose;
    }

    public String getPentadose() {
        return pentadose;
    }

    public String getIptidose() {
        return iptidose;
    }


    public String getRotavirusdose() {
        return rotavirusdose;
    }

    public CWCVisit rotavirusdose(String rotavirusdose) {
        this.rotavirusdose = rotavirusdose;
        return this;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getMuac() {
        return muac;
    }

    public Double getHeight() {
        return height;
    }

    public Boolean getMaleInvolved() {
        return maleInvolved;
    }

    public String getCwcLocation() {
        return cwcLocation;
    }

    public String getHouse() {
        return house;
    }

    public String getCommunity() {
        return community;
    }

    public String getComments() {
        return comments;
    }

    public CWCVisit staff(MRSUser staff) {
        this.staff = staff;
        return this;
    }

    public CWCVisit facility(Facility facility) {
        this.facility = facility;
        return this;
    }

    public CWCVisit date(Date date) {
        this.date = date;
        return this;
    }

    public CWCVisit patient(Patient mrsPatient) {
        this.patient = mrsPatient;
        return this;
    }

    public CWCVisit serialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public CWCVisit immunizations(List<String> immunizations) {
        this.immunizations = immunizations;
        return this;
    }

    public CWCVisit opvdose(String opvdose) {
        this.opvdose = opvdose;
        return this;
    }

    public CWCVisit pentadose(String pentadose) {
        this.pentadose = pentadose;
        return this;
    }

    public CWCVisit iptidose(String iptidose) {
        this.iptidose = iptidose;
        return this;
    }

    public CWCVisit weight(Double weight) {
        this.weight = weight;
        return this;
    }

    public CWCVisit muac(Double muac) {
        this.muac = muac;
        return this;
    }

    public CWCVisit height(Double height) {
        this.height = height;
        return this;
    }

    public CWCVisit maleInvolved(Boolean maleInvolved) {
        this.maleInvolved = maleInvolved;
        return this;
    }

    public CWCVisit cwcLocation(String cwcLocation) {
        this.cwcLocation = cwcLocation;
        return this;
    }

    public CWCVisit house(String house) {
        this.house = house;
        return this;
    }

    public CWCVisit community(String community) {
        this.community = community;
        return this;
    }

    public CWCVisit comments(String comments) {
        this.comments = comments;
        return this;
    }

    public String getPneumococcaldose() {
        return pneumococcaldose;
    }

    public CWCVisit pneumococcaldose(String pneumococcaldose) {
        this.pneumococcaldose = pneumococcaldose;
        return this;
    }

    public Boolean getVisitor() {
        return visitor;
    }

    public CWCVisit visitor(Boolean visitor) {
        this.visitor = visitor;
        return this;
    }
}