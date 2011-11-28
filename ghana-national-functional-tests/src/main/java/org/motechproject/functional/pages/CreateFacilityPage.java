package org.motechproject.functional.pages;

import org.motechproject.functional.base.MyDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;


public class CreateFacilityPage {
    WebDriver driver = MyDriver.getDriverInstance();
    HomePage homePage = new HomePage();
    LoginPage loginPage = new LoginPage();


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

    @FindBy(id = "submitNewFacility")
    @CacheLookup
    WebElement SubmitFacilityDetails;


    public boolean IsRegionDisplayed() {
        if (RegionDropDown.isDisplayed())
            return true;
        else
            return false;

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
        String successmsg;
        successmsg = driver.getPageSource();
        if (successmsg.contains("Facility created successfully"))
            return true;
        else
            return false;
    }
}
