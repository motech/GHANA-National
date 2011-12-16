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
    private String newEmail;
    private String phoneNumber;
    private String newRole;
    private String currentRole;
    private String currentEmail;

    public StaffForm() {
    }

    public StaffForm(String id, String staffId, String firstName, String middleName, String lastName, String newEmail, String phoneNumber, String newRole, String currentRole, String currentEmail) {
        this.id = id;
        this.staffId = staffId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.newEmail = newEmail;
        this.phoneNumber = phoneNumber;
        this.newRole = newRole;
        this.currentRole = currentRole;
        this.currentEmail = currentEmail;
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

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNewRole() {
        return newRole;
    }

    public void setNewRole(String newRole) {
        this.newRole = newRole;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }

    public MRSUser createUser() {
        MRSUser mrsUser = new MRSUser().id(getId()).systemId(getStaffId()).firstName(getFirstName()).middleName(getMiddleName()).lastName(getLastName());
        String roleOfStaff = getNewRole();

        mrsUser.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_EMAIL, getNewEmail()));
        mrsUser.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, getPhoneNumber()));
        mrsUser.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, roleOfStaff));
        mrsUser.securityRole(StaffType.Role.securityRoleFor(roleOfStaff));

        try {
            if (isAdmin(roleOfStaff)) {
                mrsUser.userName(getNewEmail());
            }
        } catch (Exception ignored) {
        }
        return mrsUser;
    }
}
