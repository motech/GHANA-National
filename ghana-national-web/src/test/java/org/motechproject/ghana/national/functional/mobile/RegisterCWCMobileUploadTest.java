package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestCWCEnrollment;
import org.motechproject.ghana.national.functional.data.TestMobileMidwifeEnrollment;
import org.motechproject.ghana.national.functional.data.TestPatient;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class RegisterCWCMobileUploadTest extends LoggedInUserFunctionalTest {

    @Test
    public void shouldCheckForAllMandatoryDetails() throws Exception {
        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerCWCForm(), MapUtils.EMPTY_MAP);
        final List<XformHttpClient.Error> errors = xformResponse.getErrors();

        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("staffId"), hasItem("is mandatory"));
        assertThat(errorsMap.get("facilityId"), hasItem("is mandatory"));
        assertThat(errorsMap.get("motechId"), hasItem("is mandatory"));
        assertThat(errorsMap.get("registrationToday"), hasItem("is mandatory"));
        assertThat(errorsMap.get("registrationDate"), hasItem("is mandatory"));
        assertThat(errorsMap.get("serialNumber"), hasItem("is mandatory"));
    }

    @Test
    public void shouldValidateIfRegistrationIsDoneByAProperUserForAnExistingPatientAndFacility() throws Exception {
        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.registerCWCForm(), new HashMap<String, String>() {{
            put("motechId", "-1");
            put("facilityId", "-1");
            put("staffId", "-1");
            put("registrationToday", RegistrationToday.TODAY.toString());
            put("registrationDate", "2012-01-03");
            put("serialNumber", "1234243243");
        }});
        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("staffId"), hasItem("not found"));
        assertThat(errorsMap.get("facilityId"), hasItem("not found"));
        assertThat(errorsMap.get("motechId"), hasItem("not found"));
    }

    @Test
    public void shouldRegisterAPatientForCWCAndMobileMidwifeProgramUsingMobileDeviceAndSearchForItInWeb() {
        DataGenerator dataGenerator = new DataGenerator();
        String staffId = staffGenerator.createStaff(browser, homePage);

        TestPatient testPatient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId)
                .patientType(TestPatient.PATIENT_TYPE.CHILD_UNDER_FIVE)
                .estimatedDateOfBirth(false)
                .dateOfBirth(DateUtil.newDate(DateUtil.today().getYear() - 1, 11, 11));

        String patientId = patientGenerator.createPatientWithStaff(testPatient, browser, homePage);

        TestCWCEnrollment cwcEnrollment = TestCWCEnrollment.create().withMotechPatientId(patientId).withStaffId(staffId);
        TestMobileMidwifeEnrollment mmEnrollmentDetails = TestMobileMidwifeEnrollment.with(staffId, testPatient.facilityId()).patientId(patientId);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerCWCForm(), cwcEnrollment.withMobileMidwifeEnrollmentThroughMobile(mmEnrollmentDetails));
        assertEquals(1, response.getSuccessCount());

        PatientEditPage patientEditPage = toPatientEditPage(testPatient);
        CWCEnrollmentPage cwcEnrollmentPage = browser.toEnrollCWCPage(patientEditPage);
        cwcEnrollmentPage.displaying(cwcEnrollment);

        patientEditPage = toPatientEditPage(testPatient);
        MobileMidwifeEnrollmentPage mobileMidwifeEnrollmentPage = browser.toMobileMidwifeEnrollmentForm(patientEditPage);
        assertThat(mobileMidwifeEnrollmentPage.details(), is(equalTo(mmEnrollmentDetails)));
    }

    @Test
    public void shouldUnRegisterExistingMobileMidWifeWhileCWCRegistration() {
        DataGenerator dataGenerator = new DataGenerator();

        String staffId = staffGenerator.createStaff(browser, homePage);

        TestPatient testPatient = TestPatient.with("First Name" + dataGenerator.randomString(5), staffId)
                .patientType(TestPatient.PATIENT_TYPE.CHILD_UNDER_FIVE)
                .estimatedDateOfBirth(false)
                .dateOfBirth(DateUtil.newDate(DateUtil.today().getYear() - 1, 11, 11));

        String patientId = patientGenerator.createPatientWithStaff(testPatient, browser, homePage);

        PatientEditPage patientEditPage = toPatientEditPage(testPatient);
        MobileMidwifeEnrollmentPage mobileMidwifeEnrollmentPage = browser.toMobileMidwifeEnrollmentForm(patientEditPage);
        mobileMidwifeEnrollmentPage.enroll(TestMobileMidwifeEnrollment.with(staffId));

        TestCWCEnrollment cwcEnrollment = TestCWCEnrollment.create().withMotechPatientId(patientId).withStaffId(staffId);

        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerCWCForm(), cwcEnrollment.withoutMobileMidwifeEnrollmentThroughMobile());
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
