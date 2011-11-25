package org.motechproject.functional.page;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.functional.base.MyDriver;
import org.motechproject.functional.pages.CreateFacilityPage;
import org.motechproject.functional.pages.HomePage;
import org.motechproject.functional.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class CreateFacilityTest {

    LoginPage loginPage = new LoginPage();
    HomePage homePage = new HomePage();
    CreateFacilityPage createFacilityPage = new CreateFacilityPage();
    WebDriver driver = MyDriver.getDriverInstance();


    /*@Test
    public void CheckIfAllRelevantFieldsAreDisplayedOnLoad() {
        driver.navigate().to(MyDriver.URL);
        loginPage.LoginAs("admin", "P@ssw0rd");
        homePage.OpenCreateFacilityPage();
        //createFacilityPage = PageFactory.initElements(driver, CreateFacilityPage.class);
        Assert.assertTrue(createFacilityPage.EnsureAllInputFieldsArePresentOnLoad());
        homePage.Logout();
    }
*/
    @Test
    public void createFacilityWithValidValues() {
        driver.navigate().to(MyDriver.URL);
        loginPage.LoginAs("admin", "P@ssw0rd");
        homePage.OpenCreateFacilityPage();

        boolean TestPassed = true;
        createFacilityPage = PageFactory.initElements(driver, CreateFacilityPage.class);
        createFacilityPage.SetFacilityName("Test Facility" + Math.random() * 9000L);
        createFacilityPage.SelectCountry("Ghana");
        if (createFacilityPage.IsRegionDisplayed()) {
            createFacilityPage.SetRegionName("Central Region");
        } else {
            System.out.println("Region Drop down not appearing");
            TestPassed = false;
        }
        createFacilityPage.SelectDistrict("Awutu Senya");
        createFacilityPage.SelectSubDistrict("Bawjiase");

        long number = (long) Math.floor(Math.random() * 900000000L) + 100000000L;

        createFacilityPage.SetPhoneNum("0" + number);

        if (createFacilityPage.SubmitDetails())
            TestPassed = true;
        else
            TestPassed = false;

        Assert.assertTrue(TestPassed);
        homePage.Logout();
    }


}
