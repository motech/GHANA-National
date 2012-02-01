package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.domain.RegistrationToday;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.motechproject.ghana.national.functional.framework.XformHttpClient.XFormParser;
import static org.testng.Assert.assertEquals;

public class RegisterCWCMobileUploadTest {

    @Test
    public void shouldCheckForAllMandatoryDetails() throws Exception {
        final XformHttpClient.XformResponse xformResponse = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("register-cwc-template.xml", MapUtils.EMPTY_MAP));
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
        final XformHttpClient.XformResponse xformResponse = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("register-cwc-template.xml", new HashMap<String, String>() {{
                    put("motechId", "-1");
                    put("facilityId", "-1");
                    put("staffId", "-1");
                    put("registrationToday", RegistrationToday.TODAY.toString());
                    put("registrationDate", "2012-01-03");
                    put("serialNumber","1234243243");
        }}));
        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("staffId"), hasItem("not found"));
        assertThat(errorsMap.get("facilityId"), hasItem("not found"));
        assertThat(errorsMap.get("motechId"), hasItem("not found"));
    }

}
