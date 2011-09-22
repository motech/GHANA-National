package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type === 'CallCenterAdmin'")
public class CallCenterAdmin extends User {
    @JsonProperty("type")
    final String type = CallCenterAdmin.class.getSimpleName();

    @Override
    @JsonIgnore
    public String getAuthority() {
        return type;
    }

    private CallCenterAdmin() {
    }

    public CallCenterAdmin(String username, String password) {
        super(username, password);
    }
}
