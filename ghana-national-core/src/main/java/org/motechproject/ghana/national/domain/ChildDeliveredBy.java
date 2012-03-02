package org.motechproject.ghana.national.domain;

public enum ChildDeliveredBy implements MobileFormEnum{
    DOCTOR("1"), MEDICAL_ASSISTANT("2"), MIDWIFE_PRIVATE("3"), MIDWIFE_GOVT("4"), CHO_OR_CHN("5"), TRAINED_TBA("6"),
    UNTRAINED_TBA("7"), FRIEND_OR_RELATIVE("8");
    private String numericValue;

    ChildDeliveredBy(String numericValue) {
        this.numericValue = numericValue;
    }

    @Override
    public String getNumericValue() {
        return numericValue;
    }
}
