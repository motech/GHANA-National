package org.motechproject.ghana.national.functional.mobile;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.framework.XformHttpClient;
import org.motechproject.functional.mobileforms.MobileForm;
import org.motechproject.functional.pages.patient.PatientPage;
import org.motechproject.functional.pages.patient.SearchPatientPage;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.ghana.national.functional.Generator.FacilityGenerator;
import org.motechproject.ghana.national.functional.Generator.PatientGenerator;
import org.motechproject.ghana.national.functional.Generator.StaffGenerator;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class EditClientFromMobileTest extends LoggedInUserFunctionalTest {
    @Autowired
    StaffGenerator staffGenerator;
    @Autowired
    FacilityGenerator facilityGenerator;
    @Autowired
    PatientGenerator patientGenerator;
    DataGenerator dataGenerator;

    @Before
    public void setUp() {
        dataGenerator = new DataGenerator();
    }

    @Test
    public void shouldCheckForMandatoryFields() throws Exception {

        final XformHttpClient.XformResponse xformResponse = setupEditClientFormAndUpload(new HashMap<String, String>() {{
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
        final XformHttpClient.XformResponse xformResponse = setupEditClientFormAndUpload(new HashMap<String, String>() {{
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

        final XformHttpClient.XformResponse xformResponse = setupEditClientFormAndUpload(new HashMap<String, String>() {{
            put("firstName", "Joe");
        }});

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();
        assertNull(errorsMap.get("firstName"));
    }

    @org.testng.annotations.Test
    public void shouldUpdatePatientIfNoErrorsAreFound() throws Exception {
        DataGenerator dataGenerator = new DataGenerator();

        String facilityMotechId = facilityGenerator.createFacility(browser, homePage);
        String staffId = staffGenerator.createStaff(browser, homePage);
        final String patientId = patientGenerator.createPatient(browser, homePage);

        TestPatient patient = TestPatient.with("Updated First Name" + dataGenerator.randomString(5)).
                registrationMode(TestPatient.PATIENT_REGN_MODE.AUTO_GENERATE_ID).
                patientType(TestPatient.PATIENT_TYPE.OTHER).estimatedDateOfBirth(false)
                .middleName("Updated Middle Name").lastName("Updated Last Name").
                        staffId(staffId).motechId(patientId).facilityIdWherePatientIsEdited(facilityMotechId);

        mobile.upload(MobileForm.editClientForm(), patient.editFromMobile());

        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithMotechId(patientId);
        searchPatientPage.displaying(patient);

        searchPatientPage.clickEditLink(patient);
        PatientPage patientPage = browser.getPatientPage();
        assertThat(patientPage.motechId(), is(equalTo(patient.motechId())));
        assertThat(patientPage.firstName(), is(equalTo(patient.firstName())));
        assertThat(patientPage.middleName(), is(equalTo(patient.middleName())));
        assertThat(patientPage.lastName(), is(equalTo(patient.lastName())));

    }


    private XformHttpClient.XformResponse setupEditClientFormAndUpload(Map<String, String> data) throws Exception {
        return XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XformHttpClient.XFormParser.parse("edit-client-template.xml", data));
    }
}
