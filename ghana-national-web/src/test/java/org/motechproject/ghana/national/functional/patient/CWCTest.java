package org.motechproject.ghana.national.functional.patient;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestCWCEnrollment;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
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
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

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

        String patientFirstName =  "First Name" + dataGenerator.randomString(5);
        PatientPage patientPage = browser.toCreatePatient(homePage);
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

    @Test
    public void shouldCreateCWCVisit() {
        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String facilityId = facilityGenerator.createFacility(browser, homePage);
        String patientFirstName =  "First Name" + dataGenerator.randomString(5);
        final PatientPage patientPage = browser.toCreatePatient(homePage);
        final TestPatient patient = TestPatient.with(patientFirstName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.CHILD_UNDER_FIVE)
                .estimatedDateOfBirth(false)
                .dateOfBirth(DateUtil.now().minusYears(2).toLocalDate());
        patientPage.create(patient);
        SearchPatientPage searchPatientPage = browser.toSearchPatient(homePage);
        searchPatientPage.searchWithName(patient.firstName());
        PatientEditPage patientEditPage = browser.toPatientEditPage(searchPatientPage, patient);
        TestCWCEnrollment testCWCEnrollment = TestCWCEnrollment.create().withStaffId(staffId);
        final CWCEnrollmentPage cwcEnrollmentPage = browser.toEnrollCWCPage(patientEditPage);
        cwcEnrollmentPage.save(testCWCEnrollment);
        
        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerCWCVisitForm(), new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", facilityId);
            put("date", "2012-12-11");
            put("motechId", cwcEnrollmentPage.getPatientMotechId());
            put("serialNumber", "1234567");
            put("immunizations", "BCG OPV YF");
            put("opvdose", "OPV 1");
            put("pentadose", "Penta 1");
            put("iptidose", "IPT 1");
            put("weight", "23");
            put("muac", "12");
            put("height", "13");
            put("maleInvolved", "Y");
            put("cwcLocation", "HOME");
            put("house", "32");
            put("community", "Home");
            put("comments", "Unknwon");
        }});

        assertEquals(0, response.getErrors().size());
    }

    private PatientEditPage searchPatient(String patientFirstName, TestPatient testPatient, BasePage basePage) {
        SearchPatientPage searchPatientPage = browser.toSearchPatient(basePage);

        searchPatientPage.searchWithName(patientFirstName);
        searchPatientPage.displaying(testPatient);

        return browser.toPatientEditPage(searchPatientPage, testPatient);
    }

}
