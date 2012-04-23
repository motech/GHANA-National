package org.motechproject.ghana.national.service;

public enum IPTiDose {
    IPTi1(1, "IPTi1"), IPTi2(2, "IPTi2"), IPTi3(3, "IPTi3");
    private Integer dose;
    private String milestone;

    IPTiDose(Integer dose, String milestone) {
        this.dose = dose;
        this.milestone = milestone;
    }
    
    public Integer getDose(IPTiDose dose){
        for (IPTiDose iptiDose : values()) {
            if (iptiDose.milestone.equals(dose.milestoneName()))
                return this.dose;
        }
        return null;
    }
    
    public static IPTiDose byValue(Integer dose) {
        for (IPTiDose iptiDose : values()) {
            if (iptiDose.dose.equals(dose))
                return iptiDose;
        }
        return null;
    }

    public String milestoneName() {
        return milestone;
    }
}
