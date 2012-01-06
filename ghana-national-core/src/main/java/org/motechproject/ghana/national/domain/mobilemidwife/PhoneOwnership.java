package org.motechproject.ghana.national.domain.mobilemidwife;

import org.motechproject.ghana.national.domain.Displayable;

public enum PhoneOwnership implements Displayable {
    HOUSEHOLD("Household phone"), PERSONAL("Personal phone"), PUBLIC("Public phone");
    private String displayName;

    PhoneOwnership(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public String value(){
        return name();
    }
}


