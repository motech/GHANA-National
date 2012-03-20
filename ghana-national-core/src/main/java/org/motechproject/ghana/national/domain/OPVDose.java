package org.motechproject.ghana.national.domain;

public enum OPVDose {
    OPV_0(0), OPV_1(1), OPV_2(2), OPV_3(3);

    private int dose;

    private OPVDose(int dose) {
        this.dose = dose;
    }

    public static OPVDose byValue(String value) {
        for (OPVDose dosage : values()) {
            if (Integer.toString(dosage.dose).equals(value)) return dosage;
        }
        return null;
    }

    public Integer value() {
        return dose;
    }
}
