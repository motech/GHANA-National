package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type === 'SuperAdmin'")
public class SuperAdmin extends User {
    @JsonProperty("type")
    final String type = SuperAdmin.class.getSimpleName();

    private SuperAdmin() {
    }

    @Override
    @JsonIgnore
    public String getAuthority() {
        return type;
    }

    public SuperAdmin(String username, String password) {
        super(username, password);
    }
}
