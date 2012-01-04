package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'MotechProgram'")
public class MotechProgram extends MotechAuditableDataObject {

    @JsonProperty("type")
    private String type = "MotechProgram";

    @JsonProperty
    private String name;

    public MotechProgram() {
    }

    public MotechProgram(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
