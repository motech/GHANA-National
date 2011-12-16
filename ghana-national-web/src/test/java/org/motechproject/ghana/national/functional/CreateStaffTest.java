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
       loginPage.loginAs("admin","P@ssw0rd");
        homePage.OpenCreateStaffPage();
        PageFactory.initElements(driver,createStaff);

        createStaff.WithStaffFirstName("Automation")
                .WithStaffLastName("automation")
                .WithValidStaffPhoneNumber()
                .WithGeneratedEmailID()
                .WithStaffRole("Super Admin");
        Assert.assertTrue(createStaff.SubmitStaff());

    }


}
