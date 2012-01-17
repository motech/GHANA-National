package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.apache.xpath.operations.Bool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.FunctionalTest;
import org.motechproject.ghana.national.functional.Generator.FacilityGenerator;
import org.motechproject.ghana.national.functional.Generator.PatientGenerator;
import org.motechproject.ghana.national.functional.Generator.StaffGenerator;
import org.motechproject.ghana.national.repository.AllStaffs;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.web.FacilityController;
import org.motechproject.ghana.national.web.PatientController;
import org.motechproject.ghana.national.web.StaffController;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.motechproject.functional.framework.XformHttpClient.XFormParser;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class RegisterMobileMidwifeTest extends AbstractJUnit4SpringContextTests{



    @Autowired
    StaffGenerator staffGenerator;
    @Autowired
    PatientGenerator patientGenerator;

    @Autowired
    FacilityGenerator facilityGenerator;


    @Test
    public void shouldValidateIfPatientIsAlreadyRegistered() throws Exception {
        final XformHttpClient.XformResponse xformResponse = setupMobileMidwifeFormAndUpload(new HashMap<String, String>() {{
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
        final XformHttpClient.XformResponse xformResponse = setupMobileMidwifeFormAndUpload(new HashMap<String, String>() {{
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
        final XformHttpClient.XformResponse xformResponse = setupMobileMidwifeFormAndUpload(new HashMap<String, String>() {{
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
        final XformHttpClient.XformResponse xformResponse = setupMobileMidwifeFormAndUpload(MapUtils.EMPTY_MAP);

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("staffId"), hasItem("is mandatory"));
        assertThat(errorsMap.get("facilityId"), hasItem("is mandatory"));
        assertThat(errorsMap.get("patientId"), hasItem("is mandatory"));
        assertThat(errorsMap.get("consent"), hasItem("is mandatory"));
    }



   @Test
    public void shouldRegisterForMobileMidWifeProgramIfValidationsPass() throws Exception{

        final String staffId=staffGenerator.createDummyStaffAndReturnStaffId();
        final String facilityId=facilityGenerator.createDummyFacilityAndReturnFacilityId();
        final String patientId = patientGenerator.createDummyPatientAndReturnPatientId(facilityId);

      final XformHttpClient.XformResponse xformResponse = setupMobileMidwifeFormAndUpload(new HashMap<String, String>() {{

            put("patientId",patientId);
            put("staffId",staffId);
            put("facilityId",facilityId);
            put("consent", "Y");
        }});

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 0);

    }

    private XformHttpClient.XformResponse setupMobileMidwifeFormAndUpload(Map<String, String> data) throws Exception {
        return XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("mobile-midwife-template.xml", data));
    }


}
