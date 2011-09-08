package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type === 'CALL_CENTER_ADMIN'")
public class CallCenterAdmin extends User {
    @JsonProperty("type")
    final String type = "CALL_CENTER_ADMIN";

    @Override
    protected String getAuthority() {
        return type;
    }

    private CallCenterAdmin() {
    }

    public CallCenterAdmin(String username) {
        super(username);
    }
}
