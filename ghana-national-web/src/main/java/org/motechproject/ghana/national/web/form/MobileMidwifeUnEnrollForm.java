package org.motechproject.ghana.national.web.form;

public class MobileMidwifeUnEnrollForm {

    private String patientMotechId;
    private String staffMotechId;
    private FacilityForm facilityForm;

    public void setPatientMotechId(String patientMotechId) {
        this.patientMotechId = patientMotechId;
    }

    public String getPatientMotechId() {
        return patientMotechId;
    }

    public void setFacilityForm(FacilityForm facilityForm) {
        this.facilityForm = facilityForm;
    }

    public FacilityForm getFacilityForm() {
        return facilityForm;
    }

    public String getStaffMotechId() {
        return staffMotechId;
    }

    public void setStaffMotechId(String staffMotechId) {
        this.staffMotechId = staffMotechId;
    }
}
