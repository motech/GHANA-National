package org.motechproject.ghana.national.functional.pages.openmrs;

import org.motechproject.ghana.national.functional.data.OpenMRSTestUser;
import org.motechproject.ghana.national.functional.pages.BasePage;
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
