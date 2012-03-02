package org.motechproject.ghana.national.domain;

public enum ChildDeliveryOutcome implements MobileFormEnum{
    SINGLETON("1"), TWINS("2"), TRIPLETS("3");

    private String numericValue;

    ChildDeliveryOutcome(String numericValue) {
        this.numericValue = numericValue;
    }

    @Override
    public String getNumericValue() {
        return numericValue;
    }
}
