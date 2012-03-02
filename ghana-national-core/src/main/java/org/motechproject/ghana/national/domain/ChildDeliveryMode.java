package org.motechproject.ghana.national.domain;

public enum ChildDeliveryMode implements MobileFormEnum {
    NORMAL("1"), C_SECTION("2"), VACUUM("3"), FORCEPS("4");
    private String numericValue;

    ChildDeliveryMode(String numericValue) {
        this.numericValue = numericValue;
    }

    @Override
    public String getNumericValue() {
        return numericValue;
    }
}

