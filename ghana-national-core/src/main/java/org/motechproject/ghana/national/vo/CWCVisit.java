package org.motechproject.ghana.national.vo;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mrs.model.MRSUser;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CWCVisit {

    private MRSUser staff;
    private Facility facility;
    private Date date;
    private Patient patient;
    private String serialNumber;
    private List<String> immunizations;
    private String opvdose;
    private String pentadose;
    private String iptidose;
    private String rotavirusdose;
    private Double weight;
    private Double muac;
    private Double height;
    private Boolean maleInvolved;
    private String cwcLocation;
    private String house;
    private String community;
    private String comments;

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

    public List<String> getImmunizations() {
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
}