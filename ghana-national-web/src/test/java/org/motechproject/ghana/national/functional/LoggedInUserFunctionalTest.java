package org.motechproject.ghana.national.functional;

import org.motechproject.functional.data.TestUser;
import org.motechproject.functional.pages.home.HomePage;
import org.motechproject.functional.pages.login.LoginPage;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class LoggedInUserFunctionalTest extends FunctionalTest {
    protected HomePage homePage;

    @BeforeMethod
    public void loggedInUserSetUp() {
        LoginPage loginPage = browser.gotoLoginPage();
        loginPage.login(TestUser.admin());
        homePage = browser.homePage();
    }

    @AfterMethod
    public void logout() {
        homePage.logout();
    }
}
