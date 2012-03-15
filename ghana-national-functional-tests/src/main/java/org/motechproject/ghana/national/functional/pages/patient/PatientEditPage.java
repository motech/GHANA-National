package org.motechproject.ghana.national.functional.pages.patient;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PatientEditPage extends PatientPage{

    public PatientEditPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitForElementID("submitNewPatient", driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "mobileMidwifeEnrollmentLink")
    @CacheLookup
    private WebElement mobileMidwifeEnrollmentLink;

    @FindBy(id = "mobileMidwifeUnenrollmentLink")
    @CacheLookup
    private WebElement mobileMidwifeUnEnrollmentLink;

    @FindBy(id = "cwcenrollment")
    @CacheLookup
    WebElement cwcEnrollment;

    @FindBy(id = "ancenrollment")
    @CacheLookup
    public WebElement ancEnrollment;

    public void clickOnEnrollCWCLink() {
        cwcEnrollment.click();
    }

    public void clickOnEnrollANCLink() {
        ancEnrollment.click();
    }

    public void clickEnrollMobileMidwifeLink() {
       mobileMidwifeEnrollmentLink.click();
    }

    public void clickUnEnrollMobileMidwifeLink() {
       mobileMidwifeUnEnrollmentLink.click();
    }
}
