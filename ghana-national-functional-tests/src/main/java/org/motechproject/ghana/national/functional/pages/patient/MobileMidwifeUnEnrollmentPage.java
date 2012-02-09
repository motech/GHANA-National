package org.motechproject.ghana.national.functional.pages.patient;

import org.motechproject.ghana.national.functional.data.TestMobileMidwifeUnEnrollment;
import org.motechproject.ghana.national.functional.pages.home.HomePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class MobileMidwifeUnEnrollmentPage extends HomePage {

    @FindBy(id = "patientMotechId")
    @CacheLookup
    private WebElement patientMotechId;

    @FindBy(id = "staffMotechId")
    @CacheLookup
    private WebElement staffMotechId;

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

    @FindBy(id = "unregisterMobileMidwife")
    @CacheLookup
    private WebElement unregister;

    public MobileMidwifeUnEnrollmentPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitForElementID("unregisterMobileMidwife", driver);
        PageFactory.initElements(driver, this);
    }

    public MobileMidwifeUnEnrollmentPage withStaffMotechId(String staffId) {
        enter(this.staffMotechId, staffId);
        return this;
    }

    public MobileMidwifeUnEnrollmentPage withPatientMotechId(String staffId) {
        enter(this.patientMotechId, staffId);
        return this;
    }

    public MobileMidwifeUnEnrollmentPage withFacility(String facility) {
        Select selectFacility = new Select(this.facilities);
        selectFacility.selectByVisibleText(facility);
        return this;
    }

    public MobileMidwifeUnEnrollmentPage withCountry(String country) {
        Select selectCountry = new Select(this.country);
        selectCountry.selectByValue(country);
        return this;
    }

    public MobileMidwifeUnEnrollmentPage withRegion(String region) {
        Select selectRegion = new Select(this.region);
        selectRegion.selectByValue(region);
        return this;
    }

    public MobileMidwifeUnEnrollmentPage withDistrict(String district) {
        Select selectDistrict = new Select(this.district);
        selectDistrict.selectByValue(district);
        return this;
    }

    public MobileMidwifeUnEnrollmentPage withSubDistrict(String subDistrict) {
        Select selectSubDistrict = new Select(this.subDistrict);
        selectSubDistrict.selectByValue(subDistrict);
        return this;
    }


    private void unregister() {
        unregister.click();
    }

    @Override
    public void waitForSuccessfulCompletion() {
        elementPoller.waitForElementClassName("success", driver);
    }

    public void unenroll(TestMobileMidwifeUnEnrollment testMobileMidwifeUnEnrollment) {
        this.withStaffMotechId(testMobileMidwifeUnEnrollment.staffId())
                .withCountry(testMobileMidwifeUnEnrollment.country())
                .withRegion(testMobileMidwifeUnEnrollment.region())
                .withDistrict(testMobileMidwifeUnEnrollment.district())
                .withSubDistrict(testMobileMidwifeUnEnrollment.subDistrict())
                .withFacility(testMobileMidwifeUnEnrollment.facility())
                .unregister();
    }

    private String valueOf(WebElement webElement) {
        return webElement.getAttribute("value");
    }

    public TestMobileMidwifeUnEnrollment details() {
        return new TestMobileMidwifeUnEnrollment().staffId(valueOf(staffMotechId))
                .patientId(valueOf(patientMotechId))
                .country(valueOf(country))
                .district(valueOf(district))
                .subDistrict(valueOf(subDistrict))
                .region(valueOf(region))
                .facility(new Select(facilities).getFirstSelectedOption().getText());
    }

}
