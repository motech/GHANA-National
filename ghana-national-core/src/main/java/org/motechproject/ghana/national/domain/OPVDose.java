package org.motechproject.ghana.national.domain;

public enum OPVDose {
    OPV_0(0,"OPV0"), OPV_1(1,"OPV1"), OPV_2(2,"OPV2"), OPV_3(3,"OPV3");

    private int dose;
    private String milestone;

    private OPVDose(int dose) {
        this.dose = dose;
    }

    OPVDose(int dose, String milestone) {
        this.dose = dose;
        this.milestone = milestone;
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
    
    public String milestoneName(){
        return milestone;
    }
}
