package org.motechproject.ghana.national.functional.pages.openmrs;

import org.motechproject.ghana.national.functional.data.OpenMRSTestUser;
import org.motechproject.ghana.national.functional.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class OpenMRSLoginPage extends BasePage<OpenMRSLoginPage> {
    @FindBy(id = "username")
    @CacheLookup
    private WebElement userName;

    @FindBy(id = "password")
    @CacheLookup
    private WebElement password;

    @FindBy(xpath = "/html/body/div/div[3]/center/div/div/form/table/tbody/tr[3]/td[2]/input")
    @CacheLookup
    private WebElement submit;

    public OpenMRSLoginPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitForElementID("username", driver);
        PageFactory.initElements(driver, this);
    }

    public void login(OpenMRSTestUser user) {
        enter(this.userName, user.name()).enter(this.password, user.password());
        submit.click();
    }
}
