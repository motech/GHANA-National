package org.motechproject.ghana.national.web.form;

import java.util.List;

public class SearchPatientForm {
    private String motechId;
    private String name;
    private List<PatientForm> patientForms;
    private String phoneNumber;

    public SearchPatientForm() {
    }

    public SearchPatientForm(String name, String motechId, String phoneNumber) {
        this.motechId = motechId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public SearchPatientForm(List<PatientForm> patientForms) {
        this.patientForms = patientForms;
    }

    public String getMotechId() {
        return motechId;
    }

    public void setMotechId(String motechId) {
        this.motechId = motechId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PatientForm> getPatientForms() {
        return patientForms;
    }

    public void setPatientForms(List<PatientForm> patientForms) {
        this.patientForms = patientForms;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
