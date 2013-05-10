package org.motechproject.ghana.national.domain;

public enum ANCCareHistory {

    IPT_SP("IPT_SP"), TT("TETANUS TOXOID DOSE"), HEMOGLOBIN("HEMOGLOBIN"), VITA("VITAMIN A"),IRON_OR_FOLATE("IRON OR FOLATE LEVEL"), SYPHILIS("SYPHILIS TEST (VDRL"),
    MALARIA_RAPID_TEST("MALARIA RAPID TEST"), DIARRHEA("DIARRHEA"), PNEUMOCOCCAL("PNEUMOCOCCAL");

    private String description;

    private ANCCareHistory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
