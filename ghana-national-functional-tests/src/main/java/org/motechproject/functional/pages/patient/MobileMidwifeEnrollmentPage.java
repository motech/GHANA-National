package org.motechproject.functional.pages.patient;

import org.motechproject.functional.pages.home.HomePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MobileMidwifeEnrollmentPage extends HomePage {

    @FindBy(id = "patientMotechId")
    @CacheLookup
    private WebElement patientMotechId;

    @FindBy(id = "staffMotechId")
    @CacheLookup
    private WebElement staffMotechId;

    @FindBy(id = "facilityMotechId")
    @CacheLookup
    private WebElement facilityMotechId;

    @FindBy(id = "consentYes")
    @CacheLookup
    private WebElement consentYes;

    @FindBy(id = "consentNo")
    @CacheLookup
    private WebElement consentNo;

    @FindBy(id = "serviceType")
    @CacheLookup
    private WebElement serviceType;

    @FindBy(id = "phoneOwnership")
    @CacheLookup
    private WebElement phoneOwnership;

    @FindBy(id = "phoneNumber")
    @CacheLookup
    private WebElement phoneNumber;

    @FindBy(id = "medium")
    @CacheLookup
    private WebElement medium;

    @FindBy(id = "dayOfWeek")
    @CacheLookup
    private WebElement dayOfWeek;

    @FindBy(id = "timeOfDay.hour")
    @CacheLookup
    private WebElement hourOfDay;

    @FindBy(id = "timeOfDay.minute")
    @CacheLookup
    private WebElement minuteOfDay;

    @FindBy(id = "language")
    @CacheLookup
    private WebElement language;

    @FindBy(id = "learnedFrom")
    @CacheLookup
    private WebElement learnedFrom;

    @FindBy(id = "reasonToJoin")
    @CacheLookup
    private WebElement reasonToJoin;

    @FindBy(id = "messageStartWeek")
    @CacheLookup
    private WebElement messageStartWeek;

    @FindBy(id = "submitMobileMidwife")
    @CacheLookup
    private WebElement submit;

    public MobileMidwifeEnrollmentPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitForElementID("submitMobileMidwife", driver);
        PageFactory.initElements(driver, this);
    }

    public MobileMidwifeEnrollmentPage withStaffMotechId(String staffId) {
        enter(this.staffMotechId, staffId);
        return this;
    }

    public MobileMidwifeEnrollmentPage withPatientMotechId(String staffId) {
        enter(this.patientMotechId, staffId);
        return this;
    }

    public MobileMidwifeEnrollmentPage withFacilityMotechId(String facilityMotechId) {
        enter(this.facilityMotechId, facilityMotechId);
        return this;
    }

    public MobileMidwifeEnrollmentPage withConsent(Boolean history) {
        if (history)
            consentYes.click();
        else
            consentNo.click();
        return this;
    }

    public MobileMidwifeEnrollmentPage withServiceType(String serviceType) {
        select(this.serviceType, serviceType);
        return this;
    }

    public MobileMidwifeEnrollmentPage withPhoneOwnership(String phoneOwnership) {
        select(this.phoneOwnership, phoneOwnership);
        return this;
    }

    public MobileMidwifeEnrollmentPage withPhoneNumber(String phoneNumber) {
        enter(this.phoneNumber, phoneNumber);
        return this;
    }

    public MobileMidwifeEnrollmentPage withMedium(String medium) {
        select(this.medium, medium);
        return this;
    }

    public MobileMidwifeEnrollmentPage withDayOfWeek(String dayOfWeek) {
        select(this.dayOfWeek, dayOfWeek);
        return this;
    }

    public MobileMidwifeEnrollmentPage withTime(String hour, String minute) {
        select(this.hourOfDay, hour);
        select(this.minuteOfDay, minute);
        return this;
    }

    public MobileMidwifeEnrollmentPage withLanguage(String language) {
        select(this.language, language);
        return this;
    }

    public MobileMidwifeEnrollmentPage withLearnedFrom(String learnedFrom) {
        select(this.learnedFrom, learnedFrom);
        return this;
    }

    public MobileMidwifeEnrollmentPage withReasonToJoin(String reasonToJoin) {
        select(this.reasonToJoin, reasonToJoin);
        return this;
    }

    public MobileMidwifeEnrollmentPage withMessageStartWeek(String messageStartWeek) {
        select(this.messageStartWeek, messageStartWeek);
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

    public String serviceType() {
        return attrValue(serviceType, "value");
    }

    public String medium() {
        return attrValue(medium, "value");
    }
}
