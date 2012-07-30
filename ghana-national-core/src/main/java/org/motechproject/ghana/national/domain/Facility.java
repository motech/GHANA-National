package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.mrs.model.MRSFacility;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.ghana.national.tools.Utility.emptyToNull;

@TypeDiscriminator("doc.type === 'Facility'")
public class Facility extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "Facility";
    @JsonProperty
    private String phoneNumber;
    @JsonProperty
    private String additionalPhoneNumber1;
    @JsonProperty
    private String additionalPhoneNumber2;
    @JsonProperty
    private String additionalPhoneNumber3;
    @JsonProperty
    private String mrsFacilityId;
    @JsonProperty
    private String motechId;

    @JsonIgnore
    private MRSFacility mrsFacility;

    public Facility() {
    }

    public Facility(MRSFacility mrsFacility) {
        this.mrsFacility = mrsFacility;
    }

    public Facility mrsFacility(MRSFacility mrsFacility) {
        this.mrsFacility = mrsFacility;
        return this;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public Facility phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String additionalPhoneNumber1() {
        return additionalPhoneNumber1;
    }

    public Facility additionalPhoneNumber1(String additionalPhoneNumber1) {
        this.additionalPhoneNumber1 = additionalPhoneNumber1;
        return this;
    }

    public String additionalPhoneNumber2() {
        return additionalPhoneNumber2;
    }

    public Facility additionalPhoneNumber2(String additionalPhoneNumber2) {
        this.additionalPhoneNumber2 = additionalPhoneNumber2;
        return this;
    }

    public String additionalPhoneNumber3() {
        return additionalPhoneNumber3;
    }

    public Facility additionalPhoneNumber3(String additionalPhoneNumber3) {
        this.additionalPhoneNumber3 = additionalPhoneNumber3;
        return this;
    }

    public MRSFacility mrsFacility() {
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

    public String mrsFacilityId() {
        return mrsFacilityId;
    }

    public Facility mrsFacilityId(String mrsFacilityId) {
        this.mrsFacilityId = mrsFacilityId;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String motechId() {
        return motechId;
    }

    public Facility motechId(String motechId) {
        this.motechId = motechId;
        return this;
    }

    public String getAdditionalPhoneNumber1() {
        return additionalPhoneNumber1;
    }

    public String getAdditionalPhoneNumber2() {
        return additionalPhoneNumber2;
    }

    public String getAdditionalPhoneNumber3() {
        return additionalPhoneNumber3;
    }

    public String getMrsFacilityId() {
        return mrsFacilityId;
    }

    public String getMotechId() {
        return motechId;
    }

    public List<String> findAllPhoneNumbers() {
        List<String> phoneNumbers = new ArrayList<String>();
        if (emptyToNull(phoneNumber) != null) {
            phoneNumbers.add(phoneNumber);
        }
        if (emptyToNull(additionalPhoneNumber1) != null) {
            phoneNumbers.add(additionalPhoneNumber1);
        }
        if (emptyToNull(additionalPhoneNumber2) != null) {
            phoneNumbers.add(additionalPhoneNumber2);
        }
        if (emptyToNull(additionalPhoneNumber3) != null) {
            phoneNumbers.add(additionalPhoneNumber3);
        }
        return phoneNumbers;
    }
}

