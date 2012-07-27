package org.motechproject.ghana.national.domain.ivr;

public enum VerboiceDialStatus {
    FAILED("failed"), BUSY("busy"), COMPLETED("completed");
    private String code;

    VerboiceDialStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
