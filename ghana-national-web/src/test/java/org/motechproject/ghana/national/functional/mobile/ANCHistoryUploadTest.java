package org.motechproject.ghana.national.functional.mobile;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestANCEnrollment;
import org.motechproject.ghana.national.functional.data.TestMobileMidwifeEnrollment;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.pages.patient.ANCEnrollmentPage;
import org.motechproject.ghana.national.functional.pages.patient.MobileMidwifeEnrollmentPage;
import org.motechproject.ghana.national.functional.pages.patient.PatientEditPage;
import org.motechproject.ghana.national.functional.pages.patient.SearchPatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class ANCHistoryUploadTest extends LoggedInUserFunctionalTest {

    @Test
    public void shouldCreateANCForAPatientWithMobileDeviceAndSearchForItInWeb() {
        DataGenerator dataGenerator = new DataGenerator();

        String staffId = staffGenerator.createStaff(browser, homePage);

        TestPatient testPatient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);

        String patientId = patientGenerator.createPatientWithStaff(testPatient, browser, homePage);

        TestANCEnrollment ancEnrollment = TestANCEnrollment.create().withMotechPatientId(patientId).withStaffId(staffId);
        TestMobileMidwifeEnrollment mmEnrollmentDetails = TestMobileMidwifeEnrollment.with(staffId, testPatient.facilityId()).patientId(patientId);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerANCForm(), ancEnrollment.withMobileMidwifeEnrollmentThroughMobile(mmEnrollmentDetails));

        assertEquals(1, response.getSuccessCount());

        PatientEditPage patientEditPage = toPatientEditPage(testPatient);
        ANCEnrollmentPage ancEnrollmentPage = browser.toEnrollANCPage(patientEditPage);
        ancEnrollmentPage.displaying(ancEnrollment);

        patientEditPage = toPatientEditPage(testPatient);
        MobileMidwifeEnrollmentPage mobileMidwifeEnrollmentPage = browser.toMobileMidwifeEnrollmentForm(patientEditPage);
        assertThat(mobileMidwifeEnrollmentPage.details(), is(equalTo(mmEnrollmentDetails)));
    }

    @Test
    public void shouldUnRegisterExistingMobileMidWifeWhileANCRegistration() {
        DataGenerator dataGenerator = new DataGenerator();

        String staffId = staffGenerator.createStaff(browser, homePage);

        TestPatient testPatient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);

        String patientId = patientGenerator.createPatientWithStaff(testPatient, browser, homePage);

        PatientEditPage patientEditPage = toPatientEditPage(testPatient);
        MobileMidwifeEnrollmentPage mobileMidwifeEnrollmentPage = browser.toMobileMidwifeEnrollmentForm(patientEditPage);
        mobileMidwifeEnrollmentPage.enroll(TestMobileMidwifeEnrollment.with(staffId));

        TestANCEnrollment ancEnrollment = TestANCEnrollment.create().withMotechPatientId(patientId).withStaffId(staffId);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerANCForm(), ancEnrollment.withoutMobileMidwifeEnrollmentThroughMobile());
        assertEquals(1, response.getSuccessCount());

        PatientEditPage patientPageAfterEdit = toPatientEditPage(testPatient);
        mobileMidwifeEnrollmentPage = browser.toMobileMidwifeEnrollmentForm(patientPageAfterEdit);


        assertThat(mobileMidwifeEnrollmentPage.status(),is("INACTIVE"));
    }


    private PatientEditPage toPatientEditPage(TestPatient testPatient) {
        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithName(testPatient.firstName());
        searchPatientPage.displaying(testPatient);
        return browser.toPatientEditPage(searchPatientPage, testPatient);
    }
}
