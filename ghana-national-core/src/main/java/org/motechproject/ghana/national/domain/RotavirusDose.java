package org.motechproject.ghana.national.domain;

public enum RotavirusDose {
    ROTAVIRUS1(1, "Rotavirus1"), ROTAVIRUS2(2, "Rotavirus2");

    public Integer getDose() {
        return dose;
    }

    private Integer dose;
    private String milestone;

    RotavirusDose(Integer dose, String milestone) {
        this.dose = dose;
        this.milestone = milestone;
    }
    
    public static RotavirusDose byValue(Integer dose) {
        for (RotavirusDose rotavirusDose : values()) {
            if (rotavirusDose.dose.equals(dose))
                return rotavirusDose;
        }
        return null;
    }

    public String milestoneName() {
        return milestone;
    }
}
