
package org.motechproject.ghana.national.functional.patient;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MessageStartWeek;
import org.motechproject.ghana.national.domain.mobilemidwife.PhoneOwnership;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestMobileMidwifeEnrollment;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.pages.BasePage;
import org.motechproject.ghana.national.functional.pages.patient.MobileMidwifeEnrollmentPage;
import org.motechproject.ghana.national.functional.pages.patient.PatientEditPage;
import org.motechproject.ghana.national.functional.pages.patient.SearchPatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class MobileMidwifeTest extends LoggedInUserFunctionalTest {

    private DataGenerator dataGenerator;
    private String facilityId;
    private String staffId;

    @org.testng.annotations.BeforeClass
    protected void setupStaff() {
        login();
        staffId = staffGenerator.createStaff(browser, homePage);
        logout();
    }

    @BeforeMethod
    public void setUp() {
        dataGenerator = new DataGenerator();
    }

    @Test
    public void shouldEnrollAPatientToMobileMidwifeProgramAndEditEnrollmentDetails() {

        // create
        TestPatient patient = TestPatient.with("Samy Johnson" + new DataGenerator().randomString(5), staffId);
        String patientId = patientGenerator.createPatientWithStaff(patient, browser, homePage);

        TestMobileMidwifeEnrollment enrollmentDetails = TestMobileMidwifeEnrollment.with(staffId).patientId(patientId);

        MobileMidwifeEnrollmentPage enrollmentPage = toMobileMidwifeEnrollmentPage(patient, homePage);
        enrollmentPage.enroll(enrollmentDetails);

        enrollmentPage = toMobileMidwifeEnrollmentPage(patient, enrollmentPage);
        assertEquals(enrollmentDetails, enrollmentPage.details());

        // edit
        enrollmentDetails.withServiceType(ServiceType.CHILD_CARE).withMessageStartWeek(new MessageStartWeek("45", "Baby-week 45", ServiceType.CHILD_CARE));
        enrollmentPage.enroll(enrollmentDetails);

        enrollmentPage = toMobileMidwifeEnrollmentPage(patient, enrollmentPage);
        assertEquals(enrollmentDetails.patientId(patientId), enrollmentPage.details());
    }

    @Test
    public void shouldNotSubmitDayOfWeekAndTimeOfDayForSMSMedium_ForMobileMidwifeEnrollment() {

        // create
        TestPatient patient = TestPatient.with("Samy Johnson" + new DataGenerator().randomString(5), staffId);
        patientGenerator.createPatientWithStaff(patient, browser, homePage);

        MobileMidwifeEnrollmentPage enrollmentPage = toMobileMidwifeEnrollmentPage(patient, homePage);
        enrollmentPage.withConsent(true)
                .withPhoneOwnership(PhoneOwnership.PERSONAL.toString())
                .withMedium(Medium.SMS.toString());

        assertFalse(enrollmentPage.getDayOfWeek().isDisplayed());
        assertFalse(enrollmentPage.getHourOfDay().isDisplayed());

        enrollmentPage.withConsent(true)
                .withPhoneOwnership(PhoneOwnership.PERSONAL.toString())
                .withMedium(Medium.VOICE.toString());
        assertTrue(enrollmentPage.getDayOfWeek().isDisplayed());
        assertTrue(enrollmentPage.getHourOfDay().isDisplayed());
    }

    private MobileMidwifeEnrollmentPage toMobileMidwifeEnrollmentPage(TestPatient patient, BasePage basePage) {
        SearchPatientPage searchPatientPage = browser.toSearchPatient(basePage);
        searchPatientPage.searchWithName(patient.firstName());
        PatientEditPage patientEditPage = browser.toPatientEditPage(searchPatientPage, patient);
        return browser.toMobileMidwifeEnrollmentForm(patientEditPage);
    }
}
