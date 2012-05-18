package org.motechproject.ghana.national.domain.mobilemidwife;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public enum Medium {
    SMS("SMSPayload", asList(PhoneOwnership.PERSONAL, PhoneOwnership.HOUSEHOLD), new HashSet<String>(asList(Medium.TEXT_SMS_MEDIUM))),
    VOICE("Voice", asList(PhoneOwnership.PERSONAL, PhoneOwnership.PUBLIC, PhoneOwnership.HOUSEHOLD));
    private String displayName;
    private List<PhoneOwnership> phoneOwnerships;
    private Set<String> keys;

    public static final String TEXT_SMS_MEDIUM = "TEXT";

    Medium(String displayName, List<PhoneOwnership> phoneOwnerships) {
        this.displayName = displayName;
        this.phoneOwnerships = phoneOwnerships;
    }

    Medium(String displayName, List<PhoneOwnership> phoneOwnerships, Set<String> keysToFindMedium) {
        this.displayName = displayName;
        this.phoneOwnerships = phoneOwnerships;
        this.keys = keysToFindMedium;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return name();
    }

    public boolean hasKey(String key) {
        return keys != null && keys.contains(key);
    }

    public static Medium get(String input) {
        if (isNotEmpty(input)) {
            for (Medium medium : values()) {
                if (medium.name().equals(input) || medium.hasKey(input)) return medium;
            }
            throw new IllegalArgumentException("Invalid value for enum medium - " + input);
        }
        return null;
    }

    public List<PhoneOwnership> getPhoneOwnerships() {
        return phoneOwnerships;
    }
}