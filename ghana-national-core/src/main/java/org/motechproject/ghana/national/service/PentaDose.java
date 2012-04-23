package org.motechproject.ghana.national.service;

public enum PentaDose {
    PENTA1(1, "Penta1"), PENTA2(2, "Penta2"), PENTA3(3, "Penta3");
    private Integer dose;
    private String milestone;

    PentaDose(Integer dose, String milestone) {
        this.dose = dose;
        this.milestone = milestone;
    }
    
    public static PentaDose byValue(Integer dose) {
        for (PentaDose pentaDose : values()) {
            if (pentaDose.dose.equals(dose))
                return pentaDose;
        }
        return null;
    }

    public String milestoneName() {
        return milestone;
    }
}
