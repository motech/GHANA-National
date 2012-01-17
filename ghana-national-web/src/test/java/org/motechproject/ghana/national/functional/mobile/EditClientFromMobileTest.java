package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.Generator.FacilityGenerator;
import org.motechproject.ghana.national.functional.Generator.StaffGenerator;
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
@ContextConfiguration(locations = {"classpath:/testApplicationContext.xml"})
public class EditClientFromMobileTest {

    @Autowired
    StaffGenerator staffGenerator;
    @Autowired
    FacilityGenerator facilityGenerator;

    @Test
    public void shouldCheckForMandatoryFields() throws Exception {

        final XformHttpClient.XformResponse xformResponse = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("edit-client-template.xml", MapUtils.EMPTY_MAP));

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
        final XformHttpClient.XformResponse xformResponse = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("edit-client-template.xml", new HashMap<String, String>() {{
            put("facilityId", "testFacilityId");
            put("motechId" , "testMotechId");
            put("staffId" , "testStaffId");
            put("motherMotechId" , "testMotherMotechId");

        }}));

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("facilityId"), hasItem("not found"));
        assertThat(errorsMap.get("staffId"), hasItem("not found"));
        assertThat(errorsMap.get("motechId"), hasItem("not found"));

    }

    @Test
    public void shouldNotGiveErrorForFirstNameIfGiven() throws Exception {

        final XformHttpClient.XformResponse xformResponse = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XFormParser.parse("edit-client-template.xml", new HashMap<String, String>() {{
            put("firstName", "Joe");
        }}));

        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();
        assertNull(errorsMap.get("firstName"));
    }

    @Test
    public void shouldUpdatePatientIfNoErrorsAreFound() {
        String staffId = staffGenerator.createDummyStaffAndReturnStaffId();
        String facilityId = facilityGenerator.createDummyFacilityAndReturnFacilityId();
    }
}
