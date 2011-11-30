package org.motechproject.ghana.national.functional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.HomePage;
import org.motechproject.functional.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class LoginTest {

    @Autowired
    private LoginPage loginPage;

    @Autowired
    private HomePage homePage;

    @Autowired
    private WebDriverProvider driverProvider;

    private WebDriver driver;

    @Before
    public void setUp() {
        driver = driverProvider.getWebDriver();
    }
    @Test
    public void abletoLoginwithRightUnamePass() {
        Assert.assertTrue(loginPage.LoginAs("admin", "P@ssw0rd"));
        homePage.Logout();
    }

    @Test
    public void shouldnotLoginwithInvalidUnamePass() {
        Assert.assertFalse(loginPage.LoginAs("blah", "blahblah"));
    }

    @Test
    public void shouldnotLoginwithEmptyPass() {
        Assert.assertFalse(loginPage.LoginAs("admin", ""));
    }
}
