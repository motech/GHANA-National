package org.motechproject.functional.pages.patient;

import org.motechproject.functional.pages.home.HomePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PatientEditPage extends HomePage{

    public PatientEditPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitForElementID("submitNewPatient", driver);
        PageFactory.initElements(driver, this);
    }

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

    @FindBy(id = "mobileMidwifeEnrollmentLink")
    @CacheLookup
    private WebElement mobileMidwifeEnrollmentLink;

    @FindBy(id = "cwcenrollment")
    @CacheLookup
    WebElement cwcEnrollment;

    public void clickOnEnrollCWCLink() {
        cwcEnrollment.click();
    }

    public void clickEnrollMobileMidwifeLink() {
       mobileMidwifeEnrollmentLink.click();
    }

}
