package org.motechproject.functional.framework;

import org.motechproject.functional.TestEnvironmentConfiguration;
import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.pages.BasePage;
import org.motechproject.functional.pages.facility.FacilityPage;
import org.motechproject.functional.pages.facility.SearchFacilityPage;
import org.motechproject.functional.pages.home.HomePage;
import org.motechproject.functional.pages.login.LoginPage;
import org.motechproject.functional.pages.openmrs.MotechIdGeneratorPage;
import org.motechproject.functional.pages.openmrs.OpenMRSHomePage;
import org.motechproject.functional.pages.openmrs.OpenMRSLoginPage;
import org.motechproject.functional.pages.patient.*;
import org.motechproject.functional.pages.staff.SearchStaffPage;
import org.motechproject.functional.pages.staff.StaffPage;
import org.motechproject.functional.util.ScreenShotCaptor;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Browser {
    private WebDriver webDriver;
    private ApplicationURLs applicationURLs;

    @Autowired
    public Browser(WebDriverProvider webDriverProvider, TestEnvironmentConfiguration configuration) {
        applicationURLs = new ApplicationURLs(configuration);
        this.webDriver = webDriverProvider.getWebDriver();
    }

    public LoginPage gotoLoginPage() {
        webDriver.get(applicationURLs.forHomePage());
        return new LoginPage(webDriver);
    }

    public HomePage homePage() {
        return new HomePage(webDriver);
    }

    public PatientPage toCreatePatient(HomePage fromPage) {
        fromPage.waitForSuccessfulCompletion();
        fromPage.openCreatePatientPage();
        return new PatientPage(webDriver);
    }

    public StaffPage getStaffPage() {
        return new StaffPage(webDriver);
    }

    public SearchPatientPage toSearchPatient(BasePage fromPage) {
        fromPage.waitForSuccessfulCompletion();
        return new SearchPatientPage(webDriver);
    }

    public SearchPatientPage toSearchPatient() {
        return new SearchPatientPage(webDriver);
    }

    public OpenMRSLoginPage toOpenMRSLogin() {
        webDriver.get(applicationURLs.forOpenMRSHomePage());
        return new OpenMRSLoginPage(webDriver);
    }

    public OpenMRSHomePage openMRSHomePage() {
        return new OpenMRSHomePage(webDriver);
    }

    public MotechIdGeneratorPage toMotechIdGenerator(String path) {
        webDriver.get(applicationURLs.forPath(path));
        return new MotechIdGeneratorPage(webDriver);
    }

    public StaffPage toStaffCreatePage(HomePage fromPage) {
        fromPage.waitForSuccessfulCompletion();
        fromPage.openCreateStaffPage();
        return new StaffPage(webDriver);
    }

    public SearchStaffPage toSearchStaffPage(HomePage fromPage) {
        fromPage.waitForSuccessfulCompletion();
        fromPage.openSearchStaffPage();
        return new SearchStaffPage(webDriver);
    }

    public CWCEnrollmentPage toEnrollCWCPage(PatientEditPage patientEditPage) {
        patientEditPage.clickOnEnrollCWCLink();
        return new CWCEnrollmentPage(webDriver);
    }

    public PatientEditPage toPatientEditPage(SearchPatientPage searchPatientPage, TestPatient patient) {
        searchPatientPage.clickEditLink(patient);
        return new PatientEditPage(webDriver);
    }

    public ANCEnrollmentPage toANCEnrollmentForm(HomePage fromPage) {
        fromPage.waitForSuccessfulCompletion();
        fromPage.openANCEnrollmentPage();
        return new ANCEnrollmentPage(webDriver);
    }

    public CWCEnrollmentPage toCWCEnrollmentForm(HomePage fromPage) {
        fromPage.waitForSuccessfulCompletion();
        fromPage.openCWCEnrollmentPage();
        return new CWCEnrollmentPage(webDriver);
    }

    public MobileMidwifeEnrollmentPage toMobileMidwifeEnrollmentForm(PatientEditPage patientEditPage) {
        patientEditPage.clickEnrollMobileMidwifeLink();
        return new MobileMidwifeEnrollmentPage(webDriver);
    }

    public void quit() {
        webDriver.quit();
    }

    public FacilityPage toFacilityPage(HomePage fromPage) {
        fromPage.waitForSuccessfulCompletion();
        fromPage.openCreateFaclityPage();
        return getFacilityPage();
    }

    public FacilityPage getFacilityPage() {
        return new FacilityPage(webDriver);
    }

    public PatientPage getPatientPage() {
        return new PatientPage(webDriver);
    }

    public SearchFacilityPage toSearchFacility(HomePage fromPage) {
        fromPage.waitForSuccessfulCompletion();
        fromPage.openSearchFaclityPage();
        return new SearchFacilityPage(webDriver);
    }

    public void captureScreenShot() {
        new ScreenShotCaptor().capture(webDriver);
    }
}
