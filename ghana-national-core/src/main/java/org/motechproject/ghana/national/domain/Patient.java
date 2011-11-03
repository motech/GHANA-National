package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'Patient'")
public class Patient extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "Patient";
    @JsonProperty
    private Integer mrsFacilityId;
    @JsonProperty
    private Integer mrsPatientId;
    @JsonIgnore
    private org.motechproject.mrs.model.Patient mrsPatient;

    public Patient() {
    }

    public Patient(org.motechproject.mrs.model.Patient mrsPatient) {
        this.mrsPatient = mrsPatient;
    }

    public org.motechproject.mrs.model.Patient mrsPatient() {
        return mrsPatient;
    }
}
