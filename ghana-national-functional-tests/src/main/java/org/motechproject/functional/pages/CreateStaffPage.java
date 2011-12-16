package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.util.DataGenerator;
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

    public boolean SubmitStaff() {
        submitNewStaff.click();
        webDriverProvider.WaitForElement_ID("footer");
//        String src = driver.findElement(By.className("success")).getText();
            String src = driver.getPageSource();
        if (src.contains("Staff created successfully"))
            return true;
        else
            return false;

    }


}
