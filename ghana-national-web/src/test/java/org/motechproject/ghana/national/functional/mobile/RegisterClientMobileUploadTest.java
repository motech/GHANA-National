package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNull;


public class RegisterClientMobileUploadTest {
    @Test
    public void shouldCheckForMandatoryFields() throws Exception {

        final XformHttpClient.XformResponse xformResponse = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("register-client-template.xml", MapUtils.EMPTY_MAP));

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, String> errorsMap = errors.iterator().next().getErrors();

        assertEquals("is mandatory", errorsMap.get("registrationMode"));
        assertEquals("is mandatory", errorsMap.get("registrantType"));
        assertEquals("is mandatory", errorsMap.get("firstName"));
        assertEquals("is mandatory", errorsMap.get("lastName"));
        assertEquals("is mandatory", errorsMap.get("dateOfBirth"));
        assertEquals("is mandatory", errorsMap.get("date"));
        assertEquals("is mandatory", errorsMap.get("estimatedBirthDate"));
        assertEquals("is mandatory", errorsMap.get("insured"));
        assertEquals("is mandatory", errorsMap.get("date"));
        assertEquals("is mandatory", errorsMap.get("address"));
        assertEquals("not found", errorsMap.get("facilityId"));
        assertEquals("not found", errorsMap.get("staffId"));
    }

    @Test
    public void shouldNotGiveErrorForFirstNameIfGiven() throws Exception {

        final XformHttpClient.XformResponse xformResponse = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("register-client-template.xml", new HashMap<String, String>() {{
            put("firstName", "Joe");
        }}));

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, String> errorsMap = errors.iterator().next().getErrors();
        assertNull(errorsMap.get("firstName"));
    }
}
