package org.motechproject.ghana.national.functional.facility;

import org.junit.runner.RunWith;
import org.motechproject.functional.pages.login.LoginPage;
import org.motechproject.functional.pages.facility.FacilityPage;
import org.motechproject.functional.pages.facility.SearchFacilityPage;
import org.motechproject.functional.pages.home.HomePage;
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
public class FacilityTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private LoginPage loginPage;

    @Autowired
    private HomePage homePage;

    @Autowired
    private FacilityPage facilityPage;

    @Autowired
    private SearchFacilityPage searchFacilityPage;

    @Autowired
    protected DataGenerator dataGenerator;

    @BeforeMethod
    public void setUp() {
        loginPage.login();
    }

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

        // create
        facilityPage.open();

        facilityPage
                .withName(name)
                .withCountry(country)
                .withRegion(region)
                .withDistrict(district)
                .withSubDistrict(subDistrict)
                .withPhoneNum(phoneNumber).submit();

        // search
        searchFacilityPage.open();
        searchFacilityPage.withName(name).search();
        searchFacilityPage.assertIfSearchReturned(name, country, region, district, subDistrict, phoneNumber);

        // update
        searchFacilityPage.clickEditLink(name, country, region, district, subDistrict, phoneNumber);
        facilityPage
                .withName(newName)
                .withRegion(newRegion)
                .withPhoneNum(newPhoneNumber).submit();

        // search
        searchFacilityPage.open();
        searchFacilityPage.withCountry(country).withRegion(newRegion).search();
        searchFacilityPage.assertIfSearchReturned(newName, country, newRegion, "", "", newPhoneNumber);
    }

    @AfterMethod
    public void tearDown() {
        homePage.logout();
    }

    @AfterSuite
    public void closeall() {
        facilityPage.getDriver().quit();
    }
}
