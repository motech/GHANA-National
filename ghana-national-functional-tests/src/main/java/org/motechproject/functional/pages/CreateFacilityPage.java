package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateFacilityPage {
    public String FACILITY_ID="";
    private WebDriver driver;

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

    @FindBy(id="id")
    @CacheLookup
    WebElement facilityid;

    @Autowired
    public CreateFacilityPage(WebDriverProvider webDriverProvider) {
        this.driver = webDriverProvider.getWebDriver();
    }


    public boolean IsRegionDisplayed() {
        return RegionDropDown.isDisplayed();

    }

    public void SetFacilityName(String facilityName) {
        FacilityNameInput.sendKeys(facilityName);
    }

    public String GetFacilityName() {
        return FacilityNameInput.getText();
    }

    public void SelectCountry(String CountryName) {
        Select selectCountry = new Select(CountryDropDown);
        selectCountry.selectByValue(CountryName);
    }

    public void SetRegionName(String RegionName) {
        Select selectRegion = new Select(RegionDropDown);
        selectRegion.selectByValue(RegionName);
    }

    public void SelectDistrict(String DistrictName) {
        Select selectDistrict = new Select(DistrictDropDown);
        selectDistrict.selectByValue(DistrictName);
    }

    public void SelectSubDistrict(String SubDistName) {
        Select selectSubDistrict = new Select(SubDistDropDown);
        selectSubDistrict.selectByValue(SubDistName);
    }

    public void SetPhoneNum(String PhoneNum) {
        PhoneNumberInput.sendKeys(PhoneNum);
    }

    public boolean SubmitDetails() {
              SubmitFacilityDetails.click();

    // if facility id is displayed in the next page then we presume facility created successfully
        if (driver.findElement(By.id("id")).isDisplayed() )
        {
            /*todo Add methods to retrieve the facility id from the text box and store it */
            /*this.FACILITY_ID=facilityid.getText();
            System.out.println("facility id" + this.FACILITY_ID);
*/            return true;
        }else
            return false;
    }

    public HomePage getHomePage() {
        return homePage;
    }

    public void setHomePage(HomePage homePage) {
        this.homePage = homePage;
    }

    public LoginPage getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(LoginPage loginPage) {
        this.loginPage = loginPage;
    }
}

