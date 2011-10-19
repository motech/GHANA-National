package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'Facility'")
public class Facility extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "Facility";
    private Integer phoneNumber;
    private Integer additionalPhoneNumber1;
    private Integer additionalPhoneNumber2;
    private Integer additionalPhoneNumber3;
    private String facilityId;
    private Integer motechFacilityId;

    public Facility() {
    }

    public Facility(Integer phoneNumber, Integer additionalPhoneNumber1, Integer additionalPhoneNumber2,
                    Integer additionalPhoneNumber3, String facilityId, Integer motechFacilityId) {
        this.phoneNumber = phoneNumber;
        this.additionalPhoneNumber1 = additionalPhoneNumber1;
        this.additionalPhoneNumber2 = additionalPhoneNumber2;
        this.additionalPhoneNumber3 = additionalPhoneNumber3;
        this.facilityId = facilityId;
        this.motechFacilityId = motechFacilityId;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getAdditionalPhoneNumber1() {
        return additionalPhoneNumber1;
    }

    public void setAdditionalPhoneNumber1(Integer additionalPhoneNumber1) {
        this.additionalPhoneNumber1 = additionalPhoneNumber1;
    }

    public Integer getAdditionalPhoneNumber2() {
        return additionalPhoneNumber2;
    }

    public void setAdditionalPhoneNumber2(Integer additionalPhoneNumber2) {
        this.additionalPhoneNumber2 = additionalPhoneNumber2;
    }

    public Integer getAdditionalPhoneNumber3() {
        return additionalPhoneNumber3;
    }

    public void setAdditionalPhoneNumber3(Integer additionalPhoneNumber3) {
        this.additionalPhoneNumber3 = additionalPhoneNumber3;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public Integer getMotechFacilityId() {
        return motechFacilityId;
    }

    public void setMotechFacilityId(Integer motechFacilityId) {
        this.motechFacilityId = motechFacilityId;
    }
}
