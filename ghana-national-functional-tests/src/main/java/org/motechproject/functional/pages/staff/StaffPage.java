package org.motechproject.functional.pages.staff;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.home.HomePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaffPage extends StaffPageElements {

    @Autowired
    private HomePage homePage;

    private String submitStaffElementId = "submitNewUser";

    private String successMessageClass = "success";

    @FindBy(id = "submitNewUser")
    @CacheLookup
    WebElement submit;

    @Autowired
    public StaffPage(WebDriverProvider webDriverProvider) {
        super(webDriverProvider.getWebDriver());
    }


    public void open() {
        homePage.openCreateStaffPage();
        waitForPageToLoad();
    }

    public void submit() {
        submit.click();
        waitForSuccessMessage();
    }

    public void update() {
        submit.click();
        waitForSuccessMessage();
    }

    public void waitForSuccessMessage(){
        elementPoller.waitForElementClassName(successMessageClass, driver);
    }

    public void waitForPageToLoad() {
        elementPoller.waitForElementID(submitStaffElementId, driver);
        PageFactory.initElements(driver, this);
    }

}
