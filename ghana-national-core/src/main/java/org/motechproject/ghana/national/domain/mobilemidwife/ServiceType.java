package org.motechproject.ghana.national.domain.mobilemidwife;

public enum ServiceType {
    PREGNANCY("Pregnancy"),
    CHILD_CARE("Child Care");

    private String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getValue() {
        return name();
    }

    public String getServiceName(Medium medium) {
        return displayName + (Medium.SMS.equals(medium) ? " Text Message" : " Voice Message");
    }

    public String getDisplayName() {
        return displayName;
    }
}


