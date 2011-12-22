package org.motechproject.ghana.national.web.form;

import java.util.List;

public class SearchPatientForm {
    private String motechId;
    private String name;
    private List<PatientForm> patientForms;

    public SearchPatientForm() {
    }

    public SearchPatientForm(String name, String motechId) {
        this.motechId = motechId;
        this.name = name;
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
}
