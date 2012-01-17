package org.motechproject.ghana.national.functional.facility;

import org.junit.runner.RunWith;
import org.motechproject.functional.pages.facility.FacilityPage;
import org.motechproject.functional.pages.facility.SearchFacilityPage;
import org.motechproject.functional.util.DataGenerator;
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
        String country = "Ghana";
        String region = "Central Region";
        String district = "Awutu Senya";
        String subDistrict = "Bawjiase";
        String phoneNumber = dataGenerator.randomPhoneNumber();

        String newName = "New Test Facility" + dataGenerator.randomString(5);
        String newRegion = "Ashanti";
        String newPhoneNumber = dataGenerator.randomPhoneNumber();

        FacilityPage facilityPage = browser.toFacilityPage(homePage);

        facilityPage
                .withName(name)
                .withCountry(country)
                .withRegion(region)
                .withDistrict(district)
                .withSubDistrict(subDistrict)
                .withPhoneNum(phoneNumber).submit();

        SearchFacilityPage searchFacilityPage = browser.toSearchFacility(facilityPage);
        searchFacilityPage.withName(name).search();
        searchFacilityPage.assertIfSearchReturned(name, country, region, district, subDistrict, phoneNumber);

        searchFacilityPage.clickEditLink(name, country, region, district, subDistrict, phoneNumber);
        facilityPage = browser.getFacilityPage();
        facilityPage
                .withName(newName)
                .withRegion(newRegion)
                .withPhoneNum(newPhoneNumber).submit();

        searchFacilityPage = browser.toSearchFacility(facilityPage);
        searchFacilityPage.withCountry(country).withRegion(newRegion).search();
        searchFacilityPage.assertIfSearchReturned(newName, country, newRegion, "", "", newPhoneNumber);
    }
}
