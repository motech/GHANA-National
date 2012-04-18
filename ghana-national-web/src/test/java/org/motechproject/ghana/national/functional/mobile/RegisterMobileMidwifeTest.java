package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestMobileMidwifeEnrollment;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.pages.patient.MobileMidwifeEnrollmentPage;
import org.motechproject.ghana.national.functional.pages.patient.PatientEditPage;
import org.motechproject.ghana.national.functional.pages.patient.SearchPatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class RegisterMobileMidwifeTest extends LoggedInUserFunctionalTest {
    DataGenerator dataGenerator;

    @BeforeMethod
    public void setUp() {
        dataGenerator = new DataGenerator();
    }

    @Test
    public void shouldValidateIfPatientIsAlreadyRegistered() throws Exception {
        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerMobileMidwifeForm(), new HashMap<String, String>() {{
            put("patientId", "someinvalidpatientid");
        }});

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("patientId"), hasItem("is invalid"));
        assertThat(errorsMap.get("patientId"), hasItem("not found"));

    }

    @Test
    public void shouldValidateIfStaffIsAlreadyRegistered() throws Exception {
        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerMobileMidwifeForm(), new HashMap<String, String>() {{
            put("staffId", "someinvalidstaffid");
        }});

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("staffId"), hasItem("is invalid"));
        assertThat(errorsMap.get("staffId"), hasItem("not found"));

    }

    @Test
    public void shouldValidateIfFacilityIsAlreadyRegistered() throws Exception {
        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerMobileMidwifeForm(), new HashMap<String, String>() {{
            put("facilityId", "someinvalidFacilityid");
        }});

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("facilityId"), hasItem("is invalid"));
        assertThat(errorsMap.get("facilityId"), hasItem("not found"));

    }

    @Test
    public void shouldValidateForMandatoryFields() throws Exception {
        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerMobileMidwifeForm(), MapUtils.EMPTY_MAP);

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("staffId"), hasItem("is mandatory"));
        assertThat(errorsMap.get("facilityId"), hasItem("is mandatory"));
        assertThat(errorsMap.get("patientId"), hasItem("is mandatory"));
    }

    @Test
    public void shouldRegisterForMobileMidWifeProgramIfValidationsPass() throws Exception {
        final String staffId = staffGenerator.createStaff(browser, homePage);

        TestPatient patient = TestPatient.with("Second ANC Name" + dataGenerator.randomString(5), staffId).
                patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER).estimatedDateOfBirth(false);

        final String patientId = patientGenerator.createPatient(patient, browser, homePage);

        TestMobileMidwifeEnrollment mmEnrollmentDetails = TestMobileMidwifeEnrollment.with(staffId).patientId(patientId);

        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerMobileMidwifeForm(), mmEnrollmentDetails.forMobile("REGISTER"));

        assertThat(xformResponse.getSuccessCount(), is(equalTo(1)));

        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithMotechId(patientId);
        searchPatientPage.displaying(patient);

        PatientEditPage editPage = browser.toPatientEditPage(searchPatientPage, patient);

        MobileMidwifeEnrollmentPage enrollmentPage = browser.toMobileMidwifeEnrollmentForm(editPage);
        assertEquals(mmEnrollmentDetails, enrollmentPage.details());
    }

    @Test
    public void shouldUnregisterFromMobileMidwifeProgram() {
        final String staffId = staffGenerator.createStaff(browser, homePage);

        TestPatient patient = TestPatient.with("Second ANC Name" + dataGenerator.randomString(5), staffId).
                patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER).estimatedDateOfBirth(false);

        final String patientId = patientGenerator.createPatient(patient, browser, homePage);

        TestMobileMidwifeEnrollment mmEnrollmentDetails = TestMobileMidwifeEnrollment.with(staffId).patientId(patientId);

        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerMobileMidwifeForm(), mmEnrollmentDetails.forMobile("REGISTER"));
        assertThat("Registration to Mobile Midwife failed", xformResponse.getSuccessCount(), is(equalTo(1)));

        TestMobileMidwifeEnrollment mmUnregisterDetails = TestMobileMidwifeEnrollment.with(staffId).patientId(patientId).status("INACTIVE");
        final XformHttpClient.XformResponse unregisterMMResponse = mobile.upload(MobileForm.registerMobileMidwifeForm(), mmUnregisterDetails.forMobile("UN_REGISTER"));
        assertThat("UnRegistration to Mobile Midwife failed", unregisterMMResponse.getSuccessCount(), is(equalTo(1)));

        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithMotechId(patientId);
        searchPatientPage.displaying(patient);

        PatientEditPage editPage = browser.toPatientEditPage(searchPatientPage, patient);

        MobileMidwifeEnrollmentPage enrollmentPage = browser.toMobileMidwifeEnrollmentForm(editPage);
        assertEquals(mmUnregisterDetails, enrollmentPage.details());
    }
}
