package org.motechproject.ghana.national.domain;

import org.motechproject.mrs.model.MRSPatient;

public class Patient {
    private MRSPatient mrsPatient;

    public Patient() {
    }

    public Patient(MRSPatient mrsPatient) {
        this.mrsPatient = mrsPatient;
    }

    public MRSPatient mrsPatient() {
        return mrsPatient;
    }
}
