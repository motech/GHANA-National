package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.motechproject.functional.framework.XformHttpClient;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.motechproject.functional.framework.XformHttpClient.XFormParser;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNull;


public class RegisterClientFromMobileTest {

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
}
