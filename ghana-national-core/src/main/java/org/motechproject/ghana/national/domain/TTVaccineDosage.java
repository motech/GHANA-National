package org.motechproject.ghana.national.domain;

public enum TTVaccineDosage {
    TT1(1), TT2(2), TT3(3), TT4(4), TT5(5);

    private Integer dosage;

    TTVaccineDosage(Integer dosage) {
        this.dosage = dosage;
    }

    public Integer getDosage() {
        return dosage;
    }

    public Double getDosageAsDouble() {
        return (double) dosage;
    }

    public String getScheduleMilestoneName(){
        return name();
    }

    public static TTVaccineDosage byValue(int value){
        return valueOf("TT" + String.valueOf(value));
    }
}
