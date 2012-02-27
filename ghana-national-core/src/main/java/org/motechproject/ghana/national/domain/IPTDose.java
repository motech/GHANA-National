package org.motechproject.ghana.national.domain;

public enum IPTDose {
    SP1(1), SP2(2), SP3(3);
    private int dose;

    IPTDose(int dose) {
        this.dose = dose;
    }

    public static IPTDose byValue(String value) {
        for (IPTDose dosage : values()) {
            if (Integer.toString(dosage.dose).equals(value)) return dosage;
        }
        return null;
    }

    public Integer value() {
        return dose;
    }
}
