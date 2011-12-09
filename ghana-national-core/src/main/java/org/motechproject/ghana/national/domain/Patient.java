package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;
import org.motechproject.mrs.model.MRSPatient;

@TypeDiscriminator("doc.type === 'Patient'")
public class Patient extends MotechAuditableDataObject {
    @JsonProperty("type")
    private String type = "Patient";
    @JsonProperty
    private String mrsPatientId;
    @JsonIgnore
    private MRSPatient mrsPatient;

    public Patient() {
    }

    public Patient(MRSPatient mrsPatient) {
        this.mrsPatient = mrsPatient;
    }

    public MRSPatient mrsPatient() {
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
