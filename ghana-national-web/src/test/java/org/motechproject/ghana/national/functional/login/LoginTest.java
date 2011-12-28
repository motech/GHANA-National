package org.motechproject.ghana.national.functional.login;


import org.junit.runner.RunWith;
import org.motechproject.functional.pages.home.HomePage;
import org.motechproject.functional.pages.login.LoginPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class LoginTest extends AbstractTestNGSpringContextTests {

    @Value("#{functionalTestProperties['admin.username']}")
    private String adminUserNmae;

    @Value("#{functionalTestProperties['admin.password']}")
    private String adminPassword;

    @Autowired
    private LoginPage loginPage;

    @Autowired
    private HomePage homePage;

    @Test
    public void shouldLoginwithRightUsernameAndPassword() {
        loginPage.loginAs(adminUserNmae, adminPassword);
        loginPage.assertIfLoginSuccess();
        homePage.logout();
    }

    @Test
    public void shouldNotLoginwithInvalidUsernameAndPassword() {
        loginPage.loginAs("blah", "blahblah");
        loginPage.assertIfLoginFailed();

    }

    @Test
    public void shouldNotLoginwithEmptyPassword() {
        loginPage.loginAs(adminUserNmae, "");
        loginPage.assertIfLoginFailed();
    }

    @AfterSuite
    public void closeAll() {
        loginPage.getDriver().quit();
    }
}
