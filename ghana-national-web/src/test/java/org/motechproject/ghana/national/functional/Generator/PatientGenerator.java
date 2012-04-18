package org.motechproject.ghana.national.functional.Generator;


import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.Browser;
import org.motechproject.ghana.national.functional.pages.home.HomePage;
import org.motechproject.ghana.national.functional.pages.patient.PatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;

public class PatientGenerator {

    @LoginAsAdmin
    @ApiSession
    public String createPatient(Browser browser, HomePage homePage, String staffId) {
        DataGenerator dataGenerator = new DataGenerator();
        TestPatient patient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId).
                patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER).estimatedDateOfBirth(false);

        return createPatient(patient, browser, homePage);
    }

    @LoginAsAdmin
    @ApiSession
    public String createPatient(TestPatient patient, Browser browser, HomePage homePage) {
        PatientPage patientPage = browser.toCreatePatient(homePage);
        patientPage.create(patient);
        patientPage.waitForSuccessfulCompletion();
        return patientPage.motechId();
    }



}
