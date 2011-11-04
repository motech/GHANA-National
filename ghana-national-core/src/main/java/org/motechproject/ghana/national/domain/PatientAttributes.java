package org.motechproject.ghana.national.domain;

public enum PatientAttributes {
    INSURED("Insured"),
    NHIS_NUMBER("NHIS Number"),
    NHIS_EXPIRY_DATE("NHIS Expiration Date");

    private String attribute;

    private PatientAttributes(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }
}
