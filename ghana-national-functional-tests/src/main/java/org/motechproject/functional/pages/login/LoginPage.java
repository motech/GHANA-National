package org.motechproject.functional.pages.login;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.BasePage;
import org.motechproject.functional.pages.home.HomePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

@Component
public class LoginPage extends BasePage {
    public static String LOGIN_PATH = "/ghana-national-web/login.jsp";

    @Value("#{functionalTestProperties['host']}")
    private String host;

    @Value("#{functionalTestProperties['port']}")
    private String port;

    @Value("#{functionalTestProperties['admin.username']}")
    private String adminUserNmae;

    @Value("#{functionalTestProperties['admin.password']}")
    private String adminPassword;

    @FindBy(name = "j_username")
    @CacheLookup
    private WebElement userName;

    @FindBy(name = "j_password")
    @CacheLookup
    private WebElement password;

    @FindBy(xpath = "//input[3]")
    @CacheLookup
    private WebElement submit;

    @Autowired
    private HomePage homePage;

    public LoginPage withUserName(String userName) {
        this.userName.clear();
        this.userName.sendKeys(userName);
        return this;
    }

    public LoginPage withPassword(String password) {
        this.password.clear();
        this.password.sendKeys(password);
        return this;
    }

    public void submit() {
        submit.click();

    }

    @Autowired
    public LoginPage(WebDriverProvider driverProvider) {
        super(driverProvider.getWebDriver());
    }

    public void loginAs(String userName, String password) {
        driver.get("http://" + host + ":" + port + LOGIN_PATH);
        elementPoller.waitForElementID("j_username", driver);
        PageFactory.initElements(driver, this);
        withUserName(userName).withPassword(password).submit();
        waitForHomePageToLoad();
    }

    private void waitForHomePageToLoad() {
        elementPoller.waitForElementLinkText("Logout", driver);
    }

    public void assertIfLoginSuccess() {
         assertTrue(homePage.isLogoutLinkVisible());
    }

    public void login() {
        loginAs(adminUserNmae, adminPassword);
    }

    public void assertIfLoginFailed() {
        PageFactory.initElements(driver, this);
        assertTrue(userName.isDisplayed());
        assertTrue(password.isDisplayed());
        assertTrue(submit.isDisplayed());
    }
}
