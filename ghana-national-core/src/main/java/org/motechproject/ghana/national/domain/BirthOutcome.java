package org.motechproject.ghana.national.domain;

public enum BirthOutcome implements MobileFormEnum{
    ALIVE("1"), FRESH_STILL_BIRTH("2"), MACERATED_STILL_BIRTH("3");
    private String numericValue;

    BirthOutcome(String numericValue) {
        this.numericValue = numericValue;
    }

    @Override
    public String getNumericValue() {
        return numericValue;
    }
}
