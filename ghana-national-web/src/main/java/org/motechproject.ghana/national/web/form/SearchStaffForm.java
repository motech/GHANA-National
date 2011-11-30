package org.motechproject.ghana.national.web.form;

public class SearchStaffForm extends CreateStaffForm {
    private String staffID;

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String id) {
        this.staffID = id;
    }

    public SearchStaffForm(String staffID, String firstName, String middleName, String lastName, String email, String phoneNumber, String role) {
        super(firstName, middleName, lastName, email, phoneNumber, role);
        this.staffID = staffID;
    }

    public SearchStaffForm() {
        super();
    }
}
