package org.motechproject.ghana.national.domain;

public class AggregationMessageIdentifier {
    public static final String SEPARATOR = "-";
    private String externalId;
    private String scheduleName;

    public AggregationMessageIdentifier(String externalId, String scheduleName) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
    }


    public String getIdentifier() {
        return externalId + SEPARATOR + scheduleName;
    }
}
