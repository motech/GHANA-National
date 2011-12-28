package org.motechproject.ghana.national.functional.staff;


import org.junit.runner.RunWith;
import org.motechproject.functional.pages.home.HomePage;
import org.motechproject.functional.pages.login.LoginPage;
import org.motechproject.functional.pages.staff.SearchStaffPage;
import org.motechproject.functional.pages.staff.StaffPage;
import org.motechproject.functional.util.DataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class StaffTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private LoginPage loginPage;

    @Autowired
    private HomePage homePage;

    @Autowired
    private StaffPage staffPage;

    @Autowired
    private DataGenerator dataGenerator;

    @Autowired
    private SearchStaffPage searchStaffPage;

    @BeforeMethod
    public void setUp() {
        loginPage.login();
    }

    @Test
    public void shouldCreateAStaffSearchForHerAndEditHer() {
        String firstName = "First Name" + dataGenerator.randomString(5);
        String middleName = "Middle Name";
        String lastName = "Last Name";
        String phoneNumber = dataGenerator.randomPhoneNumber();
        String emailId = dataGenerator.randomEmailId();
        StaffPage.STAFF_ROLE role = StaffPage.STAFF_ROLE.COMMUNITY_HEALTH_WORKER;

        // create
        staffPage.open();

        staffPage
                .withFirstName(firstName)
                .withMiddleName(middleName)
                .withLastName(lastName)
                .withPhoneNumber(phoneNumber)
                .withEmailId(emailId)
                .withRole(role.getRole());
        staffPage.submit();

        // search
        searchStaffPage.open();
        searchStaffPage.withFirstName(firstName);
        searchStaffPage.search();
        searchStaffPage.assertIfSearchReturned(firstName, middleName, lastName, phoneNumber, role.getShortName());

        // update
        searchStaffPage.clickEditLink(firstName, middleName, lastName, phoneNumber, role.getShortName());

        String newFirstName = "New First Name" + dataGenerator.randomString(5);
        String newMiddleName = "New Middle Name";
        String newLastName = "New Last Name";
        String newPhoneNumber = dataGenerator.randomPhoneNumber();
        StaffPage.STAFF_ROLE newRole = StaffPage.STAFF_ROLE.FIELD_AGENT;

        staffPage
                .withFirstName(newFirstName)
                .withMiddleName(newMiddleName)
                .withLastName(newLastName)
                .withPhoneNumber(newPhoneNumber)
                .withRole(newRole.getRole());
        staffPage.update();

        // search
        searchStaffPage.open();
        searchStaffPage.withFirstName(newFirstName);
        searchStaffPage.search();
        searchStaffPage.assertIfSearchReturned(newFirstName, newMiddleName, newLastName, newPhoneNumber, newRole.getShortName());
    }

    @AfterMethod
    public void logout() {
        homePage.logout();
    }

    @AfterSuite
    public void closeall() {
        staffPage.getDriver().quit();
    }
}
