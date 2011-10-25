package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.List;

import static java.util.Arrays.asList;


@TypeDiscriminator("doc.type === 'UserType'")
public class UserType extends MotechAuditableDataObject {
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

    public enum Role {
        SUPER_ADMIN("Super Admin", Constants.SECURITY_ROLE_SUPER),
        FACILITY_ADMIN("Facility Admin", Constants.SECURITY_ROLE_PROVIDER),
        CALL_CENTER_ADMIN("CallCenter Admin", Constants.SECURITY_ROLE_PROVIDER),
        HEALTH_CARE_ADMIN("HeathCare Admin", Constants.SECURITY_ROLE_PROVIDER),
        HEALTH_PROMOTION_OFFICER("HPO", Constants.SECURITY_ROLE_PROVIDER),
        COMMUNITY_HEALTH_OPERATOR("CHO", Constants.SECURITY_ROLE_PROVIDER),
        COMMUNITY_HEALTH_NURSE("CHN", Constants.SECURITY_ROLE_PROVIDER),
        COMMUNITY_HEALTH_VOLUNTEER("CHV", Constants.SECURITY_ROLE_PROVIDER),
        FIELD_AGENT("FA", Constants.SECURITY_ROLE_PROVIDER),
        MOBILE_MIDWIFE("MM", Constants.SECURITY_ROLE_PROVIDER);

        private String key;
        private String securityRole;

        Role(String key, String securityRole) {
            this.key = key;
            this.securityRole = securityRole;
        }

        public static String securityRoleFor(String key) {
            for (Role role : values())
                if (role.key.equalsIgnoreCase(key)) return role.securityRole;
            return null;
        }

        public static Boolean isAdmin(String roleName) {
            List<String> allAdmins = asList(SUPER_ADMIN.key(), FACILITY_ADMIN.key(), CALL_CENTER_ADMIN.key(), HEALTH_CARE_ADMIN.key());
            return allAdmins.contains(roleName);
        }

        public String key() {
            return key;
        }
    }
}

