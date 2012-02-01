package org.motechproject.ghana.national.functional.pages.login;

import org.motechproject.ghana.national.functional.data.TestUser;
import org.motechproject.ghana.national.functional.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

public class LoginPage extends BasePage<LoginPage> {
    @FindBy(name = "j_username")
    @CacheLookup
    private WebElement userName;

    @FindBy(name = "j_password")
    @CacheLookup
    private WebElement password;

    @FindBy(xpath = "//input[3]")
    @CacheLookup
    private WebElement submit;

    public LoginPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitForElementID("j_username", driver);
        PageFactory.initElements(driver, this);
    }

    public void login(TestUser user) {
        enter(this.userName, user.name()).enter(this.password, user.password());
        submit.click();
    }

    public void assertIfLoginFailed() {
        PageFactory.initElements(driver, this);
        assertTrue(userName.isDisplayed());
        assertTrue(password.isDisplayed());
        assertTrue(submit.isDisplayed());
    }
}
