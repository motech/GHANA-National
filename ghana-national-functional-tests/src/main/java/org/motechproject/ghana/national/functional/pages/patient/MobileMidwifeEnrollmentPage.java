package org.motechproject.ghana.national.functional.pages.patient;

import org.motechproject.ghana.national.functional.data.TestMobileMidwifeEnrollment;
import org.motechproject.ghana.national.functional.pages.home.HomePage;
import org.motechproject.ghana.national.domain.mobilemidwife.MessageStartWeek;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class MobileMidwifeEnrollmentPage extends HomePage {

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

    @FindBy(id = "timeOfDayHour")
    @CacheLookup
    private WebElement hourOfDay;

    @FindBy(id = "timeOfDayMinute")
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

    public MobileMidwifeEnrollmentPage withConsent(Boolean consent) {
        if (consent)
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

    public MobileMidwifeEnrollmentPage withFacility(String facility) {
        Select selectFacility = new Select(this.facilities);
        selectFacility.selectByVisibleText(facility);
        return this;
    }

    public MobileMidwifeEnrollmentPage withCountry(String country) {
        Select selectCountry = new Select(this.country);
        selectCountry.selectByValue(country);
        return this;
    }

    public MobileMidwifeEnrollmentPage withRegion(String region) {
        Select selectRegion = new Select(this.region);
        selectRegion.selectByValue(region);
        return this;
    }

    public MobileMidwifeEnrollmentPage withDistrict(String district) {
        Select selectDistrict = new Select(this.district);
        selectDistrict.selectByValue(district);
        return this;
    }

    public MobileMidwifeEnrollmentPage withSubDistrict(String subDistrict) {
        Select selectSubDistrict = new Select(this.subDistrict);
        selectSubDistrict.selectByValue(subDistrict);
        return this;
    }


    private void submit() {
        submit.click();
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

    public WebElement getDayOfWeek() {
        return dayOfWeek;
    }

    public WebElement getHourOfDay() {
        return hourOfDay;
    }

    public void enroll(TestMobileMidwifeEnrollment testMobileMidwifeEnrollment) {
        this.withStaffMotechId(testMobileMidwifeEnrollment.staffId())
                .withConsent(testMobileMidwifeEnrollment.consent())
                .withCountry(testMobileMidwifeEnrollment.country())
                .withRegion(testMobileMidwifeEnrollment.region())
                .withDistrict(testMobileMidwifeEnrollment.district())
                .withSubDistrict(testMobileMidwifeEnrollment.subDistrict())
                .withFacility(testMobileMidwifeEnrollment.facility())
                .withServiceType(testMobileMidwifeEnrollment.serviceType())
                .withPhoneOwnership(testMobileMidwifeEnrollment.phoneOwnership())
                .withPhoneNumber(testMobileMidwifeEnrollment.phoneNumber())
                .withMedium(testMobileMidwifeEnrollment.medium())
                .withLanguage(testMobileMidwifeEnrollment.language())
                .withLearnedFrom(testMobileMidwifeEnrollment.learnedFrom())
                .withReasonToJoin(testMobileMidwifeEnrollment.reasonToJoin())
                .withMessageStartWeek(testMobileMidwifeEnrollment.messageStartWeek())
                .submit();
    }

    private String valueOf(WebElement webElement) {
        return webElement.getAttribute("value");
    }

    public Boolean consent() {
        if (consentYes.isSelected()) {
            return Boolean.TRUE;
        } else if (consentNo.isSelected()) {
            return Boolean.FALSE;
        } else {
            return null;
        }
    }

    public TestMobileMidwifeEnrollment details() {
        return new TestMobileMidwifeEnrollment().staffId(valueOf(staffMotechId))
                .patientId(valueOf(patientMotechId))
                .consent(consent())
                .country(valueOf(country))
                .district(valueOf(district))
                .subDistrict(valueOf(subDistrict))
                .region(valueOf(region))
                .facility(new Select(facilities).getFirstSelectedOption().getText())
                .serviceType(valueOf(serviceType))
                .phoneOwnership(valueOf(phoneOwnership))
                .phoneNumber(valueOf(phoneNumber))
                .medium(valueOf(medium))
                .language(valueOf(language))
                .learnedFrom(valueOf(learnedFrom))
                .reasonToJoin(valueOf(reasonToJoin))
                .messageStartWeek(MessageStartWeek.valueOf(valueOf(messageStartWeek)));
    }

}
