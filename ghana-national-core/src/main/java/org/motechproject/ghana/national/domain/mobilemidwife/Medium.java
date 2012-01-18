package org.motechproject.ghana.national.domain.mobilemidwife;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

public enum Medium {
    SMS("SMS", Arrays.asList(PhoneOwnership.PERSONAL, PhoneOwnership.HOUSEHOLD)),
    VOICE("Voice", Arrays.asList(PhoneOwnership.PERSONAL, PhoneOwnership.PUBLIC, PhoneOwnership.HOUSEHOLD));
    private String displayName;
    private List<PhoneOwnership> phoneOwnerships;

    Medium(String displayName, List<PhoneOwnership> phoneOwnerships) {
        this.displayName = displayName;
        this.phoneOwnerships = phoneOwnerships;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue(){
        return name();
    }

    public static Medium value(String input) {
       return isNotEmpty(input) ? Medium.valueOf(input) : null;
    }

    public List<PhoneOwnership> getPhoneOwnerships() {
        return phoneOwnerships;
    }
}