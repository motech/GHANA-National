package org.motechproject.ghana.national.functional;


import org.junit.runner.RunWith;
import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.HomePage;
import org.motechproject.functional.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class LoginTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private LoginPage loginPage;

    @Autowired
    private HomePage homePage;

    @Autowired
    private WebDriverProvider driverProvider;

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = driverProvider.getWebDriver();
    }

    @Test
    public void abletoLoginwithRightUnamePass() {
        Assert.assertTrue(loginPage.loginAs("admin", "P@ssw0rd"));
        homePage.Logout();
    }

    @Test
    public void shouldnotLoginwithInvalidUnamePass() {
        Assert.assertFalse(loginPage.loginAs("blah", "blahblah"));
    }

    @Test
    public void shouldnotLoginwithEmptyPass() {
        Assert.assertFalse(loginPage.loginAs("admin", ""));
    }

    @AfterSuite
    public void closeall() {
        driver.close();
    }
}
