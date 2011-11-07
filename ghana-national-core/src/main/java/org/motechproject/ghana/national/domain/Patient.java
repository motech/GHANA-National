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
    private String mrsPatientId;
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

    public Patient mrsPatientId(String mrsPatientId) {
        this.mrsPatientId = mrsPatientId;
        return this;
    }

    public String mrsPatientId() {
        return mrsPatientId;
    }
}
