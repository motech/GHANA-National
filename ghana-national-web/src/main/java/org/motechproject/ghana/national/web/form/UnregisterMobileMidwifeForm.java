package org.motechproject.ghana.national.web.form;

public class UnregisterMobileMidwifeForm {
    private String patientMotechId;
    private String staffMotechId;
    private FacilityForm facilityForm;

    public UnregisterMobileMidwifeForm() {
    }

    public FacilityForm getFacilityForm() {
        return facilityForm;
    }

    public UnregisterMobileMidwifeForm setFacilityForm(FacilityForm facilityForm) {
        this.facilityForm = facilityForm;
        return this;
    }

    public String getStaffMotechId() {
        return staffMotechId;
    }

    public String getPatientMotechId() {
        return patientMotechId;
    }

    public UnregisterMobileMidwifeForm setPatientMotechId(String patientMotechId) {
        this.patientMotechId = patientMotechId;
        return this;
    }

    public UnregisterMobileMidwifeForm setStaffMotechId(String staffMotechId) {
        this.staffMotechId = staffMotechId;
        return this;
    }
}
