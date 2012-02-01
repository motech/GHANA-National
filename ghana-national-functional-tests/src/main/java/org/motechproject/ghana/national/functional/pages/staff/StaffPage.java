package org.motechproject.ghana.national.functional.pages.staff;

import org.motechproject.ghana.national.functional.data.TestStaff;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class StaffPage extends StaffPageElements {
    @FindBy(id = "submitNewUser")
    @CacheLookup
    WebElement submit;

    public StaffPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitForElementID("submitNewUser", driver);
        PageFactory.initElements(driver, this);
    }

    public void create(TestStaff staff) {
        StaffPage staffPage = (StaffPage) withFirstName(staff.firstName())
                .withMiddleName(staff.middleName())
                .withLastName(staff.lastName())
                .withEmailId(staff.emailId())
                .withPhoneNumber(staff.phoneNumber())
                .withRole(staff.role().getRole());
        staffPage.submit();
        staff.motechId(staffPage.staffId());
    }

    public void submit() {
        submit.click();
        waitForSuccessMessage();
    }

    public void update() {
        submit.click();
        waitForSuccessMessage();
    }

    public void waitForSuccessMessage() {
        elementPoller.waitForElementClassName("success", driver);
    }


}
