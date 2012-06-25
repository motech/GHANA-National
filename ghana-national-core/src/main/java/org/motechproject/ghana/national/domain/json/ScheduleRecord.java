package org.motechproject.ghana.national.domain.json;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.type === 'ScheduleRecord'")
public class ScheduleRecord {
    @JsonProperty
    private String name;
    @JsonProperty
    private List<MilestoneRecord> milestones = new ArrayList<MilestoneRecord>();

    public String name() {
        return name;
    }

    public List<MilestoneRecord> milestoneRecords() {
        return milestones;
    }
}
