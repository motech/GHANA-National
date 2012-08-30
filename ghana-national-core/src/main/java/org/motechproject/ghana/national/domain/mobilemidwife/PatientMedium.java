package org.motechproject.ghana.national.domain.mobilemidwife;

import org.motechproject.ghana.national.domain.Patient;

public class PatientMedium {
    Patient patient;
    Medium medium;

    public PatientMedium(Patient patient,Medium medium){
        this.patient=patient;
        this.medium=medium;
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
