package org.motechproject.ghana.national.functional.pages.patient;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ghana.national.functional.data.TestCWCEnrollment;
import org.motechproject.ghana.national.functional.pages.home.HomePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import static com.thoughtworks.selenium.SeleneseTestBase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
    private WebElement facility;

    @FindBy(id = "districts")
    @CacheLookup
    private WebElement district;

    @FindBy(id = "sub-districts")
    @CacheLookup
    private WebElement subDistrict;

    @FindBy(id = "regions")
    @CacheLookup
    private WebElement region;

    @FindBy(id = "bcgDate")
    @CacheLookup
    private WebElement bcgDate;

    @FindBy(id = "lastOPVDate")
    @CacheLookup
    private WebElement lastOPVDate;

    @FindBy(id = "vitADate")
    @CacheLookup
    private WebElement lastVitaminADate;

    @FindBy(id = "lastIPTiDate")
    @CacheLookup
    private WebElement lastIPTiDate;

    @FindBy(id = "yfDate")
    @CacheLookup
    private WebElement yellowFeverDate;

    @FindBy(id = "lastPentaDate")
    @CacheLookup
    private WebElement lastPentaDate;

    @FindBy(id = "measlesDate")
    @CacheLookup
    private WebElement measlesDate;

    @FindBy(id = "lastOPV")
    @CacheLookup
    private WebElement lastOPV;

    @FindBy(id = "lastOPV1")
    @CacheLookup
    private WebElement lastOPV1;

    @FindBy(id = "lastOPV2")
    @CacheLookup
    private WebElement lastOPV2;

    @FindBy(id = "lastOPV3")
    @CacheLookup
    private WebElement lastOPV3;

    @FindBy(id = "lastOPV4")
    @CacheLookup
    private WebElement lastOPV4;

    @FindBy(id = "lastIPTi")
    @CacheLookup
    private WebElement lastIPTi;

    @FindBy(id = "lastIPTi1")
    @CacheLookup
    private WebElement lastIPTi1;

    @FindBy(id = "lastIPTi2")
    @CacheLookup
    private WebElement lastIPTi2;

    @FindBy(id = "lastIPTi3")
    @CacheLookup
    private WebElement lastIPTi3;

    @FindBy(id = "lastPenta")
    @CacheLookup
    private WebElement lastPenta;

    @FindBy(id = "lastPenta1")
    @CacheLookup
    private WebElement lastPenta1;

    @FindBy(id = "lastPenta2")
    @CacheLookup
    private WebElement lastPenta2;

    @FindBy(id = "lastPenta3")
    @CacheLookup
    private WebElement lastPenta3;

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
        Select selectFacility = new Select(this.facility);
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

    public String getPatientMotechId() {
        return patientMotechId.getAttribute("value");
    }

    public String getStaffId() {
        return staffId.getAttribute("value");
    }

    public String getSerialNumber() {
        return serialNumber.getAttribute("value");
    }

    public String getRegistrationToday() {
        return registrationToday.getAttribute("value");
    }

    public String getRegistrationDate() {
        return registrationDate.getAttribute("value");
    }


    public String getCountry() {
        return country.getAttribute("value");
    }

    public String getFacility() {
        return new Select(facility).getFirstSelectedOption().getText();
    }

    public String getDistrict() {
        return district.getAttribute("value");
    }

    public String getSubDistrict() {
        return subDistrict.getAttribute("value");
    }

    public String getRegion() {
        return region.getAttribute("value");
    }

    public String getBcgDate() {
        return bcgDate.getAttribute("value");
    }

    public String getLastIPTiDate() {
        return lastIPTiDate.getAttribute("value");
    }

    public String getYellowFeverDate() {
        return yellowFeverDate.getAttribute("value");
    }

    public String getLastPentaDate() {
        return lastPentaDate.getAttribute("value");
    }

    public String getMeaslesDate() {
        return measlesDate.getAttribute("value");
    }

    public String getLastOPV() {
        if(lastOPV1.isSelected()){
            return "0";
        }else if (lastOPV2.isSelected()){
            return "1";
        }else if (lastOPV3.isSelected()){
            return "2";
        }else if (lastOPV4.isSelected()){
            return "3";
        }
        return "-1";
    }

    public String getLastIPTi() {
        if(lastIPTi1.isSelected()){
            return "1";
        }else if(lastIPTi2.isSelected()){
            return "2";
        }else if(lastIPTi3.isSelected()){
            return "3";
        }
        return "-1";
    }

    public String getLastPenta() {
        if(lastPenta1.isSelected()){
            return "1";
        }else if (lastPenta2.isSelected()){
            return "2";
        }else if(lastPenta3.isSelected()){
            return "3";
        }
        return "4";
    }

    public String getLastOPVDate() {
        return lastOPVDate.getAttribute("value");
    }

    public String getLastVitaminADate() {
        return lastVitaminADate.getAttribute("value");
    }

    public void displaying(TestCWCEnrollment cwcEnrollmentDetails) {
        assertEquals(getStaffId(), cwcEnrollmentDetails.getStaffId());
        assertEquals(getSerialNumber(), cwcEnrollmentDetails.getSerialNumber());
        assertEquals(getRegion(), cwcEnrollmentDetails.getRegion());
        assertEquals(getDistrict(), cwcEnrollmentDetails.getDistrict());
        assertEquals(getSubDistrict(), cwcEnrollmentDetails.getSubDistrict());
        assertEquals(getRegistrationDate(), cwcEnrollmentDetails.getRegistrationDate().toString(DateTimeFormat.forPattern("dd/MM/yyyy")));
        assertEquals(getFacility(), cwcEnrollmentDetails.getFacility());
        assertThat(getBcgDate(),is(cwcEnrollmentDetails.getBcgDate().toString(DateTimeFormat.forPattern("dd/MM/yyyy"))));
        assertThat(getLastIPTiDate(),is(cwcEnrollmentDetails.getLastIPTiDate().toString(DateTimeFormat.forPattern("dd/MM/yyyy"))));
        assertThat(getLastOPVDate(),is(cwcEnrollmentDetails.getLastOPVDate().toString(DateTimeFormat.forPattern("dd/MM/yyyy"))));
        assertThat(getLastPentaDate(),is(cwcEnrollmentDetails.getLastPentaDate().toString(DateTimeFormat.forPattern("dd/MM/yyyy"))));
        assertThat(getLastVitaminADate(),is(cwcEnrollmentDetails.getLastVitaminADate().toString(DateTimeFormat.forPattern("dd/MM/yyyy"))));
        assertThat(getMeaslesDate(),is(cwcEnrollmentDetails.getMeaslesDate().toString(DateTimeFormat.forPattern("dd/MM/yyyy"))));
        assertThat(getYellowFeverDate(),is(cwcEnrollmentDetails.getYellowFeverDate().toString(DateTimeFormat.forPattern("dd/MM/yyyy"))));
        assertThat(getLastIPTi(),is(cwcEnrollmentDetails.getLastIPTi().toString()));
        assertThat(getLastOPV(),is(cwcEnrollmentDetails.getLastOPV().toString()));
        assertThat(getLastPenta(),is(cwcEnrollmentDetails.getLastPenta().toString()));

    }
}
