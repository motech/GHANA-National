package org.motechproject.ghana.national.domain.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class AlertRecord {
    @JsonProperty
    private String validity;
    @JsonProperty
    private String window;

    public String validity() {
        return validity;
    }

    public String window() {
        return window;
    }
}
