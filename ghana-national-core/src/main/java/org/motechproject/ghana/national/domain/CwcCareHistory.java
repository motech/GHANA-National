package org.motechproject.ghana.national.domain;

public enum CwcCareHistory {
    VITA_A("Vita A (Ch)"), IPTI("IPTi (Ch)"), BCG("BCG"), OPV("OPV"), PENTA("Penta"), MEASLES("Measles"),
    YF("YF"),ROTAVIRUS("ROTAVIRUS"),PNEUMOCOCCAL("PNEUMOCOCCAL");

    private String description;

    private CwcCareHistory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
