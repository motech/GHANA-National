package org.motechproject.ghana.national.domain.mobilemidwife;

public enum ServiceType {
    PREGNANCY("Pregnancy", "Pregnancy Message"),
    CHILD_CARE("Child Care", "ChildCare Message");

    private String displayName;
    private String serviceName;

    ServiceType(String displayName, String serviceName) {
        this.displayName = displayName;
        this.serviceName = serviceName;
    }

    public String getValue() {
        return name();
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


