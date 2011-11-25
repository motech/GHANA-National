package org.motechproject.functional.page;

import org.junit.Test;
import org.motechproject.functional.base.MyDriver;
import org.motechproject.functional.pages.HomePage;
import org.motechproject.functional.pages.LoginPage;
import org.openqa.selenium.WebDriver;

public class HomePageTests {
    WebDriver driver = MyDriver.getDriverInstance();
    LoginPage loginPage = new LoginPage();
    HomePage homePage = new HomePage();

    @Test
    public void verifyHomePageLinks() {
        driver.navigate().to(MyDriver.URL);
        loginPage.LoginAs("admin", "P@ssw0rd");
        //Assert.assertTrue(homePage.isVerifyHomePageLinks());
    }
}
