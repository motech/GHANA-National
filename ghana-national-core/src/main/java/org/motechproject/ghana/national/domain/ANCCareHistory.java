package org.motechproject.ghana.national.domain;

public enum ANCCareHistory {
    IPT_SP("IPT_SP"), TT("TT"), HEMOGLOBIN("HEMOGLOBIN"), VITA("Mother Vitamin A"),IRON_OR_FOLATE("Iron or Folate Level"),
    SYPHILIS("SYPHILIS TEST (VDRL)"),MALARIA_RAPID_TEST("MALARIA RAPID TEST"), DIARRHEA("DIARRHEA"), PNEUMOCOCCAL_A("PNEUMOCOCCAL");

    private String description;

    private ANCCareHistory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
