package org.motechproject.ghana.national.functional.Generator;


import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.framework.Browser;
import org.motechproject.functional.pages.home.HomePage;
import org.motechproject.functional.pages.patient.PatientPage;
import org.motechproject.functional.util.DataGenerator;
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

        TestPatient patient = TestPatient.with("First Name" + dataGenerator.randomString(5)).staffId(staffId).
                registrationMode(TestPatient.PATIENT_REGN_MODE.AUTO_GENERATE_ID).
                patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER).estimatedDateOfBirth(false);

        patientPage.create(patient);
        patientPage.waitForSuccessfulCompletion();
        return patientPage.motechId();
    }

}
