package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.functional.util.Utilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateFacilityPage {
    private WebDriver driver;

    @FindBy(name = "country")
    @CacheLookup
    WebElement countryDropDown;

    @FindBy(name = "name")
    @CacheLookup
    WebElement facilityNameInput;

    @FindBy(name = "countyDistrict")
    @CacheLookup
    WebElement districtDropDown;

    @FindBy(name = "stateProvince")
    @CacheLookup
    WebElement subDistDropDown;

    @FindBy(name = "phoneNumber")
    @CacheLookup
    WebElement phoneNumberInput;

    @FindBy(name = "region")
    @CacheLookup
    WebElement regionDropDown;

    @FindBy(id = "submitFacility")
    @CacheLookup
    WebElement submitFacilityDetails;

    @Autowired
    public CreateFacilityPage(WebDriverProvider webDriverProvider) {
        this.driver = webDriverProvider.getWebDriver();
    }

    @Autowired
    WebDriverProvider webDriverProvider;

    @Autowired
    DataGenerator dataGenerator;

    @Autowired
    Utilities utilities;

    public CreateFacilityPage withFacilityName(String facilityName) {
        facilityNameInput.clear();
        facilityNameInput.sendKeys(facilityName + dataGenerator.randomString(5));
        return this;
    }

    public CreateFacilityPage withCountry(String countryName) {
        Select selectCountry = new Select(countryDropDown);
        selectCountry.selectByValue(countryName);
        return this;
    }

    public CreateFacilityPage withRegionName(String regionName) {
        Select selectRegion = new Select(regionDropDown);
        selectRegion.selectByValue(regionName);
        return this;
    }

    public CreateFacilityPage withDistrict(String districtName) {
        Select selectDistrict = new Select(districtDropDown);
        selectDistrict.selectByValue(districtName);
        return this;
    }

    public CreateFacilityPage withSubDistrict(String subDistName) {
        Select selectSubDistrict = new Select(subDistDropDown);
        selectSubDistrict.selectByValue(subDistName);
        return this;
    }

    public CreateFacilityPage withPhoneNum() {
        phoneNumberInput.sendKeys(dataGenerator.getRandPhoneNum());
        return this;
    }

    public boolean submitDetails() {
        submitFacilityDetails.click();
        // if facility id is displayed in the next page then we presume facility created successfully
        return webDriverProvider.waitForElement_ID("facilityId");
    }

    public boolean updateFacilityDetails() {
        submitFacilityDetails.click();
        utilities.sleep(200);
        String srcPage = driver.getPageSource();
        return (srcPage.contains("Facility edited successfully"));
    }
}

