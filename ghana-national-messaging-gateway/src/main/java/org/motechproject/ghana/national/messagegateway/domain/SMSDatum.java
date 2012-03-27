package org.motechproject.ghana.national.messagegateway.domain;

public class SMSDatum {
    private String windowName;
    private String milestone;
    private String firstName;
    private String lastName;
    private String motechId;
    private String serialNumber;

    public SMSDatum(String windowName, String milestone, String motechId, String serialNumber, String firstName, String lastName) {
        this.windowName = windowName;
        this.milestone = milestone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.motechId = motechId;
        this.serialNumber = serialNumber;
    }

    public String getWindowName() {
        return windowName;
    }

    public String getMilestone() {
        return milestone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMotechId() {
        return motechId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
}
