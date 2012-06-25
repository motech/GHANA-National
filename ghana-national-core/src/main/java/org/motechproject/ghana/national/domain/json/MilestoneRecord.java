package org.motechproject.ghana.national.domain.json;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class MilestoneRecord {
    @JsonProperty
    private String name;
    @JsonProperty
    private List<AlertRecord> alerts = new ArrayList<AlertRecord>();

    public String name() {
        return name;
    }

    public List<AlertRecord> alerts() {
        return alerts;
    }
}
