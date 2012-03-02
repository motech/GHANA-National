package org.motechproject.ghana.national.domain;

public enum ChildDeliveryLocation implements MobileFormEnum{
    GOVERNMENT_HC_HP("1"), TEACHING_HOSPITAL("2"), GOVERNMENT_HOSPITAL("3"), PRIVATE_HOSPITAL("4"),
    CHAG("5"), QUASI_GOVT_INSTITUTE("6"), MINES("7"), HOMES("8");
    private String numericValue;

    ChildDeliveryLocation(String numericValue) {
        this.numericValue = numericValue;
    }

    @Override
    public String getNumericValue() {
        return numericValue;
    }
}
