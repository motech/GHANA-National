package org.motechproject.ghana.national.functional;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.CreateFacilityPage;
import org.motechproject.functional.pages.HomePage;
import org.motechproject.functional.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext-functional-tests.xml"})
public class CreateFacilityTest {

    @Autowired
    private LoginPage loginPage;

    @Autowired
    private HomePage homePage;

    @Autowired
    private CreateFacilityPage createFacilityPage;

    @Autowired
    private WebDriverProvider driverProvider;

    private WebDriver driver;

    @Before
    public void setUp() {
        driver = driverProvider.getWebDriver();
    }

    @Ignore
    @Test
    public void createFacilityWithValidValues() {
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
