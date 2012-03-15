package org.motechproject.ghana.national.functional.mobile;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.pages.patient.PatientEditPage;
import org.motechproject.ghana.national.functional.pages.patient.SearchPatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class EditClientFromMobileTest extends LoggedInUserFunctionalTest {

    @Test
    public void shouldCheckForMandatoryFields() throws Exception {

        XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.editClientForm(), new HashMap<String, String>() {{
        }});

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("staffId"), hasItem("is mandatory"));
        assertThat(errorsMap.get("updatePatientFacilityId"), hasItem("is mandatory"));
        assertThat(errorsMap.get("motechId"), hasItem("is mandatory"));
        assertThat(errorsMap.get("date"), hasItem("is mandatory"));
    }

    @Test
    public void shouldGiveErrorIfIdsAreNotFound() throws Exception {
        XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.editClientForm(), new HashMap<String, String>() {{
            put("facilityId", "testFacilityId");
            put("motechId", "testMotechId");
            put("staffId", "testStaffId");
            put("motherMotechId", "testMotherMotechId");
        }});

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);

        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();
        assertThat(errorsMap.get("facilityId"), hasItem("not found"));
        assertThat(errorsMap.get("staffId"), hasItem("not found"));
        assertThat(errorsMap.get("motechId"), hasItem("not found"));

    }

    @Test
    public void shouldNotGiveErrorForFirstNameIfGiven() throws Exception {

        XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.editClientForm(), new HashMap<String, String>() {{
            put("firstName", "Joe");
        }});

        List<XformHttpClient.Error> errors = xformResponse.getErrors();

        assertEquals(errors.size(), 1);
        assertNull(errors.iterator().next().getErrors().get("firstName"));
    }

    @Test
    public void shouldUpdatePatientIfNoErrorsAreFound() throws Exception {
        DataGenerator dataGenerator = new DataGenerator();

        String facilityMotechId = facilityGenerator.createFacility(browser, homePage);
        String staffId = staffGenerator.createStaff(browser, homePage);
        String patientId = patientGenerator.createPatientWithStaff(browser, homePage, staffId);

        TestPatient patient = TestPatient.with("Updated First Name" + dataGenerator.randomString(5), staffId)
                .patientType(TestPatient.PATIENT_TYPE.OTHER)
                .estimatedDateOfBirth(false)
                .middleName("Updated Middle Name")
                .lastName("Updated Last Name")
                .motechId(patientId)
                .facilityIdWherePatientIsEdited(facilityMotechId);

        mobile.upload(MobileForm.editClientForm(), patient.editFromMobile());


        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithMotechId(patientId);
        searchPatientPage.displaying(patient);

        PatientEditPage patientPage = browser.toPatientEditPage(searchPatientPage, patient);
        patientPage.displaying(patient);
    }
}
