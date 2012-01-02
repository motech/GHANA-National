package org.motechproject.functional.pages.facility;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.BasePage;
import org.motechproject.functional.pages.home.HomePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FacilityPage extends BasePage {

    @FindBy(name = "country")
    @CacheLookup
    private WebElement country;

    @FindBy(name = "name")
    @CacheLookup
    private WebElement name;

    @FindBy(name = "countyDistrict")
    @CacheLookup
    private WebElement district;

    @FindBy(name = "stateProvince")
    @CacheLookup
    private WebElement subDistrict;

    @FindBy(name = "phoneNumber")
    @CacheLookup
    private WebElement phoneNumber;

    @FindBy(name = "region")
    @CacheLookup
    private WebElement region;

    @FindBy(id = "submitFacility")
    @CacheLookup
    private WebElement submit;

    private String successMessageClass = "success";
    private String submitFacilityElementId = "submitFacility";

    @Autowired
    public FacilityPage(WebDriverProvider webDriverProvider) {
        super(webDriverProvider.getWebDriver());
    }

    @Autowired
    HomePage homePage;

    public FacilityPage withName(String name) {
        this.name.clear();
        this.name.sendKeys(name);
        return this;
    }

    public FacilityPage withCountry(String country) {
        Select selectCountry = new Select(this.country);
        selectCountry.selectByValue(country);
        return this;
    }

    public FacilityPage withRegion(String region) {
        Select selectRegion = new Select(this.region);
        selectRegion.selectByValue(region);
        return this;
    }

    public FacilityPage withDistrict(String district) {
        Select selectDistrict = new Select(this.district);
        selectDistrict.selectByValue(district);
        return this;
    }

    public FacilityPage withSubDistrict(String subDistrict) {
        Select selectSubDistrict = new Select(this.subDistrict);
        selectSubDistrict.selectByValue(subDistrict);
        return this;
    }

    public FacilityPage withPhoneNum(String phoneNumber) {
        this.phoneNumber.clear();
        this.phoneNumber.sendKeys(phoneNumber);
        return this;
    }

    public void open() {
        homePage.openCreateFaclityPage();
        waitForPageToLoad();
    }

    public void submit() {
        submit.click();
        waitForSuccessMessage();
    }

    public void waitForSuccessMessage(){
        elementPoller.waitForElementClassName(successMessageClass, driver);
    }

    public void waitForPageToLoad() {
        elementPoller.waitForElementID(submitFacilityElementId, driver);
        PageFactory.initElements(driver, this);
    }

}

