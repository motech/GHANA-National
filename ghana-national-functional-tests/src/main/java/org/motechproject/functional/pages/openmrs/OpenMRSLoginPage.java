package org.motechproject.functional.pages.openmrs;

import org.motechproject.functional.data.OpenMRSTestUser;
import org.motechproject.functional.data.TestUser;
import org.motechproject.functional.pages.BasePage;
import org.motechproject.functional.util.ElementPoller;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

public class OpenMRSLoginPage extends BasePage<OpenMRSLoginPage> {
    @FindBy(id = "username")
    @CacheLookup
    private WebElement userName;

    @FindBy(id = "password")
    @CacheLookup
    private WebElement password;

    @FindBy(xpath = "//input[3]")
    @CacheLookup
    private WebElement submit;

    public OpenMRSLoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void login(OpenMRSTestUser user) {
        enter(this.userName, user.name()).enter(this.password, user.password());
        submit.click();
    }
}
