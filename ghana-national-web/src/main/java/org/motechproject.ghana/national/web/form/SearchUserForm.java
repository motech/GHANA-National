package org.motechproject.ghana.national.web.form;

public class SearchUserForm extends CreateUserForm{
    private String staffID;

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String id) {
        this.staffID = id;
    }
}
