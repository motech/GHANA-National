package org.motechproject.ghana.national.tools.seed.data.source;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.List;

@TypeDiscriminator("doc.type === 'Community'")
public class Community extends MotechAuditableDataObject {

    @JsonProperty("type")
    private String type = "Community";
    @JsonProperty
    private String id;
    // community id is a duplication, But stored just in case for the migration
    @JsonProperty
    private String communityId;
    @JsonProperty
    private String communityName;
    @JsonProperty
    private Integer facilityId;
    @JsonProperty
    private String facilityName;
    @JsonProperty
    private int retired;

    public Community id(String id) {
        this.id = id;
        return this;
    }

    // community id is a duplication, But stored just in case for the migration
    public Community communityId(String communityId) {
        this.communityId = communityId;
        return this;
    }

    public Community communityName(String communityName) {
        this.communityName = communityName;
        return this;
    }

    public Community facilityId(Integer facilityId) {
        this.facilityId = facilityId;
        return this;
    }

    public Community retired(int retired) {
        this.retired = retired;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getCommunityId() {
        return communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public Integer getFacilityId() {
        return facilityId;
    }

    public int getRetired() {
        return retired;
    }

    public Community facilityName(String facilityName) {
        this.facilityName = facilityName;
        return this;
    }
}
