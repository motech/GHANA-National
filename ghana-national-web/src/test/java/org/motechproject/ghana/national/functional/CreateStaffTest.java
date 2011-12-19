package org.motechproject.ghana.national.functional;


import org.junit.runner.RunWith;
import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.CreateStaffPage;
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
public class CreateStaffTest extends AbstractTestNGSpringContextTests {

    private Logger log = LoggerFactory.getLogger(CreateStaffTest.class);

    @Autowired
    private LoginPage loginPage;

    @Autowired
    private HomePage homePage;

/*    @Autowired
    private CreateFacilityPage createFacilityPage;*/

    @Autowired
    private CreateStaffPage createStaff;

    @Autowired
    private WebDriverProvider driverProvider;

    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = driverProvider.getWebDriver();

    }

    @Test
    public void createStaffWithValidValues() {
        loginPage.login();
        homePage.OpenCreateStaffPage();
        PageFactory.initElements(driver, createStaff);

        createStaff.WithStaffFirstName("Automation")
                .WithStaffLastName("automation")
                .WithValidStaffPhoneNumber()
                .WithGeneratedEmailID()
                .WithStaffRole("Super Admin");
        Assert.assertTrue(createStaff.SubmitStaffandWait());
    }

    @Test
    public void assertForInvalidPhone() {
        loginPage.login();
        homePage.OpenCreateStaffPage();
        PageFactory.initElements(driver, createStaff);

        createStaff.WithStaffFirstName("Automation")
                .WithStaffLastName("automation")
                .WithInvalidStaffPhoneNum()
                .WithGeneratedEmailID()
                .WithStaffRole("Super Admin")
                .SubmitStaff();

        Assert.assertTrue(createStaff.isErrorPresent(CreateStaffPage.STAFF_PAGE_ERROR_TAGS.phone_error));
    }

    @Test
    public void assertForMissingOrInvalidFirstName() {
        loginPage.login();
        homePage.OpenCreateStaffPage();
        PageFactory.initElements(driver, createStaff);
        createStaff.SubmitStaff();
        createStaff.WithStaffFirstName("Auto1").SubmitStaff();
        createStaff.isErrorPresent(CreateStaffPage.STAFF_PAGE_ERROR_TAGS.firstname_error);
    }

    @Test
    public void assertForMissingReqdFields() {
        loginPage.login();
        homePage.OpenCreateStaffPage();
        PageFactory.initElements(driver, createStaff);
        createStaff.SubmitStaff();
        Assert.assertTrue(createStaff.isErrorPresent(CreateStaffPage.STAFF_PAGE_ERROR_TAGS.firstname_error));
        Assert.assertTrue(createStaff.isErrorPresent(CreateStaffPage.STAFF_PAGE_ERROR_TAGS.lastname_error));
        Assert.assertTrue(createStaff.isErrorPresent(CreateStaffPage.STAFF_PAGE_ERROR_TAGS.phone_error));
        Assert.assertTrue(createStaff.isErrorPresent(CreateStaffPage.STAFF_PAGE_ERROR_TAGS.role_error));
    }

    @Test
    public void createAdminWithoutEmailAndCheckIfErrorIsThrown() {
        loginPage.login();
        homePage.OpenCreateStaffPage();
        PageFactory.initElements(driver, createStaff);
        createStaff.WithStaffFirstName("AutomationValid")
                .WithStaffLastName("AutoLastNAmeValid")
                .WithStaffRole("Super Admin");
        createStaff.SubmitStaff();
        Assert.assertTrue(createStaff.isErrorPresent(CreateStaffPage.STAFF_PAGE_ERROR_TAGS.email_error));
    }

    @AfterMethod
    public void logout() {
        homePage.Logout();
    }

    @AfterSuite
    public void closeall() {
        driver.quit();
    }
}
