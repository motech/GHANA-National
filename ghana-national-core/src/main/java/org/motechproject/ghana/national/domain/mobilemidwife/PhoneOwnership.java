package org.motechproject.ghana.national.domain.mobilemidwife;

public enum PhoneOwnership{
    HOUSEHOLD("Household phone"), PERSONAL("Personal phone"), PUBLIC("Public phone");
    private String displayName;

    PhoneOwnership(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue(){
        return name();
    }
}


