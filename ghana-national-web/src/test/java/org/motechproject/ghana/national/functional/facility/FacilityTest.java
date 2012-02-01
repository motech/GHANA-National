package org.motechproject.ghana.national.functional.facility;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.data.TestFacility;
import org.motechproject.ghana.national.functional.pages.facility.FacilityPage;
import org.motechproject.ghana.national.functional.pages.facility.SearchFacilityPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class FacilityTest extends LoggedInUserFunctionalTest {
    private DataGenerator dataGenerator = new DataGenerator();

    @Test
    public void shouldCreateAFaclitySearchItAndEditTheDetailsOfIt() {
        String name = "Test Facility" + dataGenerator.randomString(5);

        String newName = "New Test Facility" + dataGenerator.randomString(5);
        String newRegion = "Ashanti";
        String newPhoneNumber = dataGenerator.randomPhoneNumber();

        FacilityPage facilityPage = browser.toFacilityPage(homePage);
        TestFacility facility = TestFacility.with(name);
        facilityPage.save(facility);

        SearchFacilityPage searchFacilityPage = browser.toSearchFacility(facilityPage);
        searchFacilityPage.withName(name).search();
        searchFacilityPage.displaying(facility);

        searchFacilityPage.clickEditLink(facility);
        facilityPage = browser.getFacilityPage();

        TestFacility facilityWithUpdates = TestFacility.with(newName).region(newRegion).phoneNumber(newPhoneNumber);
        facilityPage.save(facilityWithUpdates);

        searchFacilityPage = browser.toSearchFacility(facilityPage);
        searchFacilityPage.withCountry(facility.country()).withRegion(newRegion).search();
        searchFacilityPage.displaying(facilityWithUpdates);
    }
}
