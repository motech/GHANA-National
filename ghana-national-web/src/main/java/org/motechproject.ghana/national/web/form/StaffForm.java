package org.motechproject.ghana.national.web.form;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSUser;

import static org.motechproject.ghana.national.domain.StaffType.Role.isAdmin;

public class StaffForm {
    private String id;
    private String staffId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;

    public StaffForm() {
    }

    public StaffForm(String id, String staffId, String firstName, String middleName, String lastName, String email, String phoneNumber, String role) {
        this.id = id;
        this.staffId = staffId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public MRSUser createUser() {
        MRSUser mrsUser = new MRSUser().id(getId()).systemId(getStaffId()).firstName(getFirstName()).middleName(getMiddleName()).lastName(getLastName());
        String roleOfStaff = getRole();

        mrsUser.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_EMAIL, getEmail()));
        mrsUser.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, getPhoneNumber()));
        mrsUser.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, roleOfStaff));
        mrsUser.securityRole(StaffType.Role.securityRoleFor(roleOfStaff));

        if (isAdmin(roleOfStaff)) {
            mrsUser.userName(getEmail());
        }
        return mrsUser;
    }
}
