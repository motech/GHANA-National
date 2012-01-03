package org.motechproject.ghana.national.functional.staff;


import org.junit.runner.RunWith;
import org.motechproject.functional.pages.staff.SearchStaffPage;
import org.motechproject.functional.pages.staff.StaffPage;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class StaffTest extends LoggedInUserFunctionalTest {
    @Test
    public void shouldCreateAStaffSearchForHerAndEditHer() {
        DataGenerator dataGenerator = new DataGenerator();
        String firstName = "First Name" + dataGenerator.randomString(5);
        String middleName = "Middle Name";
        String lastName = "Last Name";
        String phoneNumber = dataGenerator.randomPhoneNumber();
        String emailId = dataGenerator.randomEmailId();
        StaffPage.STAFF_ROLE role = StaffPage.STAFF_ROLE.COMMUNITY_HEALTH_WORKER;

        homePage.openCreateStaffPage();
        StaffPage staffPage = browser.getStaffPage();

        staffPage
                .withFirstName(firstName)
                .withMiddleName(middleName)
                .withLastName(lastName)
                .withPhoneNumber(phoneNumber)
                .withEmailId(emailId)
                .withRole(role.getRole());
        staffPage.submit();

        SearchStaffPage searchStaffPage = browser.toSearchStaffPage(staffPage);
        searchStaffPage.withFirstName(firstName);
        searchStaffPage.search();
        searchStaffPage.assertIfSearchReturned(firstName, middleName, lastName, phoneNumber, role.getShortName());

        // update
        searchStaffPage.clickEditLink(firstName, middleName, lastName, phoneNumber, role.getShortName());
        staffPage = browser.getStaffPage();

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
        searchStaffPage = browser.toSearchStaffPage(staffPage);
        searchStaffPage.withFirstName(newFirstName);
        searchStaffPage.search();
        searchStaffPage.assertIfSearchReturned(newFirstName, newMiddleName, newLastName, newPhoneNumber, newRole.getShortName());
    }

    @AfterMethod
    public void logout() {
        homePage.logout();
    }
}
