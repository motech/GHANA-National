package org.motechproject.ghana.national.web.form;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
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

    public StaffForm setId(String id) {
        this.id = id;
        return this;
    }

    public String getStaffId() {
        return staffId;
    }

    public StaffForm setStaffId(String staffId) {
        this.staffId = staffId;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public StaffForm setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public StaffForm setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public StaffForm setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public StaffForm setNewEmail(String newEmail) {
        this.newEmail = newEmail;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public StaffForm setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getNewRole() {
        return newRole;
    }

    public StaffForm setNewRole(String newRole) {
        this.newRole = newRole;
        return this;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public StaffForm setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
        return this;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }

    public MRSUser createUser() {
        String roleOfStaff = getNewRole();
        MRSPerson mrsPerson = new MRSPerson().firstName(getFirstName()).middleName(getMiddleName()).lastName(getLastName()).
                addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_EMAIL, getNewEmail()))
        .addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, getPhoneNumber()))
        .addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, roleOfStaff));
        MRSUser mrsUser = new MRSUser().id(getId()).systemId(getStaffId()).person(mrsPerson);

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
