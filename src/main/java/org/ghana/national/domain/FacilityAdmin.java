package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type === 'FacilityAdmin'")
public class FacilityAdmin extends User {
    @JsonProperty("type")
    final String type = FacilityAdmin.class.getSimpleName();

    @Override
    protected String getAuthority() {
        return type;
    }

    private FacilityAdmin() {
    }

    public FacilityAdmin(String username) {
        super(username);
    }
}
