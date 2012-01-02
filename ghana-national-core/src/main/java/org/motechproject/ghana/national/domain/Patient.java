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

    public MRSPatient getMrsPatient() {
        return mrsPatient;
    }

    public String getParentId() {
        return parentId;
    }

    public Patient parentId(String parentId) {
        this.parentId = parentId;
        return this;
    }
}
