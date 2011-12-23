package org.motechproject.ghana.national.functional.helper;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.CreatePatientPage;
import org.motechproject.functional.pages.HomePage;
import org.motechproject.functional.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class CreatePatientHelper {
    private LoginPage loginPage;

    private HomePage homePage;

    private CreatePatientPage createPatientPage;

    protected WebDriver driver;

    @Autowired
    public CreatePatientHelper(WebDriverProvider webDriverProvider, LoginPage loginPage, HomePage homePage, CreatePatientPage createPatientPage) {
        this.loginPage = loginPage;
        this.homePage = homePage;
        this.createPatientPage = createPatientPage;
        driver = webDriverProvider.getWebDriver();
    }

    public boolean createPatient(String fName, String mName, String lName, boolean gender, Calendar dateOfBirth, CreatePatientPage.PATIENT_TYPE patientType, String motechId) {
        loginPage.login();
        homePage.OpenCreatePatientPage();
        PageFactory.initElements(driver, createPatientPage);

        if (motechId != null) {
            createPatientPage.withRegistrationMode(CreatePatientPage.PATIENT_REGN_MODE.USE_PREPRINTED_ID)
                    .withMotechId(motechId);
        } else {
            createPatientPage.withRegistrationMode(CreatePatientPage.PATIENT_REGN_MODE.AUTO_GENERATE_ID);
        }

        createPatientPage.withPatientType(patientType)
                .withPatientFirstName(fName)
                .withPatientMiddleName(mName)
                .withPatientLastName(lName)
                .withEstimatedDOB(false)
                .withPatientGender(gender)
                .withRegion("Central Region")
                .withDistrict("Awutu Senya")
                .withSubDistrict("Kasoa")
                .withFacility("Papaase CHPS")
                .withInsured(false)
                .withDateofBirth(dateOfBirth); // select the DOB at last as there is a time delay in the jquery ui closing after selecting the date
        // this happens when the build is run locally in dev environment
        return createPatientPage.Create();
    }

}
