package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'Enrollment'")
public class Enrollment extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "Enrollment";        
    @JsonProperty
    private String patientId;
    @JsonProperty
    private MotechProgram program;

    public Enrollment() {
    }

    public Enrollment(String patientId, MotechProgram program) {
        this.patientId = patientId;
        this.program = program;
    }

    public String getPatientId() {
        return patientId;
    }

    public MotechProgram getProgram() {
        return program;
    }
}
