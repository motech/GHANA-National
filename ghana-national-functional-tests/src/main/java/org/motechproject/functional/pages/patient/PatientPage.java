package org.motechproject.functional.pages.patient;

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

import java.util.Calendar;

@Component
public class PatientPage extends BasePage {

    @FindBy(id = "motechId")
    @CacheLookup
    private WebElement motechId;

    @FindBy(name = "registrationMode")
    @CacheLookup
    private WebElement selectRegnMode;

    @FindBy(id = "typeOfPatient")
    @CacheLookup
    private WebElement typeOfPatient;

    @FindBy(id = "firstName")
    @CacheLookup
    private WebElement firstName;

    @FindBy(id = "middleName")
    @CacheLookup
    private WebElement middleName;

    @FindBy(id = "lastName")
    @CacheLookup
    private WebElement lastName;

    @FindBy(id = "preferredName")
    @CacheLookup
    private WebElement preferredName;

    @FindBy(id = "dateOfBirthHolder")
    @CacheLookup
    private WebElement dateOfBirthHolder;

    @FindBy(id = "dateOfBirth")
    @CacheLookup
    private WebElement dateOfBirth;

    @FindBy(id = "estimatedDateOfBirth1")
    @CacheLookup
    private WebElement estimatedDateOfBirth;

    @FindBy(id = "estimatedDateOfBirth2")
    @CacheLookup
    private WebElement notEstimatedDateOfBirth;

    @FindBy(id = "sex1")
    @CacheLookup
    private WebElement male;

    @FindBy(id = "sex2")
    @CacheLookup
    private WebElement female;

    @FindBy(id = "insured")
    @CacheLookup
    private WebElement insured;

    @FindBy(id = "notInsured")
    @CacheLookup
    private WebElement notinsured;

    @FindBy(id = "nhisNumber")
    @CacheLookup
    private WebElement nhisNumber;

    @FindBy(id = "nhisExpDateHolder")
    @CacheLookup
    private WebElement nhisExpDateHolder;

    @FindBy(id = "nhisExpirationDate")
    @CacheLookup
    private WebElement nhisExpirationDate;

    @FindBy(id = "regions")
    @CacheLookup
    private WebElement region;

    @FindBy(id = "facilities")
    @CacheLookup
    private WebElement facility;

    @FindBy(id = "address")
    @CacheLookup
    private WebElement address;

    @FindBy(id = "submitNewPatient")
    @CacheLookup
    private WebElement submit;

    @FindBy(id = "districts")
    @CacheLookup
    private WebElement district;

    @FindBy(id = "sub-districts")
    @CacheLookup
    private WebElement subDistrict;

    @Autowired
    private HomePage homePage;


    private String submitNewPatientElementId = "submitNewPatient";
    private String successMessageClass = "success";

    public static enum PATIENT_REGN_MODE {AUTO_GENERATE_ID, USE_PREPRINTED_ID}

    public static enum PATIENT_TYPE {PATIENT_MOTHER, CHILD_UNDER_FIVE, OTHER}

    @Autowired
    public PatientPage(WebDriverProvider webDriverProvider) {
        super(webDriverProvider.getWebDriver());
    }

    public PatientPage withAddress(String address) {
        this.address.clear();
        this.address.sendKeys(address);
        return this;
    }

    public PatientPage withPreferredName(String preferredName) {
        this.preferredName.clear();
        this.preferredName.sendKeys(preferredName);
        return this;
    }

    public PatientPage withMotechId(String motechId) {
        this.motechId.clear();
        this.motechId.sendKeys(motechId);
        return this;
    }

    public PatientPage withRegistrationMode(PATIENT_REGN_MODE patientRegnMode) {
        Select selectRegnMode = new Select(this.selectRegnMode);
        selectRegnMode.selectByValue(patientRegnMode.name());
        return this;
    }

    public PatientPage withPatientType(PATIENT_TYPE patientType) {
        Select selectPatientType = new Select(typeOfPatient);
        selectPatientType.selectByValue(patientType.name());
        return this;
    }

    public PatientPage withFirstName(String firstName) {
        this.firstName.clear();
        this.firstName.sendKeys(firstName);
        return this;
    }

    public PatientPage withMiddleName(String middleName) {
        this.middleName.clear();
        this.middleName.sendKeys(middleName);
        return this;
    }

    public PatientPage withLastName(String lastName) {
        this.lastName.clear();
        this.lastName.sendKeys(lastName);
        return this;
    }

    public PatientPage withEstimatedDateOfBirth(Boolean estimatedDataOfBirth) {
        if (estimatedDataOfBirth)
            estimatedDateOfBirth.click();
        else
            notEstimatedDateOfBirth.click();
        return this;
    }

    public PatientPage withGender(Boolean gender) {
        if (gender)
            male.click();
        else
            female.click();
        return this;
    }

    public PatientPage withInsured(Boolean isInsured) {
        if (isInsured)
            insured.click();
        else {
            notinsured.click();
        }
        return this;
    }

    public PatientPage withNHIS(String nhisNumber) {
        this.nhisNumber.clear();
        this.nhisNumber.sendKeys(nhisNumber);
        return this;
    }


    public PatientPage withRegion(String region) {
        Select selectRegion = new Select(this.region);
        selectRegion.selectByValue(region);
        return this;
    }

    public PatientPage withDistrict(String district) {
        Select selectDistrict = new Select(this.district);
        selectDistrict.selectByValue(district);
        return this;
    }

    public PatientPage withSubDistrict(String subDistrict) {
        Select selectSubDist = new Select(this.subDistrict);
        selectSubDist.selectByValue(subDistrict);
        return this;
    }

    public PatientPage withFacility(String facility) {
        Select selectFacility = new Select(this.facility);
        selectFacility.selectByVisibleText(facility);
        return this;
    }

    public PatientPage withNhisExpiryDate(Calendar nhisExpiryDate) {
        dateSelector.select(nhisExpDateHolder, nhisExpiryDate, driver);
        return this;
    }

    public PatientPage withDateofBirth(Calendar dateOfBirth) {
        dateSelector.select(dateOfBirthHolder, dateOfBirth, driver);
        return this;
    }

    public void open() {
        homePage.openCreatePatientPage();
        waitForPageToLoad();
    }

    public void submit() {
        submit.click();
        waitForSuccessMessage();
        PageFactory.initElements(driver, this);
    }

    private void waitForSuccessMessage() {
        elementPoller.waitForElementClassName(successMessageClass, driver);
    }

    private void waitForPageToLoad() {
        elementPoller.waitForElementID(submitNewPatientElementId, driver);
        PageFactory.initElements(driver, this);
    }


}
