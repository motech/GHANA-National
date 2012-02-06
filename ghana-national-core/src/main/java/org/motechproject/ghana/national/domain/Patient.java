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

    public Patient(MRSPatient mrsPatient, String parentId) {
        this.mrsPatient = mrsPatient;
        this.parentId = parentId;
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
    
    public String getMRSPatientId() {
        return mrsPatient.getId();
    }

    public String getMotechId() {
        return mrsPatient.getMotechId();
    }

    public String getFirstName(){
        return mrsPatient.getPerson().getFirstName();
    }

    public String getLastName() {
        return mrsPatient.getPerson().getLastName();
    }
}
