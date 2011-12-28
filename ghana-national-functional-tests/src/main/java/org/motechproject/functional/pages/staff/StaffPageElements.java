package org.motechproject.functional.pages.staff;

import org.motechproject.functional.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class StaffPageElements extends BasePage {
    @FindBy(id = "user_first_name")
    @CacheLookup
    WebElement firstName;

    @FindBy(id = "user_middle_name")
    @CacheLookup
    WebElement middleName;

    @FindBy(id = "user_last_name")
    @CacheLookup
    WebElement lastName;

    @FindBy(id = "phoneNumber")
    @CacheLookup
    WebElement phoneNumber;

    @FindBy(id = "user_email")
    @CacheLookup
    WebElement emailId;

    @FindBy(id = "user_role")
    @CacheLookup
    WebElement role;

    public StaffPageElements withFirstName(String firstName) {
        this.firstName.clear();
        this.firstName.sendKeys(firstName);
        return this;
    }

    public StaffPageElements withMiddleName(String middleName) {
        this.middleName.clear();
        this.middleName.sendKeys(middleName);
        return this;
    }

    public StaffPageElements withLastName(String lastName) {
        this.lastName.clear();
        this.lastName.sendKeys(lastName);
        return this;
    }

    public StaffPageElements withPhoneNumber(String phoneNumber) {
        this.phoneNumber.clear();
        this.phoneNumber.sendKeys(phoneNumber);
        return this;
    }

    public StaffPageElements withEmailId(String emailId) {
        this.emailId.clear();
        this.emailId.sendKeys(emailId);
        return this;
    }

    public StaffPageElements withRole(String role) {
        Select selectRole = new Select(this.role);
        selectRole.selectByVisibleText(role);
        return this;
    }

    public static enum STAFF_ROLE{
        SUPER_ADMIN("Super Admin (Super Administrator)", "Super Admin"),
        FACILITY_ADMIN("Facility Admin (Facility Administrator)", "Facility Admin"),
        CALL_CENTER_ADMIN("CallCenter Admin (Call Centre Administrator)", "CallCenter Admin"),
        HEALTH_CARE_ADMIN("HealthCare Admin (Health Care Administrator)", "HealthCare Admin"),
        HEALTH_EXT_WORKER("HEW (Health Extension Worker)", "HEW"),
        COMMUNITY_HEALTH_WORKER("CHO (Community Health Officer)", "CHO"),
        COMMUNITY_HEALTH_NURSE("CHN (Community Health Nurse)", "CHN"),
        COMMUNITY_HEALTH_VOLUNTEER("CHV (Community Health Volunteer)", "CHV"),
        COMMUNITY_HEALTH_OFFICER("HPO (Health Promotion Officer)", "HPO"),
        FIELD_AGENT("FA (Motech Field Agent)", "FA"),
        MOBILE_MIDWIFE_AGENT("MMA (Mobile Midwife Agent)", "MMA");

        private String role;
        private String shortName;

        STAFF_ROLE(String role, String shortName) {
            this.role = role;
            this.shortName = shortName;
        }

        public String getRole() {
            return role;
        }

        public String getShortName() {
            return shortName;
        }
    }

    public StaffPageElements(WebDriver webDriver) {
        super(webDriver);
    }

}
