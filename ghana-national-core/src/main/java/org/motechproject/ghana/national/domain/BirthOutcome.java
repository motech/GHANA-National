package org.motechproject.ghana.national.domain;

public enum BirthOutcome implements MobileFormEnum {
    ALIVE("A"), FRESH_STILL_BIRTH("FSB"), MACERATED_STILL_BIRTH("MSB");
    private String value;

    BirthOutcome(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
