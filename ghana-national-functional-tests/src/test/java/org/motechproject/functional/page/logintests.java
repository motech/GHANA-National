package org.motechproject.functional.page;

import org.junit.Assert;
import org.junit.Test;
import org.motechproject.functional.base.MyDriver;
import org.motechproject.functional.pages.HomePage;
import org.motechproject.functional.pages.LoginPage;
import org.openqa.selenium.WebDriver;

public class LoginTests {

    WebDriver driver = MyDriver.getDriverInstance();
    LoginPage loginPage = new LoginPage();
    HomePage homePage = new HomePage();
    // localhost
    //String URL="http://localhost:8123/GHANA-National-1.0-SNAPSHOT/spring_security_login";

    //qa environment url
    //String URL= "http:10.10.13.52:8080/ghana-national/admin";

    @Test
    public void abletoLoginwithRightUnamePass() {
        driver.navigate().to(MyDriver.URL);
        Assert.assertTrue(loginPage.LoginAs("admin", "P@ssw0rd"));
        homePage.Logout();
    }

    @Test
    public void shouldnotLoginwithInvalidUnamePass() {
        driver.navigate().to(MyDriver.URL);
        Assert.assertFalse(loginPage.LoginAs("blah", "blahblah"));
    }

    @Test
    public void shouldnotLoginwithEmptyPass() {
        driver.navigate().to(MyDriver.URL);
        Assert.assertFalse(loginPage.LoginAs("admin", ""));
    }
}
