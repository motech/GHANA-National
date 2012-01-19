package org.motechproject.functional.pages.facility;

import org.motechproject.functional.data.TestFacility;
import org.motechproject.functional.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.HashMap;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

public class SearchFacilityPage extends BasePage {
    @FindBy(id = "facilityId")
    @CacheLookup
    WebElement id;

    @FindBy(id = "name")
    @CacheLookup
    WebElement name;

    @FindBy(id = "countries")
    @CacheLookup
    WebElement country;

    @FindBy(id = "regions")
    @CacheLookup
    WebElement region;

    @FindBy(id = "districts")
    @CacheLookup
    WebElement district;

    @FindBy(id = "sub-districts")
    @CacheLookup
    WebElement subDistrict;

    @FindBy(id = "searchFacility")
    @CacheLookup
    WebElement search;

    private String searchResultTableId = "searchResultTable";

    public SearchFacilityPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitForElementID("searchFacility", driver);
        PageFactory.initElements(driver, this);
    }


    public SearchFacilityPage withName(String name) {
        this.name.clear();
        this.name.sendKeys(name);
        return this;
    }

    public SearchFacilityPage withCountry(String country) {
        Select selectCountry = new Select(this.country);
        selectCountry.selectByValue(country);
        return this;
    }

    public SearchFacilityPage withRegion(String regionName) {
        Select selectRegion = new Select(region);
        selectRegion.selectByValue(regionName);
        return this;
    }

    public SearchFacilityPage withDistrict(String districtName) {
        Select selectDistrict = new Select(district);
        selectDistrict.selectByValue(districtName);
        return this;
    }

    public SearchFacilityPage withSubDistrict(String subDistName) {
        Select selectSubDistrict = new Select(subDistrict);
        selectSubDistrict.selectByValue(subDistName);
        return this;
    }

    public void waitForSearchResultsToLoad() {
        elementPoller.waitForElementID("facilitiesReturnedBySearch", driver);
    }

    public void search() {
        search.click();
        waitForSearchResultsToLoad();
    }

    public void clickEditLink(TestFacility facility) {
        htmlTableParser.clickEditLink(driver, searchResultTableId, mapTableRowDataWithColumns(facility.name(), facility.country(), facility.region(), facility.district(), facility.subDistrict(), facility.phoneNumber()));
    }

    public void assertIfSearchReturned(String name, String country, String region, String district, String subDistrict, String phoneNumber) {
        assertTrue(htmlTableParser.hasRow(driver, searchResultTableId, mapTableRowDataWithColumns(name, country, region, district, subDistrict, phoneNumber)));
    }

    private HashMap<String, String> mapTableRowDataWithColumns(final String name, final String country, final String region, final String district, final String subDistrict, final String phoneNumber) {
        return new HashMap<String, String>() {{
            put("Name", name);
            put("Country", country);
            put("Region", region);
            put("District", district);
            put("Sub-District", subDistrict);
            put("Phone number", phoneNumber);
        }};
    }

    public void displaying(TestFacility facility) {
        assertIfSearchReturned(facility.name(), facility.country(), facility.region(), facility.district(), facility.subDistrict(), facility.phoneNumber());
    }
}
