package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'UserType'")
public class UserType extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "UserType";
    private String name;
    private String description;

    public UserType() {
    }

    public UserType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserType userType = (UserType) o;
        if (name != null ? !name.equals(userType.name) : userType.name != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

