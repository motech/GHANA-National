package org.ghana.national.domain;

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
    private String facilityId;
    @JsonProperty
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


}
