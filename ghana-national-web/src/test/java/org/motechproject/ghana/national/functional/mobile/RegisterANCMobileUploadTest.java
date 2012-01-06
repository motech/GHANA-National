package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.assertEquals;

public class RegisterANCMobileUploadTest {

    @Test
    public void shouldCheckForMandatoryFields() throws Exception {
        final XformHttpClient.XformResponse xformResponse = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("register-anc-template.xml", MapUtils.EMPTY_MAP));
        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("gravida"), hasItem("is mandatory"));
        assertThat(errorsMap.get("parity"), hasItem("is mandatory"));
        assertThat(errorsMap.get("height"), hasItem("is mandatory"));
        assertThat(errorsMap.get("estDeliveryDate"), hasItem("is mandatory"));
        assertThat(errorsMap.get("ancRegNumber"), hasItem("is mandatory"));
    }

    @Test
    public void shouldValidateIfFacilityIdStaffIdMotechIdExists() throws Exception {
        final XformHttpClient.XformResponse xformResponse = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("register-anc-template.xml", new HashMap<String, String>() {{
            put("motechId", "-1");
            put("facilityId", "-1");
            put("staffId", "-1");
            put("gravida", "1");
            put("parity", "1");
            put("height", "61");
            put("estDeliveryDate", "2010-12-11");
            put("date", "2012-12-11");
            put("deliveryDateConfirmed", "Y");
            put("regDateToday", "TODAY");
            put("addHistory", "0");
            put("ancRegNumber", "123ABC");
        }}));
        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("staffId"), hasItem("not found"));
        assertThat(errorsMap.get("facilityId"), hasItem("not found"));
        assertThat(errorsMap.get("motechId"), hasItem("not found"));
    }
}
