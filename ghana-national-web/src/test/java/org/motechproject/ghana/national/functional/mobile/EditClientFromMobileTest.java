package org.motechproject.ghana.national.functional.mobile;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.framework.XformHttpClient;
import org.motechproject.functional.mobileforms.MobileForm;
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
import static org.hamcrest.Matchers.hasItem;
import static org.motechproject.functional.framework.XformHttpClient.XFormParser;
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

    // TODO: TEST NOT COMPLETE
    @Ignore
    @Test
    public void shouldUpdatePatientIfNoErrorsAreFound() throws Exception {

        DataGenerator dataGenerator = new DataGenerator();

        String facilityMotechId = facilityGenerator.createFacilityAndReturnFacilityId(browser, homePage);
//        String facilityId = facilityGenerator.getFacilityId(facilityMotechId);
        String staffId = staffGenerator.createStaffAndReturnStaffId(browser, homePage);
        final String patientId = patientGenerator.createPatientAndReturnPatientId(browser, homePage);

        TestPatient patient = TestPatient.with("First Name" + dataGenerator.randomString(5)).
                registrationMode(TestPatient.PATIENT_REGN_MODE.AUTO_GENERATE_ID).
                patientType(TestPatient.PATIENT_TYPE.OTHER).estimatedDateOfBirth(false).
                staffId(staffId);

        mobile.upload(MobileForm.editClientForm(), patient.forMobileEdit());

        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithName(patient.firstName());
        searchPatientPage.displaying(patient);

    }

    private XformHttpClient.XformResponse setupEditClientFormAndUpload(Map<String, String> data) throws Exception {
        return XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("edit-client-template.xml", data));
    }
}
