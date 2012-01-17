package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.apache.xpath.operations.Bool;
import org.motechproject.functional.framework.XformHttpClient;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.motechproject.functional.framework.XformHttpClient.XFormParser;
import static org.testng.Assert.assertEquals;

public class RegisterMobileMidwifeTest {

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
        String patientId = "1234";


      final XformHttpClient.XformResponse xformResponse = setupMobileMidwifeFormAndUpload(new HashMap<String, String>() {{

          put("patientId",  patientId);
            put("staffId","5678");
            put("facilityId","576475");
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
