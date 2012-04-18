package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.*;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.pages.patient.*;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.motechproject.util.DateUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class RegisterClientFromMobileTest extends LoggedInUserFunctionalTest {

    DataGenerator dataGenerator;

    public RegisterClientFromMobileTest() {
        this.dataGenerator = new DataGenerator();
    }

    @Test
    public void shouldCheckForMandatoryFields() throws Exception {

        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerClientForm(), MapUtils.EMPTY_MAP);

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("registrationMode"), hasItem("is mandatory"));
        assertThat(errorsMap.get("registrantType"), hasItem("is mandatory"));
        assertThat(errorsMap.get("firstName"), hasItem("is mandatory"));
        assertThat(errorsMap.get("lastName"), hasItem("is mandatory"));
        assertThat(errorsMap.get("dateOfBirth"), hasItem("is mandatory"));
        assertThat(errorsMap.get("date"), hasItem("is mandatory"));
        assertThat(errorsMap.get("estimatedBirthDate"), hasItem("is mandatory"));
        assertThat(errorsMap.get("insured"), hasItem("is mandatory"));
        assertThat(errorsMap.get("date"), hasItem("is mandatory"));
        assertThat(errorsMap.get("address"), hasItem("is mandatory"));
        assertThat(errorsMap.get("facilityId"), hasItem("not found"));
        assertThat(errorsMap.get("staffId"), hasItem("not found"));
    }

    @Test
    public void shouldGiveErrorForFirstNameIfNotGiven() throws Exception {

        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerClientForm(), new HashMap<String, String>() {{
            put("firstName", "Joe");
        }});

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();
        assertNull(errorsMap.get("firstName"));
    }

    @Test
    public void shouldCreateAPatientWithMobileDeviceAndSearchForHerByName() {
        String staffId = staffGenerator.createStaff(browser, homePage);

        TestPatient patient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId).
                patientType(TestPatient.PATIENT_TYPE.OTHER).estimatedDateOfBirth(false);

        mobile.upload(MobileForm.registerClientForm(), patient.forMobile());

        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithName(patient.firstName());
        searchPatientPage.displaying(patient);

    }

    @Test
    public void shouldCreatePatientWithANCRegistrationInfoAndSearchForHer() {
        String staffId = staffGenerator.createStaff(browser, homePage);

        TestPatient patient = TestPatient.with("Second ANC Name" + dataGenerator.randomString(5), staffId).
                patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER).estimatedDateOfBirth(false);

        TestMobileMidwifeEnrollment mmEnrollmentDetails = TestMobileMidwifeEnrollment.with(staffId, patient.facilityId());

        TestANCEnrollment ancEnrollmentDetails = TestANCEnrollment.create();

        TestClientRegistration<TestANCEnrollment> testClientRegistration = new TestClientRegistration<TestANCEnrollment>(patient, ancEnrollmentDetails, mmEnrollmentDetails);

        mobile.upload(MobileForm.registerClientForm(), testClientRegistration.withProgramEnrollmentThroughMobile());

        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithName(patient.firstName());
        searchPatientPage.displaying(patient);

        PatientEditPage editPage = browser.toPatientEditPage(searchPatientPage, patient);
        String patientId = editPage.motechId();

        ANCEnrollmentPage ancEnrollmentPage = browser.toEnrollANCPage(editPage);

            TestANCEnrollment expectedANCEnrollment = testClientRegistration.getEnrollment()
                .withStaffId(testClientRegistration.getPatient().staffId())
                .withFacilityId(testClientRegistration.getPatient().facilityId())
                .withMotechPatientId(patientId)
                .withRegistrationDate(testClientRegistration.getPatient().getRegistrationDate());

        ancEnrollmentPage.displaying(expectedANCEnrollment);

        browser.toSearchPatient();
        searchPatientPage.searchWithName(patient.firstName());
        editPage = browser.toPatientEditPage(searchPatientPage, patient);
        MobileMidwifeEnrollmentPage enrollmentPage = browser.toMobileMidwifeEnrollmentForm(editPage);

        assertEquals(mmEnrollmentDetails.patientId(patientId), enrollmentPage.details());
    }

    @Test
    public void shouldCreatePatientWithCWCRegistrationInfoAndSearchForChild() {
        String staffId = staffGenerator.createStaff(browser, homePage);

        String motherMotechId = patientGenerator.createPatient(browser, homePage, staffId);

        TestPatient patient = TestPatient.with("Second CWC Name" + dataGenerator.randomString(5), staffId).
                patientType(TestPatient.PATIENT_TYPE.CHILD_UNDER_FIVE).estimatedDateOfBirth(false).
                motherMotechId(motherMotechId).registrationDate(DateUtil.newDate(2000, 12, 12)).dateOfBirth(DateUtil.newDate(2010,11,11));

        TestMobileMidwifeEnrollment mmEnrollmentDetails = TestMobileMidwifeEnrollment.with(staffId, patient.facilityId());

        TestCWCEnrollment cwcEnrollmentDetails = TestCWCEnrollment.create();

        TestClientRegistration<TestCWCEnrollment> testClientRegistration = new TestClientRegistration<TestCWCEnrollment>(patient, cwcEnrollmentDetails, mmEnrollmentDetails);

        mobile.upload(MobileForm.registerClientForm(), testClientRegistration.withProgramEnrollmentThroughMobile());

        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithName(patient.firstName());
        searchPatientPage.displaying(patient);

        TestCWCEnrollment expectedCWCEnrollment = testClientRegistration.getEnrollment()
                .withStaffId(testClientRegistration.getPatient().staffId())
                .withFacilityId(testClientRegistration.getPatient().facilityId())
                .withMotechPatientId(testClientRegistration.getPatient().motechId())
                .withRegistrationDate(testClientRegistration.getPatient().getRegistrationDate());

        PatientEditPage editPage = browser.toPatientEditPage(searchPatientPage, patient);
        String patientId = editPage.motechId();

        CWCEnrollmentPage cwcEnrollmentPage = browser.toEnrollCWCPage(editPage);
        cwcEnrollmentPage.displaying(expectedCWCEnrollment);

        browser.toSearchPatient();
        searchPatientPage.searchWithName(patient.firstName());
        editPage = browser.toPatientEditPage(searchPatientPage, patient);
        MobileMidwifeEnrollmentPage enrollmentPage = browser.toMobileMidwifeEnrollmentForm(editPage);

        assertEquals(mmEnrollmentDetails.patientId(patientId), enrollmentPage.details());
    }

}
