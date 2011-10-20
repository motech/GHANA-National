package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'Facility'")
public class Facility extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "Facility";
    @JsonProperty
    private Integer phoneNumber;
    @JsonProperty
    private Integer additionalPhoneNumber1;
    @JsonProperty
    private Integer additionalPhoneNumber2;
    @JsonProperty
    private Integer additionalPhoneNumber3;
    @JsonProperty
    private Integer mrsFacilityId;

    @JsonIgnore
    private org.motechproject.mrs.model.Facility mrsFacility;

    public Facility() {
    }

    public Facility(org.motechproject.mrs.model.Facility mrsFacility) {
        this.mrsFacility = mrsFacility;
    }

    public Facility mrsFacility(org.motechproject.mrs.model.Facility mrsFacility) {
        this.mrsFacility = mrsFacility;
        return this;
    }

    public Integer phoneNumber() {
        return phoneNumber;
    }

    public Facility phoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Integer additionalPhoneNumber1() {
        return additionalPhoneNumber1;
    }

    public Facility additionalPhoneNumber1(Integer additionalPhoneNumber1) {
        this.additionalPhoneNumber1 = additionalPhoneNumber1;
        return this;
    }

    public Integer additionalPhoneNumber2() {
        return additionalPhoneNumber2;
    }

    public Facility additionalPhoneNumber2(Integer additionalPhoneNumber2) {
        this.additionalPhoneNumber2 = additionalPhoneNumber2;
        return this;
    }

    public Integer additionalPhoneNumber3() {
        return additionalPhoneNumber3;
    }

    public Facility additionalPhoneNumber3(Integer additionalPhoneNumber3) {
        this.additionalPhoneNumber3 = additionalPhoneNumber3;
        return this;
    }

    public org.motechproject.mrs.model.Facility mrsFacility() {
        return mrsFacility;
    }

    public String name() {
        return mrsFacility.getName();
    }

    public String country() {
        return mrsFacility.getCountry();
    }

    public String region() {
        return mrsFacility.getRegion();
    }

    public String province() {
        return mrsFacility.getStateProvince();
    }

    public String district() {
        return mrsFacility.getCountyDistrict();
    }

    public Integer mrsFacilityId() {
        return mrsFacilityId;
    }

    public Facility mrsFacilityId(Integer mrsFacilityId) {
        this.mrsFacilityId = mrsFacilityId;
        return this;
    }
}

