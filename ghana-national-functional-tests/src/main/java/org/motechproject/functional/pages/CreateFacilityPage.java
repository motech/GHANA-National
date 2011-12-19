package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.functional.util.Utilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateFacilityPage {
    protected String FACILITY_ID,facilityName,country,regionName,districtName,subDistName,facilityPhone;

    private WebDriver driver;
    private Logger log = LoggerFactory.getLogger(CreateFacilityPage.class);

    @Autowired
    private HomePage homePage;

    @Autowired
    private LoginPage loginPage;

    @FindBy(name = "country")
    @CacheLookup
    WebElement CountryDropDown;

    @FindBy(name = "name")
    @CacheLookup
    WebElement FacilityNameInput;

    @FindBy(name = "countyDistrict")
    @CacheLookup
    WebElement DistrictDropDown;

    @FindBy(name = "stateProvince")
    @CacheLookup
    WebElement SubDistDropDown;

    @FindBy(name = "phoneNumber")
    @CacheLookup
    WebElement PhoneNumberInput;

    @FindBy(name = "additionalPhoneNumber1")
    @CacheLookup
    WebElement PhoneNum1;

    @FindBy(name = "additionalPhoneNumber2")
    @CacheLookup
    WebElement PhoneNum2;

    @FindBy(name = "additionalPhoneNumber3")
    @CacheLookup
    WebElement PhoneNum3;

    @FindBy(name = "region")
    @CacheLookup
    WebElement RegionDropDown;

    @FindBy(id = "submitFacility")
    @CacheLookup
    WebElement SubmitFacilityDetails;

    @Autowired
    public CreateFacilityPage(WebDriverProvider webDriverProvider) {
        this.driver = webDriverProvider.getWebDriver();
    }

    @Autowired
    WebDriverProvider webDriverProvider;

    @Autowired
    DataGenerator dataGenerator;

    @Autowired
    Utilities myUtilities;
    public boolean IsRegionDisplayed() {
        return RegionDropDown.isDisplayed();
    }

    public CreateFacilityPage WithFacilityName(String facilityName) {
        FacilityNameInput.clear();
        FacilityNameInput.sendKeys(facilityName + dataGenerator.randomString(5));
        return this;
    }

    public String GetFacilityName() {
        return FacilityNameInput.getText();
    }

    public CreateFacilityPage withCountry(String CountryName) {
        Select selectCountry = new Select(CountryDropDown);
        selectCountry.selectByValue(CountryName);
        return this;
    }

    public CreateFacilityPage withRegionName(String RegionName) {
        Select selectRegion = new Select(RegionDropDown);
        selectRegion.selectByValue(RegionName);
        return this;
    }

    public CreateFacilityPage withDistrict(String DistrictName) {
        Select selectDistrict = new Select(DistrictDropDown);
        selectDistrict.selectByValue(DistrictName);
        return this;
    }

    public CreateFacilityPage withSubDistrict(String SubDistName) {
        Select selectSubDistrict = new Select(SubDistDropDown);
        selectSubDistrict.selectByValue(SubDistName);
        return this;
    }

    public CreateFacilityPage withPhoneNum() {
        PhoneNumberInput.sendKeys(dataGenerator.getRandPhoneNum());
        return this;
    }

    public boolean SubmitDetails() {
        SubmitFacilityDetails.click();
        // if facility id is displayed in the next page then we presume facility created successfully
        return webDriverProvider.WaitForElement_ID("facilityId");
    }

    public boolean UpdateFacilityDetails() {
        SubmitFacilityDetails.click();
        myUtilities.mySleep(20);
        String srcPage = driver.getPageSource();
        return (srcPage.contains("Facility edited successfully"));
    }
}

