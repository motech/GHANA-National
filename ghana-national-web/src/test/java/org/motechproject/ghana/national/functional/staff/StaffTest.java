package org.motechproject.ghana.national.functional.staff;


import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.data.TestStaff;
import org.motechproject.ghana.national.functional.pages.staff.SearchStaffPage;
import org.motechproject.ghana.national.functional.pages.staff.StaffPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.motechproject.ghana.national.functional.data.TestStaff.STAFF_ROLE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class StaffTest extends LoggedInUserFunctionalTest {
    @Test
    public void shouldCreateAStaffSearchForHerAndEditHer() {
        DataGenerator dataGenerator = new DataGenerator();
        String firstName = "First Name" + dataGenerator.randomString(5);

        TestStaff staff = TestStaff.with(firstName);

        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        staffPage.create(staff);

        SearchStaffPage searchStaffPage = browser.toSearchStaffPage(staffPage);
        searchStaffPage.withFirstName(firstName);
        searchStaffPage.search();
        searchStaffPage.assertIfSearchReturned(staff.firstName(), staff.middleName(), staff.lastName(), staff.phoneNumber(), staff.role().getShortName());

        // update
        searchStaffPage.clickEditLink(staff.firstName(), staff.middleName(), staff.lastName(), staff.phoneNumber(), staff.role().getShortName());
        staffPage = browser.getStaffPage();

        String newFirstName = "New First Name" + dataGenerator.randomString(5);
        String newMiddleName = "New Middle Name";
        String newLastName = "New Last Name";
        String newPhoneNumber = dataGenerator.randomPhoneNumber();
        TestStaff.STAFF_ROLE newRole = STAFF_ROLE.FIELD_AGENT;

        staffPage
                .withFirstName(newFirstName)
                .withMiddleName(newMiddleName)
                .withLastName(newLastName)
                .withPhoneNumber(newPhoneNumber)
                .withRole(newRole.getRole());
        staffPage.update();

        // searchWithName
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
