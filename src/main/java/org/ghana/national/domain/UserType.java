package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;


@TypeDiscriminator("doc.type === 'UserType'")
public class UserType extends MotechAuditableDataObject {
    public static final String SUPER_ADMIN = "Super Admin";
    public static final String FACILITY_ADMIN = "Facility Admin";
    public static final String CALL_CENTER_ADMIN = "CallCenter Admin";
    public static final String HEALTH_CARE_ADMIN = "HeathCare Admin";
    public static final String HEALTH_PROMOTION_OFFICER = "HPO";
    public static final String COMMUNITY_HEALTH_OPERATOR = "CHO";
    public static final String COMMUNITY_HEALTH_NURSE = "CHN";
    public static final String COMMUNITY_HEALTH_VOLUNTEER = "CHV";
    public static final String FIELD_AGENT = "FA";
    public static final String MOBILE_MIDWIFE = "MM";

    @JsonProperty("type")
    private String type = "UserType";
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;

    public UserType() {
    }

    public UserType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String name() {
        return name;
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

