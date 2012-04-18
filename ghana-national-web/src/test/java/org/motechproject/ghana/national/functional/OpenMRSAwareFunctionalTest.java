package org.motechproject.ghana.national.functional;

import org.motechproject.ghana.national.functional.data.OpenMRSTestUser;
import org.motechproject.ghana.national.functional.data.TestUser;
import org.motechproject.ghana.national.functional.framework.OpenMRSBrowser;
import org.motechproject.ghana.national.functional.framework.OpenMRSDB;
import org.motechproject.ghana.national.functional.pages.home.HomePage;
import org.motechproject.ghana.national.functional.pages.login.LoginPage;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSHomePage;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSLoginPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public abstract class OpenMRSAwareFunctionalTest extends FunctionalTest {

    protected HomePage homePage;
    protected OpenMRSHomePage openMRSHomePage;
    @Autowired
    protected OpenMRSDB openMRSDB;

    @Autowired
    protected OpenMRSBrowser openMRSBrowser;

    @BeforeMethod
    public void loggedInUserSetUp() {
        login();
    }

    protected void login() {
        openMRSLogin();
        LoginPage loginPage = browser.gotoLoginPage();
        loginPage.login(TestUser.admin());
        homePage = browser.homePage();
    }

    protected void openMRSLogin() {
        OpenMRSLoginPage openMRSLoginPage = openMRSBrowser.toOpenMRSLoginPage();
        openMRSLoginPage.login(OpenMRSTestUser.admin());
        openMRSHomePage = openMRSBrowser.openMRSHomePage();
    }

    protected String getParsedDate(String estimatedDeliveryDate) throws ParseException {
        return new SimpleDateFormat("dd MMMM yyyy HH:mm:ss z").format(new SimpleDateFormat("dd/MM/yyyy").parse(estimatedDeliveryDate));
    }

    @AfterMethod
    public void logout() {
        openMRSBrowser.toOpenMRSHomePage().logout();
        browser.gotoHomePage().logout();
    }
}
