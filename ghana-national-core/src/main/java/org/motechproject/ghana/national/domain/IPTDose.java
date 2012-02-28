package org.motechproject.ghana.national.domain;

public enum IPTDose {
    SP1(1, "IPT1"), SP2(2, "IPT2"), SP3(3, "IPT3");
    private int dose;
    private String milestone;

    IPTDose(int dose, String milestone) {
         this.dose = dose;
        this.milestone = milestone;
    }

    public static IPTDose byValue(String value) {
        for (IPTDose dosage : values()) {
            if (Integer.toString(dosage.dose).equals(value)) return dosage;
        }
        return null;
    }

    public String milestone() {
        return milestone;
    }

    public Integer value() {
        return dose;
    }
}
