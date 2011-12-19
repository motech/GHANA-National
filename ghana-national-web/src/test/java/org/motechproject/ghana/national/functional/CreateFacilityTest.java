package org.motechproject.ghana.national.functional;

import org.junit.runner.RunWith;
import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.CreateFacilityPage;
import org.motechproject.functional.pages.HomePage;
import org.motechproject.functional.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class CreateFacilityTest extends AbstractTestNGSpringContextTests {

    private Logger log = LoggerFactory.getLogger(CreateFacilityTest.class);

    @Autowired
    private LoginPage loginPage;

    @Autowired
    protected HomePage homePage;

    @Autowired
    protected CreateFacilityPage createFacilityPage;

    @Autowired
    private WebDriverProvider driverProvider;

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = driverProvider.getWebDriver();
    }

    @Test
    public void createFacilityWithValidValues() {
        loginPage.login();
        homePage.OpenCreateFacilityPage();
        boolean TestPassed;
        PageFactory.initElements(driver, createFacilityPage);
        long number = (long) Math.floor(Math.random() * 900000000L) + 100000000L;
        createFacilityPage
                .WithFacilityName("Test Facility")
                .withCountry("Ghana").withRegionName("Central Region")
        .withDistrict("Awutu Senya")
                .withSubDistrict("Bawjiase")

        .withPhoneNum();
        Assert.assertTrue(createFacilityPage.SubmitDetails());
    }

    @AfterMethod
    public void tearDown(){
        homePage.Logout();
    }

     @AfterSuite
    public void closeall() {
        driver.quit();
    }
}
