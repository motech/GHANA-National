package org.motechproject.ghana.national.functional.patient;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestCWCEnrollment;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.pages.BasePage;
import org.motechproject.ghana.national.functional.pages.patient.CWCEnrollmentPage;
import org.motechproject.ghana.national.functional.pages.patient.PatientEditPage;
import org.motechproject.ghana.national.functional.pages.patient.PatientPage;
import org.motechproject.ghana.national.functional.pages.patient.SearchPatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.motechproject.util.DateUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class CWCTest extends LoggedInUserFunctionalTest {
    private DataGenerator dataGenerator;

    @BeforeMethod
    public void setUp() {
        dataGenerator = new DataGenerator();
    }

    @Test
    public void shouldEnrollForCWCForAPatient() {
        String staffId = staffGenerator.createStaff(browser, homePage);

        PatientPage patientPage = browser.toCreatePatient(homePage);
        String patientFirstName = "First Name" + dataGenerator.randomString(5);
        TestPatient patient = TestPatient.with(patientFirstName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.CHILD_UNDER_FIVE)
                .estimatedDateOfBirth(false)
                .dateOfBirth(DateUtil.newDate(DateUtil.today().getYear() - 1, 11, 11));

        patientPage.create(patient);

        // create
        SearchPatientPage searchPatientPage = browser.toSearchPatient(homePage);
        searchPatientPage.searchWithName(patient.firstName());

        TestCWCEnrollment testCWCEnrollment = TestCWCEnrollment.create().withStaffId(staffId);

        PatientEditPage patientEditPage = browser.toPatientEditPage(searchPatientPage, patient);
        CWCEnrollmentPage cwcEnrollmentPage = browser.toEnrollCWCPage(patientEditPage);
        cwcEnrollmentPage.save(testCWCEnrollment);

        patientEditPage = searchPatient(patientFirstName, patient, cwcEnrollmentPage);
        cwcEnrollmentPage = browser.toEnrollCWCPage(patientEditPage);

        cwcEnrollmentPage.displaying(testCWCEnrollment);

        // edit
        testCWCEnrollment.withAddCareHistory(Arrays.asList(CwcCareHistory.MEASLES)).withMeaslesDate(DateUtil.newDate(2006, 12, 2));
        cwcEnrollmentPage.save(testCWCEnrollment);

        patientEditPage = searchPatient(patientFirstName, patient, cwcEnrollmentPage);
        cwcEnrollmentPage = browser.toEnrollCWCPage(patientEditPage);

        cwcEnrollmentPage.displaying(testCWCEnrollment);
    }

    private PatientEditPage searchPatient(String patientFirstName, TestPatient testPatient, BasePage basePage) {
        SearchPatientPage searchPatientPage = browser.toSearchPatient(basePage);

        searchPatientPage.searchWithName(patientFirstName);
        searchPatientPage.displaying(testPatient);

        return browser.toPatientEditPage(searchPatientPage, testPatient);
    }

}
