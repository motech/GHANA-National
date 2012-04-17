package org.motechproject.ghana.national.functional;

import org.motechproject.ghana.national.functional.data.TestUser;
import org.motechproject.ghana.national.functional.pages.home.HomePage;
import org.motechproject.ghana.national.functional.pages.login.LoginPage;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSHomePage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class LoggedInUserFunctionalTest extends FunctionalTest {
    protected HomePage homePage;
    protected OpenMRSHomePage openMRSHomePage;
    @BeforeMethod
    public void loggedInUserSetUp() {
        login();
    }

    protected void login() {
        LoginPage loginPage = browser.gotoLoginPage();
        loginPage.login(TestUser.admin());
        homePage = browser.homePage();
    }

    @AfterMethod
    public void logout() {
        homePage.logout();
    }
}
