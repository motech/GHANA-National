package org.motechproject.ghana.national.functional.login;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.data.TestUser;
import org.motechproject.ghana.national.functional.pages.home.HomePage;
import org.motechproject.ghana.national.functional.pages.login.LoginPage;
import org.motechproject.ghana.national.functional.FunctionalTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class LoginTest extends FunctionalTest {
    private LoginPage loginPage;

    @BeforeMethod
    public void setUp() {
        loginPage = browser.gotoLoginPage();
    }

    @Test
    public void shouldLoginwithRightUsernameAndPassword() {
        loginPage.login(TestUser.admin());
        HomePage homePage = browser.homePage();
        assertTrue(homePage.isLogoutLinkVisible());
        homePage.logout();
    }

    @Test
    public void shouldNotLoginwithInvalidUsernameAndPassword() {
        loginPage.login(new TestUser("blah", "blahblah"));
        loginPage.assertIfLoginFailed();
    }

    @Test
    public void shouldNotLoginwithEmptyPassword() {
        loginPage.login(new TestUser(TestUser.admin().name(), ""));
        loginPage.assertIfLoginFailed();
    }
}
