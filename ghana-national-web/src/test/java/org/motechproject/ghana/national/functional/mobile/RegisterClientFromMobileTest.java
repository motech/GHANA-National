package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.junit.runner.RunWith;
import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.data.TestStaff;
import org.motechproject.functional.framework.XformHttpClient;
import org.motechproject.functional.mobileforms.MobileForm;
import org.motechproject.functional.pages.patient.SearchPatientPage;
import org.motechproject.functional.pages.staff.StaffPage;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.service.IdentifierGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.motechproject.functional.framework.XformHttpClient.XFormParser;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class RegisterClientFromMobileTest extends LoggedInUserFunctionalTest {

    @Autowired
    private IdentifierGenerationService identifierGenerationService;

    @Test
    public void shouldCheckForMandatoryFields() throws Exception {

        final XformHttpClient.XformResponse xformResponse = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("register-client-template.xml", MapUtils.EMPTY_MAP));

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
    public void shouldNotGiveErrorForFirstNameIfGiven() throws Exception {

        final XformHttpClient.XformResponse xformResponse = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("register-client-template.xml", new HashMap<String, String>() {{
            put("firstName", "Joe");
        }}));

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();
        assertNull(errorsMap.get("firstName"));
    }

    @Test
    public void shouldCreateAPatientWithMobileDeviceAndSearchForHerByName(){
        DataGenerator dataGenerator = new DataGenerator();
        String firstName = "First Name" + dataGenerator.randomString(5);

        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        staffPage.create(TestStaff.with(firstName));

        TestPatient patient = TestPatient.with("First Name" + dataGenerator.randomString(5)).
                registrationMode(TestPatient.PATIENT_REGN_MODE.AUTO_GENERATE_ID).
                patientType(TestPatient.PATIENT_TYPE.OTHER).estimatedDateOfBirth(false).
                staffId(staffPage.staffId());

        mobile.upload(MobileForm.registerClientForm(), patient.forMobile());

        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithName(patient.firstName());
        searchPatientPage.displaying(patient);

    }

}
