package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.util.DataGenerator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public class CreatePatientPage {

    @FindBy(id = "motechId")
    @CacheLookup
    WebElement motechId;

    @FindBy(name = "registrationMode")
    @CacheLookup
    WebElement SelectRegnMode;

    @FindBy(id = "typeOfPatient")
    @CacheLookup
    WebElement typeOfPatient;

    @FindBy(id = "firstName")
    @CacheLookup
    WebElement firstName;

    @FindBy(id = "middleName")
    @CacheLookup
    WebElement middleName;

    @FindBy(id = "lastName")
    @CacheLookup
    WebElement lastName;

    @FindBy(id = "preferredName")
    @CacheLookup
    WebElement preferredName;

    @FindBy(id = "dateOfBirth")
    @CacheLookup
    WebElement dateOfBirth;

    @FindBy(id = "estimatedDateOfBirth1")
    @CacheLookup
    WebElement estimatedDateOfBirth;

    @FindBy(id = "estimatedDateOfBirth2")
    @CacheLookup
    WebElement notEstimatedDateOfBirth;

    @FindBy(id = "sex1")
    @CacheLookup
    WebElement male;

    @FindBy(id = "sex2")
    @CacheLookup
    WebElement female;

    @FindBy(id = "insured1")
    @CacheLookup
    WebElement insured;

    @FindBy(id = "insured2")
    @CacheLookup
    WebElement notinsured;

    @FindBy(id = "nhisNumber")
    @CacheLookup
    WebElement nhisNumber;

    @FindBy(id = "nhisExpirationDate")
    @CacheLookup
    WebElement nhisExpirationDate;

    @FindBy(id = "regions")
    @CacheLookup
    WebElement regionDropDown;

    @FindBy(id = "facilities")
    @CacheLookup
    WebElement facilityDropDown;

    @FindBy(id = "address")
    @CacheLookup
    WebElement address;

    @FindBy(id = "submitNewPatient")
    @CacheLookup
    WebElement submitNewPatient;

    @FindBy(id = "districts")
    @CacheLookup
    WebElement districtDropDown;

    @FindBy(id = "sub-districts")
    @CacheLookup
    WebElement subdistrictDropDown;

    @Autowired
    WebDriverProvider webDriverProvider;

    @Autowired
    DataGenerator dataGenerator;

    private WebDriver driver;

    public enum PATIENT_REGN_MODE {AUTO_GENERATE_ID, USE_PREPRINTED_ID}

    public enum PATIENT_TYPE {PATIENT_MOTHER, CHILD_UNDER_FIVE, OTHER}

    @Autowired
    public CreatePatientPage(WebDriverProvider webDriverProvider) {
        this.driver = webDriverProvider.getWebDriver();
    }

    public CreatePatientPage WithRegistrationMode(PATIENT_REGN_MODE patient_regn_mode) {
        Select selectRegnMode = new Select(SelectRegnMode);
        selectRegnMode.selectByValue(patient_regn_mode.name());
        return this;
    }

    public CreatePatientPage WithPatientType(PATIENT_TYPE patient_type) {
        Select selectPatientType = new Select(typeOfPatient);
        selectPatientType.selectByValue(patient_type.name());
        return this;
    }

    public CreatePatientPage WithPatientFirstName(String patientname) {
        firstName.sendKeys(patientname + dataGenerator.randomString(5));
        return this;
    }

    public CreatePatientPage WithPatientMiddleName(String midname) {
        middleName.sendKeys(midname);
        return this;
    }

    public CreatePatientPage WithPatientLastName(String lstname) {
        lastName.sendKeys(lstname);
        return this;
    }

    public CreatePatientPage WithDateofBirth(Calendar DOB) {

        driver.findElement(By.className("ui-datepicker-trigger")).click();
        WebElement month = driver.findElement(By.className("ui-datepicker-month"));
        Select selectmonth = new Select(month);
        selectmonth.selectByValue(Integer.toString(DOB.get(Calendar.MONTH)));
        WebElement Year = driver.findElement(By.className("ui-datepicker-year"));
        Select selectyear = new Select(Year);
        selectyear.selectByValue(Integer.toString(DOB.get(Calendar.YEAR)));
//       WebElement datefield = driver.findElement(By.className("ui-state-default"));
             WebElement table = driver.findElement(By.className("ui-datepicker-calendar"));

        List<WebElement> tds = table.findElements(By.tagName("td"));
        for (WebElement td : tds) {
            //Select 20th Date of the month
            if (td.getText().equals("20")) {
                td.findElement(By.linkText("20")).click();
                break;
            }
        }
            return this;
        }

    public CreatePatientPage WithEstimatedDOB(Boolean estimateddob) {
        if (estimateddob)
            estimatedDateOfBirth.click();
        else
            notEstimatedDateOfBirth.click();
        return this;
    }

    public CreatePatientPage WithPatientGender(Boolean Gender) {
        if (Gender)
            male.click();
        else
            female.click();
        return this;
    }

    public CreatePatientPage WithInsured(Boolean Insured) {
        if (Insured)
            insured.click();
        else
            notinsured.click();
        return this;
    }

    public CreatePatientPage WithNHIS(String NHISNumber) {
        nhisNumber.sendKeys(NHISNumber);
        return this;
    }

    public CreatePatientPage WithNHISExpirateDate(String expirationdate) {
        nhisExpirationDate.sendKeys(expirationdate);
        return this;
    }

    public CreatePatientPage WithRegion(String regionName) {
        Select selectRegion = new Select(regionDropDown);
        selectRegion.selectByValue(regionName);
        return this;
    }

    public CreatePatientPage WithDistrict(String districtName) {
        Select selectDistrict = new Select(districtDropDown);
        selectDistrict.selectByValue(districtName);
        return this;
    }

    public CreatePatientPage WithSubDistrict(String subdistrictName) {
        Select selectSubDist = new Select(subdistrictDropDown);
        selectSubDist.selectByValue(subdistrictName);
        return this;
    }

    public CreatePatientPage WithFacility(String facilityName) {
        Select selectFacility = new Select(facilityDropDown);
//        selectFacility.selectByValue(facilityName);
        selectFacility.selectByVisibleText(facilityName);

        return this;
    }

    public boolean Create() {
        submitNewPatient.click();
        webDriverProvider.WaitForElement_ID("footer");
        String src = driver.getPageSource();
        if (src.contains("Patient created successfully"))
            return true;
            else
        return false;
    }


}
