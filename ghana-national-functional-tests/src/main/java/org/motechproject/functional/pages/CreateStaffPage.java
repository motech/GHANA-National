package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.functional.util.Utilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateStaffPage {

    @FindBy(id = "user_first_name")
    @CacheLookup
    WebElement staffFirstName;

    @FindBy(id = "user_middle_name")
    @CacheLookup
    WebElement staffMidName;

    @FindBy(id = "user_last_name")
    @CacheLookup
    WebElement staffLastName;

    @FindBy(id = "phoneNumber")
    @CacheLookup
    WebElement staffPhoneNumber;

    @FindBy(id = "user_email")
    @CacheLookup
    WebElement staffEmailId;

    @FindBy(id = "user_role")
    @CacheLookup
    WebElement roleDropDown;

    @FindBy(id = "submitNewUser")
    @CacheLookup
    WebElement submitNewStaff;

    @Autowired
    WebDriverProvider webDriverProvider;

    @Autowired
    DataGenerator dataGenerator;

    private WebDriver driver;

    public enum STAFF_PAGE_ERROR_TAGS {phone_error, email_error, firstname_error, lastname_error, role_error}

    @Autowired
    Utilities myUtilities;

    @Autowired
    public CreateStaffPage(WebDriverProvider webDriverProvider) {
        this.driver = webDriverProvider.getWebDriver();
    }


    public CreateStaffPage WithStaffFirstName(String firstName) {
        staffFirstName.sendKeys(firstName);
        return this;
    }

    public CreateStaffPage WithStaffMidName(String midName) {
        staffMidName.sendKeys(midName);
        return this;
    }

    public CreateStaffPage WithStaffLastName(String lastName) {
        staffLastName.sendKeys(lastName);
        return this;
    }

    public CreateStaffPage WithValidStaffPhoneNumber() {
        staffPhoneNumber.sendKeys(dataGenerator.getRandPhoneNum());
        return this;
    }

    public CreateStaffPage WithStaffEmailid(String emailId) {
        staffEmailId.sendKeys(emailId);
        return this;
    }

    public CreateStaffPage WithGeneratedEmailID() {
        staffEmailId.sendKeys(dataGenerator.randomString(8) + "@generateddomain.com");
        return this;
    }

    public CreateStaffPage WithStaffRole(String staffCode) {
        Select selectStaffRole = new Select(roleDropDown);
        List<WebElement> staffCodes = selectStaffRole.getOptions();
        for (WebElement options : staffCodes) {
            if (options.getText().contains(staffCode)) {
                selectStaffRole.selectByVisibleText(options.getText());
                break;
            }
        }
        return this;
    }

    public String getStaffLogin() {
        return staffEmailId.getText();
    }

    public boolean SubmitStaffandWait() {
        submitNewStaff.click();
        return webDriverProvider.WaitForElement_ID("staffId");
//        String src = driver.findElement(By.className("success")).getText();
    }

    public void SubmitStaff() {
        submitNewStaff.click();
    }

    public CreateStaffPage WithInvalidStaffPhoneNum() {
        long n = (long) Math.floor(Math.random() * 9000000L) + 1000000L;
        staffPhoneNumber.sendKeys(Long.toString(n));
        return this;
    }

    public boolean isErrorPresent(STAFF_PAGE_ERROR_TAGS error_tags) {

        switch (error_tags) {
            case email_error:
                return driver.findElement(By.id("email_error")).isDisplayed();
            case phone_error:
                return driver.findElement(By.id("phoneNumberError")).isDisplayed();
            case firstname_error:
                return driver.findElement(By.id("firstName_error")).isDisplayed();
            case lastname_error:
                return driver.findElement(By.id("lastName_error")).isDisplayed();
            case role_error:
                return driver.findElement(By.id("role_error")).isDisplayed();
        }
        return false;
    }
}
