package org.motechproject.ghana.national.domain;

import org.motechproject.mrs.model.MRSPatient;

public class Patient {
    private MRSPatient mrsPatient;

    private String parentId;

    public Patient() {
    }

    public Patient(MRSPatient mrsPatient) {
        this.mrsPatient = mrsPatient;
    }

    public MRSPatient mrsPatient() {
        return mrsPatient;
    }

    public String parentId() {
        return parentId;
    }

    public Patient parentId(String parentId) {
        this.parentId = parentId;
        return this;
    }
}
