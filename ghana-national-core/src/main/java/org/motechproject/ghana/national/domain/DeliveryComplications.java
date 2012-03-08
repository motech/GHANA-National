package org.motechproject.ghana.national.domain;

public enum DeliveryComplications implements MobileFormEnum{
    ECLAMPSIA("1"), PUERPERAL_INF_SEPSIS("2"), PPH("3"), RUPTURED_UTERUS("4"), CARDIAC_ARREST("5"), VVF("6"), DROP_FOOT("7"), PUERPERAL_PSYCHOSIS("8"), OTHER("9");
    private String numericValue;

    DeliveryComplications(String numericValue) {
        this.numericValue = numericValue;
    }

    @Override
    public String getValue() {
        return numericValue;
    }
}
