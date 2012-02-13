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
    private WebElement no;

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

    @FindBy(id = "bcgDateHolder")
    @CacheLookup
    private WebElement bcgDateHolder;

    @FindBy(id = "lastOPVDate")
    @CacheLookup
    private WebElement lastOPVDate;

    @FindBy(id = "lastOPVDateHolder")
    @CacheLookup
    private WebElement lastOPVDateHolder;

    @FindBy(id = "vitADate")
    @CacheLookup
    private WebElement lastVitaminADate;

    @FindBy(id = "vitADateHolder")
    @CacheLookup
    private WebElement lastVitaminADateHolder;

    @FindBy(id = "lastIPTiDate")
    @CacheLookup
    private WebElement lastIPTiDate;

    @FindBy(id = "lastIPTiDateHolder")
    @CacheLookup
    private WebElement lastIPTiDateHolder;

    @FindBy(id = "yfDate")
    @CacheLookup
    private WebElement yellowFeverDate;

    @FindBy(id = "yfDateHolder")
    @CacheLookup
    private WebElement yellowFeverDateHolder;

    @FindBy(id = "lastPentaDate")
    @CacheLookup
    private WebElement lastPentaDate;

    @FindBy(id = "lastPentaDateHolder")
    @CacheLookup
    private WebElement lastPentaDateHolder;

    @FindBy(id = "measlesDate")
    @CacheLookup
    private WebElement measlesDate;

    @FindBy(id = "measlesDateHolder")
    @CacheLookup
    private WebElement measlesDateHolder;

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

    @FindBy(id = "careHistory1")
    @CacheLookup
    private WebElement bcgCareHistory;

    @FindBy(id = "careHistory2")
    @CacheLookup
    private WebElement iptiCareHistory;

    @FindBy(id = "careHistory4")
    @CacheLookup
    private WebElement opvCareHistory;

    @FindBy(id = "careHistory5")
    @CacheLookup
    private WebElement pentaCareHistory;

    @FindBy(id = "careHistory6")
    @CacheLookup
    private WebElement vitaminAHistory;

    @FindBy(id = "careHistory3")
    @CacheLookup
    private WebElement measlesHistory;

    @FindBy(id = "careHistory7")
    @CacheLookup
    private WebElement yellowFeverHistory;

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
        if (lastOPV1.isSelected()) {
            return "0";
        } else if (lastOPV2.isSelected()) {
            return "1";
        } else if (lastOPV3.isSelected()) {
            return "2";
        } else if (lastOPV4.isSelected()) {
            return "3";
        }
        return "-1";
    }

    public String getLastIPTi() {
        if (lastIPTi1.isSelected()) {
            return "1";
        } else if (lastIPTi2.isSelected()) {
            return "2";
        } else if (lastIPTi3.isSelected()) {
            return "3";
        }
        return "-1";
    }

    public String getLastPenta() {
        if (lastPenta1.isSelected()) {
            return "1";
        } else if (lastPenta2.isSelected()) {
            return "2";
        } else if (lastPenta3.isSelected()) {
            return "3";
        }
        return "-1";
    }

    public String getLastOPVDate() {
        return lastOPVDate.getAttribute("value");
    }

    public String getLastVitaminADate() {
        return lastVitaminADate.getAttribute("value");
    }

    public void displaying(TestCWCEnrollment cwcEnrollmentDetails) {
        assertEquals(cwcEnrollmentDetails.getStaffId(), getStaffId());
        assertEquals(cwcEnrollmentDetails.getSerialNumber(), getSerialNumber());
        assertEquals(cwcEnrollmentDetails.getRegion(), getRegion());
        assertEquals(cwcEnrollmentDetails.getDistrict(), getDistrict());
        assertEquals(cwcEnrollmentDetails.getSubDistrict(), getSubDistrict());
        assertEquals(cwcEnrollmentDetails.getRegistrationDate().toString(DateTimeFormat.forPattern("dd/MM/yyyy")), getRegistrationDate());
        assertEquals(cwcEnrollmentDetails.getFacility(), getFacility());
        assertThat(getBcgDate(), is(nullSafeDateFormatting(cwcEnrollmentDetails.getBcgDate())));
        assertThat(getLastIPTiDate(), is(nullSafeDateFormatting(cwcEnrollmentDetails.getLastIPTiDate())));
        assertThat(getLastOPVDate(), is(nullSafeDateFormatting(cwcEnrollmentDetails.getLastOPVDate())));
        assertThat(getLastPentaDate(), is(nullSafeDateFormatting(cwcEnrollmentDetails.getLastPentaDate())));
        assertThat(getLastVitaminADate(), is(nullSafeDateFormatting(cwcEnrollmentDetails.getLastVitaminADate())));
        assertThat(getMeaslesDate(), is(nullSafeDateFormatting(cwcEnrollmentDetails.getMeaslesDate())));
        assertThat(getYellowFeverDate(), is(nullSafeDateFormatting(cwcEnrollmentDetails.getYellowFeverDate())));
        assertThat(nullSafeInteger(getLastIPTi()), is(cwcEnrollmentDetails.getLastIPTi()));
        assertThat(nullSafeInteger(getLastOPV()), is(cwcEnrollmentDetails.getLastOPV()));
        assertThat(nullSafeInteger(getLastPenta()), is(cwcEnrollmentDetails.getLastPenta()));
    }

    public String nullSafeDateFormatting(LocalDate date){
        return date == null? "": date.toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
    }

    public String nullSafeInteger(String value){
        return "-1".equals(value) ? null : value;
    }

    public void save(TestCWCEnrollment enrollment) {
        withStaffId(enrollment.getStaffId())
                .withSerialNumber(enrollment.getSerialNumber())
                .withCountry(enrollment.getCountry())
                .withRegion(enrollment.getRegion())
                .withDistrict(enrollment.getDistrict())
                .withSubDistrict(enrollment.getSubDistrict())
                .withFacility(enrollment.getFacility())
                .withRegistrationToday(enrollment.getRegistrationToday().name())
                .withRegistrationDate(enrollment.getRegistrationDate())
                .withAddHistory(enrollment.getAddHistory())
                .withHasBcgHistory(enrollment.hasBcgHistory())
                .withHasIPTiHistory(enrollment.hasIPTiHistory())
                .withHasOPVHistory(enrollment.hasOPVHistory())
                .withHasPentaHistory(enrollment.hasPentaHistory())
                .withHasLastVitaminAHistory(enrollment.hasVitaminAHistory())
                .withHasMeaslesHistory(enrollment.hasMeaslesHistory())
                .withHasYellowFeverHistory(enrollment.hasYellowFeverHistory());

        if(enrollment.hasBcgHistory()){
            withBcgDate(enrollment.getBcgDate());
        }
        if(enrollment.hasIPTiHistory()){
            withLastIPTiDate(enrollment.getLastIPTiDate())
                .withLastIPTi(enrollment.getLastIPTi());
        }
        if(enrollment.hasOPVHistory()){
            withLastOPVDate(enrollment.getLastOPVDate())
                .withLastOPV(enrollment.getLastOPV());
        }
        if(enrollment.hasPentaHistory()){
            withLastPentaDate(enrollment.getLastPentaDate())
                .withLastPenta(enrollment.getLastPenta());
        }
        if(enrollment.hasVitaminAHistory()){
            withLastVitaminADate(enrollment.getLastVitaminADate());
        }
        if(enrollment.hasMeaslesHistory()) {
            withMeaslesDate(enrollment.getMeaslesDate());
        }
        if(enrollment.hasYellowFeverHistory()) {
            withYellowFeverDate(enrollment.getYellowFeverDate());
        }
        submit();

    }

    private CWCEnrollmentPage withHasYellowFeverHistory(Boolean hasYellowFeverHistory) {
        boolean selected = yellowFeverHistory.isSelected();
        if (hasYellowFeverHistory && !selected || !hasYellowFeverHistory && selected)
            yellowFeverHistory.click();
        return this;
    }

    private CWCEnrollmentPage withHasMeaslesHistory(Boolean hasMeaslesHistory) {
        boolean selected = measlesHistory.isSelected();
        if (hasMeaslesHistory && !selected || !hasMeaslesHistory && selected)
            measlesHistory.click();
        return this;
    }

    private CWCEnrollmentPage withHasLastVitaminAHistory(Boolean hasVitaminAHistory) {
        boolean selected = vitaminAHistory.isSelected();
        if (hasVitaminAHistory && !selected || !hasVitaminAHistory && selected)
            vitaminAHistory.click();
        return this;
    }

    private CWCEnrollmentPage withHasPentaHistory(Boolean hasPentaHistory) {
        boolean selected = pentaCareHistory.isSelected();
        if (hasPentaHistory && !selected || !hasPentaHistory && selected)
            pentaCareHistory.click();
        return this;
    }

    private CWCEnrollmentPage withHasOPVHistory(Boolean hasOPVHistory) {
        boolean selected = opvCareHistory.isSelected();
        if (hasOPVHistory && !selected || !hasOPVHistory && selected)
            opvCareHistory.click();
        return this;
    }

    private CWCEnrollmentPage withHasIPTiHistory(Boolean hasIPTiHistory) {
        boolean selected = iptiCareHistory.isSelected();
        if (hasIPTiHistory && !selected || !hasIPTiHistory && selected)
            iptiCareHistory.click();
        return this;
    }

    private CWCEnrollmentPage withHasBcgHistory(Boolean hasBcgHistory) {
        boolean selected = bcgCareHistory.isSelected();
        if (hasBcgHistory && !selected || !hasBcgHistory && selected)
            bcgCareHistory.click();
        return this;
    }

    private CWCEnrollmentPage withLastPenta(String lastPenta) {
        if ("1".equals(lastPenta)) {
            lastPenta1.click();
        } else if ("2".equals(lastPenta)) {
            lastPenta2.click();
        } else if ("3".equals(lastPenta)) {
            lastPenta3.click();
        }
        return null;
    }

    private CWCEnrollmentPage withLastIPTi(String lastIPTi) {
        if ("1".equals(lastIPTi)) {
            lastIPTi1.click();
        } else if ("2".equals(lastIPTi)) {
            lastIPTi2.click();
        } else if ("3".equals(lastIPTi)) {
            lastIPTi3.click();
        }
        return this;
    }

    private CWCEnrollmentPage withLastOPV(String lastOPV) {
        if ("0".equals(lastOPV)) {
            lastOPV1.click();
        } else if ("1".equals(lastOPV)) {
            lastOPV2.click();
        } else if ("2".equals(lastOPV)) {
            lastOPV3.click();
        } else if ("3".equals(lastOPV)) {
            lastOPV4.click();
        }
        return this;
    }


    private CWCEnrollmentPage withYellowFeverDate(LocalDate yellowFeverDate) {
        dateSelector.select(yellowFeverDateHolder, yellowFeverDate, driver);
        return this;
    }

    private CWCEnrollmentPage withMeaslesDate(LocalDate measlesDate) {
        dateSelector.select(measlesDateHolder, measlesDate, driver);
        return this;
    }

    private CWCEnrollmentPage withLastVitaminADate(LocalDate lastVitaminADate) {
        dateSelector.select(lastVitaminADateHolder, lastVitaminADate, driver);
        return this;
    }

    private CWCEnrollmentPage withLastPentaDate(LocalDate lastPentaDate) {
        dateSelector.select(lastPentaDateHolder, lastPentaDate, driver);
        return this;
    }

    private CWCEnrollmentPage withLastOPVDate(LocalDate lastOPVDate) {
        dateSelector.select(lastOPVDateHolder, lastOPVDate, driver);
        return this;
    }

    private CWCEnrollmentPage withLastIPTiDate(LocalDate lastIPTiDate) {
        dateSelector.select(lastIPTiDateHolder, lastIPTiDate, driver);
        return this;
    }

    private CWCEnrollmentPage withBcgDate(LocalDate bcgDate) {
        dateSelector.select(bcgDateHolder, bcgDate, driver);
        return this;
    }
}
