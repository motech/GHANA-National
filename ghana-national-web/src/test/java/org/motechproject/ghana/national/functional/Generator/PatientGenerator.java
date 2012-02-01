package org.motechproject.ghana.national.functional.Generator;


import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.Browser;
import org.motechproject.ghana.national.functional.pages.home.HomePage;
import org.motechproject.ghana.national.functional.pages.patient.PatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.stereotype.Component;

@Component
public class PatientGenerator {

    private DataGenerator dataGenerator;

    public PatientGenerator() {
    }

    @LoginAsAdmin
    @ApiSession
    public String createPatientWithStaff(Browser browser, HomePage homePage, String staffId) {
        dataGenerator = new DataGenerator();
        PatientPage patientPage = browser.toCreatePatient(homePage);

        TestPatient patient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId).
                patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER).estimatedDateOfBirth(false);

        patientPage.create(patient);
        patientPage.waitForSuccessfulCompletion();
        return patientPage.motechId();
    }

}
