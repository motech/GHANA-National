package org.motechproject.ghana.national.functional.patient;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestANCEnrollment;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.pages.BasePage;
import org.motechproject.ghana.national.functional.pages.patient.ANCEnrollmentPage;
import org.motechproject.ghana.national.functional.pages.patient.PatientEditPage;
import org.motechproject.ghana.national.functional.pages.patient.PatientPage;
import org.motechproject.ghana.national.functional.pages.patient.SearchPatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class ANCTest extends LoggedInUserFunctionalTest {
    private PatientPage patientPage;
    private DataGenerator dataGenerator;

    @BeforeMethod
    public void setUp() {
        dataGenerator = new DataGenerator();
    }

    @Test(enabled = false)
    public void shouldEnrollForANCForAPatientAndUpdate() {
        // create
        String staffId = staffGenerator.createStaff(browser, homePage);

        String patientFirstName = "patient first name" + dataGenerator.randomString(5);
        TestPatient testPatient = TestPatient.with(patientFirstName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);
        
        patientPage = browser.toCreatePatient(homePage);
        patientPage.create(testPatient);

        TestANCEnrollment ancEnrollment = TestANCEnrollment.create().withStaffId(staffId).withRegistrationToday(RegistrationToday.IN_PAST);
        PatientEditPage patientEditPage = searchPatient(patientFirstName, testPatient, patientPage);
        ANCEnrollmentPage ancEnrollmentPage = browser.toEnrollANCPage(patientEditPage);
        ancEnrollmentPage.save(ancEnrollment);

        patientEditPage = searchPatient(patientFirstName, testPatient, ancEnrollmentPage);
        ancEnrollmentPage = browser.toEnrollANCPage(patientEditPage);

        ancEnrollmentPage.displaying(ancEnrollment);

        // edit
        ancEnrollment.withSubDistrict("Senya").withFacility("Senya HC").withGravida("4").withHeight("160.9");
        ancEnrollmentPage.save(ancEnrollment);

        patientEditPage = searchPatient(patientFirstName, testPatient, ancEnrollmentPage);
        ancEnrollmentPage = browser.toEnrollANCPage(patientEditPage);

        ancEnrollmentPage.displaying(ancEnrollment);
    }

    private PatientEditPage searchPatient(String patientFirstName, TestPatient testPatient, BasePage basePage) {
        SearchPatientPage searchPatientPage = browser.toSearchPatient(basePage);

        searchPatientPage.searchWithName(patientFirstName);
        searchPatientPage.displaying(testPatient);

        return browser.toPatientEditPage(searchPatientPage, testPatient);
    }
}
