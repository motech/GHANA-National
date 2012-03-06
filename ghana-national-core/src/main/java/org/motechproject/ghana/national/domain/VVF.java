package org.motechproject.ghana.national.domain;

public enum VVF implements MobileFormEnum{
    REFERRED("1"), REPAIRED("2");
    private String numericValue;

    VVF(String numericValue) {
        this.numericValue = numericValue;
    }

    @Override
    public String getValue() {
        return numericValue;
    }
}
