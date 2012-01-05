package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ghana.national.vo.MotechProgramName;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'MotechProgram'")
public class MotechProgram extends MotechAuditableDataObject {

    @JsonProperty("type")
    private String type = "MotechProgram";

    @JsonProperty
    private MotechProgramName name;

    public MotechProgram() {
    }

    public MotechProgram(MotechProgramName name) {
        this.name = name;
    }

    public MotechProgramName getName() {
        return name;
    }
}
