package org.motechproject.ghana.national.functional.pages.patient;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.pages.home.HomePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PatientPage extends HomePage {
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

    @FindBy(id = "staffId")
    @CacheLookup
    private WebElement staffId;

    public PatientPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitForElementID("submitNewPatient", driver);
        PageFactory.initElements(driver, this);
    }

    public PatientPage withAddress(String address) {
        this.address.clear();
        this.address.sendKeys(address);
        return this;
    }

    public PatientPage withMotechId(String motechId) {
        this.motechId.clear();
        this.motechId.sendKeys(motechId);
        return this;
    }

    public PatientPage withStaffId(String staffId) {
        this.staffId.clear();
        this.staffId.sendKeys(staffId);
        return this;
    }

    public PatientPage withRegistrationMode(TestPatient.PATIENT_REGN_MODE patientRegnMode) {
        Select selectRegnMode = new Select(this.selectRegnMode);
        selectRegnMode.selectByValue(patientRegnMode.name());
        return this;
    }

    public PatientPage withPatientType(TestPatient.PATIENT_TYPE patientType) {
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
            female.click();
        else
            male.click();
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

    public PatientPage withNhisExpiryDate(LocalDate nhisExpiryDate) {
        dateSelector.select(nhisExpDateHolder, nhisExpiryDate, driver);
        return this;
    }

    public PatientPage withDateofBirth(LocalDate dateOfBirth) {
        dateSelector.select(dateOfBirthHolder, dateOfBirth, driver);
        return this;
    }

    public void submit() {
        submit.click();
    }

    public void create(TestPatient patient) {
        PatientPage patientPage = this.withRegistrationMode(patient.registrationMode())
                .withEstimatedDateOfBirth(patient.estimatedDateOfBirth())
                .withInsured(patient.insured())
                .withPatientType(patient.patientType())
                .withFirstName(patient.firstName())
                .withMiddleName(patient.middleName())
                .withLastName(patient.lastName())
                .withRegion(patient.region())
                .withDistrict(patient.district())
                .withSubDistrict(patient.subDistrict())
                .withFacility(patient.facility())
                .withAddress(patient.address())
                .withGender(patient.isFemale())
                .withDateofBirth(patient.dateOfBirth())
                .withStaffId(patient.staffId());
        if (patient.hasMotechId())
            patientPage.withMotechId(patient.motechId());
        patientPage.submit();
    }

    public void displaying(TestPatient patient) {
        assertThat(valueOf(firstName), is(equalTo(patient.firstName())));
        assertThat(valueOf(middleName), is(equalTo(patient.middleName())));
        assertThat(valueOf(lastName), is(equalTo(patient.lastName())));
        assertThat(valueOf(dateOfBirth), is(equalTo(patient.dateOfBirth().toString(DateTimeFormat.forPattern("dd/MM/yyyy")))));
        assertThat(getEstimatedDateOfBirth(), is(equalTo(patient.estimatedDateOfBirth())));
        assertThat(getGenderCode(), is(equalTo(patient.genderCode())));
        assertThat(isInsured(), is(equalTo(patient.insured())));
        assertThat(valueOf(region), is(equalTo(patient.region())));
        assertThat(selectedOption(facility), is(equalTo(patient.facility())));
        assertThat(valueOf(address), is(equalTo(patient.address())));
        assertThat(valueOf(district), is(equalTo(patient.district())));
        assertThat(valueOf(subDistrict), is(equalTo(patient.subDistrict())));
    }

    private Boolean isInsured() {
        if (insured.isSelected()) {
            return true;
        } else if (notinsured.isSelected()) {
            return false;
        }
        return null;
    }

    private String getGenderCode() {
        if (male.isSelected()) {
            return "M";
        } else if (female.isSelected()) {
            return "F";
        }
        return null;
    }

    private String valueOf(WebElement webElement) {
        return webElement.getAttribute("value");
    }

    private String selectedOption(WebElement webElement) {
        return new Select(webElement).getFirstSelectedOption().getText();
    }

    public String motechId() {
        return motechId.getAttribute("value");
    }

    @Override
    public void waitForSuccessfulCompletion() {
        elementPoller.waitForElementClassName("success", driver);
    }

    public Boolean getEstimatedDateOfBirth() {
        if (estimatedDateOfBirth.isSelected()) {
            return true;
        } else if (notEstimatedDateOfBirth.isSelected()) {
            return false;
        }
        return null;
    }
}
