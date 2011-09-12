package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type === 'SuperAdmin'")
public class SuperAdmin extends User {
    @JsonProperty("type")
    final String type = SuperAdmin.class.getSimpleName();

    private SuperAdmin() {
    }

    @Override
    protected String getAuthority() {
        return type;
    }

    public SuperAdmin(String username) {
        super(username);
    }
}
