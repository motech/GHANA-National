package org.motechproject.functional.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

@Component
public class CreatePatientPage {

    @FindBy(id = "motechId")
    @CacheLookup
    WebElement motechId;

    @FindBy(id = "registrationMode")
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
    WebElement radioestimatedDateOfBirth1;

    @FindBy(id = "estimatedDateOfBirth2")
    @CacheLookup
    WebElement radioestimatedDateOfBirth2;

    @FindBy(id = "sex1")
    @CacheLookup
    WebElement radiosex1;

    @FindBy(id = "sex2")
    @CacheLookup
    WebElement radiosex2;

    @FindBy(id = "insured1")
    @CacheLookup
    WebElement radioinsured1;

    @FindBy(id = "insured2")
    @CacheLookup
    WebElement radioinsured2;

    @FindBy(id = "nhisNumber")
    @CacheLookup
    WebElement nhisNumber;

    @FindBy(id = "nhisExpirationDate")
    @CacheLookup
    WebElement nhisExpirationDate;

    @FindBy(id = "regions")
    @CacheLookup
    WebElement selectregions;

    @FindBy(id = "facilities")
    @CacheLookup
    WebElement selectfacilities;

    @FindBy(id = "address")
    @CacheLookup
    WebElement address;

    @FindBy(id = "submitNewPatient")
    @CacheLookup
    WebElement submitNewPatient;

    public void SelectRegnMode() {

    }

}
