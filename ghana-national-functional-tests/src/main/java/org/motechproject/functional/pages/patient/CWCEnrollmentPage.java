package org.motechproject.functional.pages.patient;

import org.joda.time.LocalDate;
import org.motechproject.functional.pages.home.HomePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class CWCEnrollmentPage extends HomePage {

    @FindBy(id = "patientMotechId")
    @CacheLookup
    private WebElement patientMotechId;

    @FindBy(id = "staffId")
    @CacheLookup
    private WebElement staffId;

    @FindBy(id = "serialNumber")
    @CacheLookup
    private WebElement serialNumber;

    @FindBy(id = "registrationToday")
    @CacheLookup
    private WebElement registrationToday;

    @FindBy(id = "registrationDate")
    @CacheLookup
    private WebElement registrationDate;

    @FindBy(id = "registraionDateHolder")
    @CacheLookup
    private WebElement registraionDateHolder;

    @FindBy(id = "addHistory1")
    @CacheLookup
    private WebElement yes;

    @FindBy(id = "addHistory2")
    @CacheLookup
    private WebElement  no;

    @FindBy(id = "countries")
    @CacheLookup
    private WebElement country;

    @FindBy(id = "facilities")
    @CacheLookup
    private WebElement facilities;

    @FindBy(id = "districts")
    @CacheLookup
    private WebElement district;

    @FindBy(id = "sub-districts")
    @CacheLookup
    private WebElement subDistrict;

    @FindBy(id = "regions")
    @CacheLookup
    private WebElement region;

    @FindBy(id = "enrollCWC")
    @CacheLookup
    private WebElement submit;
    
    public CWCEnrollmentPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitForElementID("enrollCWC", driver);
        PageFactory.initElements(driver, this);
    }

    public CWCEnrollmentPage withStaffId(String staffId) {
        this.staffId.clear();
        this.staffId.sendKeys(staffId);
        return this;
    }

    public CWCEnrollmentPage withSerialNumber(String serialNumber) {
        this.serialNumber.clear();
        this.serialNumber.sendKeys(serialNumber);
        return this;
    }

    public CWCEnrollmentPage withRegistrationToday(String registrationToday) {
        Select selectSubDist = new Select(this.registrationToday);
        selectSubDist.selectByValue(registrationToday);
        return this;
    }

    public CWCEnrollmentPage withRegistrationDate(LocalDate registrationDate) {
        dateSelector.select(registraionDateHolder, registrationDate, driver);
        return this;
    }

    public CWCEnrollmentPage withFacility(String facility) {
        Select selectFacility = new Select(this.facilities);
        selectFacility.selectByVisibleText(facility);
        return this;
    }

    public CWCEnrollmentPage withCountry(String country) {
        Select selectCountry = new Select(this.country);
        selectCountry.selectByValue(country);
        return this;
    }

    public CWCEnrollmentPage withRegion(String region) {
        Select selectRegion = new Select(this.region);
        selectRegion.selectByValue(region);
        return this;
    }

    public CWCEnrollmentPage withDistrict(String district) {
        Select selectDistrict = new Select(this.district);
        selectDistrict.selectByValue(district);
        return this;
    }

    public CWCEnrollmentPage withSubDistrict(String subDistrict) {
        Select selectSubDistrict = new Select(this.subDistrict);
        selectSubDistrict.selectByValue(subDistrict);
        return this;
    }

    public CWCEnrollmentPage withAddHistory(Boolean history) {
        if (history)
            yes.click();
        else
            no.click();
        return this;
    }

    public void submit() {
        submit.click();
        waitForSuccessfulCompletion();
    }

    @Override
    public void waitForSuccessfulCompletion() {
        elementPoller.waitForElementClassName("success", driver);
    }
}
