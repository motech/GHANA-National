package org.motechproject.ghana.national.functional.patient;

import org.junit.runner.RunWith;
import org.motechproject.functional.data.TestANCEnrollment;
import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.data.TestStaff;
import org.motechproject.functional.framework.XformHttpClient;
import org.motechproject.functional.mobileforms.MobileForm;
import org.motechproject.functional.pages.BasePage;
import org.motechproject.functional.pages.patient.ANCEnrollmentPage;
import org.motechproject.functional.pages.patient.PatientEditPage;
import org.motechproject.functional.pages.patient.PatientPage;
import org.motechproject.functional.pages.patient.SearchPatientPage;
import org.motechproject.functional.pages.staff.StaffPage;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class ANCTest extends LoggedInUserFunctionalTest {
    private PatientPage patientPage;
    private DataGenerator dataGenerator;

    @BeforeMethod
    public void setUp() {
        dataGenerator = new DataGenerator();
    }

    @Test
    public void shouldEnrollForANCForAPatientAndUpdate() {
        // create

        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        staffPage.create(TestStaff.with("staff first name" + dataGenerator.randomString(5)));
        String staffId = staffPage.staffId();

        String patientFirstName = "patient first name" + dataGenerator.randomString(5);
        TestPatient testPatient = TestPatient.with(patientFirstName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);
        
        patientPage = browser.toCreatePatient(staffPage);
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

    @Test
    public void shouldCreateANCForAPatientWithMobileDeviceAndSearchForItInWeb() {
        DataGenerator dataGenerator = new DataGenerator();
        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        staffPage.create(TestStaff.with("First Name" + dataGenerator.randomString(5)));

        String staffId = staffPage.staffId();
        String patientFirstName = "First Name" + dataGenerator.randomString(5);
        TestPatient testPatient = TestPatient.with(patientFirstName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);

        patientPage = browser.toCreatePatient(staffPage);
        patientPage.create(testPatient);

        TestANCEnrollment ancEnrollment = TestANCEnrollment.create().withMotechPatientId(patientPage.motechId()).withStaffId(staffId);
        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerANCForm(), ancEnrollment.forMobile());

        assertEquals(1, response.getSuccessCount());
        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithName(testPatient.firstName());
        searchPatientPage.displaying(testPatient);

        PatientEditPage patientEditPage = browser.toPatientEditPage(searchPatientPage, testPatient);
        ANCEnrollmentPage ancEnrollmentPage = browser.toEnrollANCPage(patientEditPage);
        ancEnrollmentPage.displaying(ancEnrollment);
    }

    private PatientEditPage searchPatient(String patientFirstName, TestPatient testPatient, BasePage basePage) {
        SearchPatientPage searchPatientPage = browser.toSearchPatient(basePage);

        searchPatientPage.searchWithName(patientFirstName);
        searchPatientPage.displaying(testPatient);

        return browser.toPatientEditPage(searchPatientPage, testPatient);
    }
}
