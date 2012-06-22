package org.motechproject.ghana.national.domain;

public enum PneumococcalDose {
    PNEUMO1(1, "Pneumo1"), PNEUMO2(2, "Pneumo2"),PNEUMO3(3, "Pneumo3");

    public Integer getDose() {
        return dose;
    }

    private Integer dose;
    private String milestone;

    PneumococcalDose(Integer dose, String milestone) {
        this.dose = dose;
        this.milestone = milestone;
    }
    
    public static PneumococcalDose byValue(Integer dose) {
        for (PneumococcalDose pneumococcalDose : values()) {
            if (pneumococcalDose.dose.equals(dose))
                return pneumococcalDose;
        }
        return null;
    }

    public String milestoneName() {
        return milestone;
    }
}
