package org.motechproject.ghana.national.functional.pages.openmrs.vo;

public class OpenMRSObservationVO {

    private String observationName;
    private String value;

    public OpenMRSObservationVO(String observationName, String value) {
        this.observationName=observationName;
        this.value=value;
    }

    public String getObservationName() {
        return observationName;
    }

    public void setObservationName(String observationName) {
        this.observationName = observationName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
