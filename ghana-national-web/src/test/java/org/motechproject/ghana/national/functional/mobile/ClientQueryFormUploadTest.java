package org.motechproject.ghana.national.functional.mobile;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.domain.ClientQueryType;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.motechproject.util.DateUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class ClientQueryFormUploadTest extends LoggedInUserFunctionalTest {

    @Test
    public void shouldCheckIfAtleastOneInfoIsEnteredForFindClientIDQuery() throws Exception {
        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.queryClientForm(), new HashMap<String, String>() {
            {
                put("facilityId", "-1");
                put("staffId", "-1");
                put("queryType", ClientQueryType.FIND_CLIENT_ID.toString());
                put("sender", "0987654321");
            }
        });
        final List<XformHttpClient.Error> errors = xformResponse.getErrors();

        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("queryType"), hasItem(Constants.INSUFFICIENT_SEARCH_CRITERIA));
    }

    @Test
    public void shouldCheckFormMandatoryFieldsForClientDetailsQuery() throws Exception {
        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.queryClientForm(), new HashMap<String, String>() {{
            put("motechId", "-1");
            put("facilityId", "-1");
            put("staffId", "-1");
            put("queryType", ClientQueryType.CLIENT_DETAILS.toString());
        }});
        final List<XformHttpClient.Error> errors = xformResponse.getErrors();
        assertEquals(errors.size(), 1);
        final Map<String, List<String>> errorsMap = errors.iterator().next().getErrors();

        assertThat(errorsMap.get("staffId"), hasItem("not found"));
        assertThat(errorsMap.get("facilityId"), hasItem("not found"));
        assertThat(errorsMap.get("motechId"), hasItem("not found"));
    }

    @Test
    public void shouldUploadFormWithClientQueryTypeAsClientDetails() throws IOException {
        DataGenerator dataGenerator = new DataGenerator();

        final String staffId = staffGenerator.createStaff(browser, homePage);
        String firstName = "First Name" + dataGenerator.randomString(5);
        TestPatient testPatient = TestPatient.with(firstName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);

        final String patientId = patientGenerator.createPatientWithStaff(testPatient, browser, homePage);
        HashMap<String, String> inputParams = new HashMap<String, String>() {{
            put("motechId", patientId);
            put("facilityId", "13212");
            put("staffId", staffId);
            put("queryType", ClientQueryType.CLIENT_DETAILS.toString());
            put("sender", "0987654321");
        }};
        XformHttpClient.XformResponse response = mobile.upload(MobileForm.queryClientForm(), inputParams);
        assertEquals(1, response.getSuccessCount());

        String responseBodyAsString = getMessageGatewayResponse();

        assertThat(responseBodyAsString, containsString(patientId));
        assertThat(responseBodyAsString, containsString(firstName));
    }

    @Test
    public void shouldUploadFormWithClientQueryTypeAsFindClientID() throws IOException {
        DataGenerator dataGenerator = new DataGenerator();

        final String staffId = staffGenerator.createStaff(browser, homePage);
        String firstPatientName = "Client First Name" + dataGenerator.randomString(5);
        final TestPatient firstTestPatient = TestPatient.with(firstPatientName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);
        final String firstPatientId = patientGenerator.createPatientWithStaff(firstTestPatient, browser, homePage);

        String secondPatientName = "Client First " + dataGenerator.randomString(5);
        TestPatient secondTestPatient = TestPatient.with(secondPatientName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);
        final String secondPatientId = patientGenerator.createPatientWithStaff(secondTestPatient, browser, homePage);

        HashMap<String, String> inputParams = new HashMap<String, String>() {{
            put("facilityId", firstTestPatient.facilityId());
            put("staffId", staffId);
            put("queryType", ClientQueryType.FIND_CLIENT_ID.toString());
            put("firstName", "Clie");
            put("lastName", "Last");
            put("sender", "0987654321");
        }};
        XformHttpClient.XformResponse response = mobile.upload(MobileForm.queryClientForm(), inputParams);
        assertEquals(1, response.getSuccessCount());

        String responseBodyAsString = getMessageGatewayResponse();

        assertThat(responseBodyAsString, containsString(firstPatientId));
        assertThat(responseBodyAsString, containsString(secondPatientId));
        assertThat(responseBodyAsString, containsString(firstPatientName));
        assertThat(responseBodyAsString, containsString(secondPatientName));
    }

    @Test
    public void shouldUploadFormWithClientQueryTypeAsFindClientIDWithDateOfBirth() throws IOException {
        DataGenerator dataGenerator = new DataGenerator();

        final String staffId = staffGenerator.createStaff(browser, homePage);
        String firstPatientName = "Client First Name" + dataGenerator.randomString(5);
        final TestPatient firstTestPatient = TestPatient.with(firstPatientName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);
        final String firstPatientId = patientGenerator.createPatientWithStaff(firstTestPatient, browser, homePage);

        String secondPatientName = "Client First " + dataGenerator.randomString(5);
        TestPatient secondTestPatient = TestPatient.with(secondPatientName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false).dateOfBirth(DateUtil.newDate(2000, 9, 9));

        final String secondPatientId = patientGenerator.createPatientWithStaff(secondTestPatient, browser, homePage);

        HashMap<String, String> inputParams = new HashMap<String, String>() {{
            put("facilityId", firstTestPatient.facilityId());
            put("staffId", staffId);
            put("queryType", ClientQueryType.FIND_CLIENT_ID.toString());
            put("dateOfBirth", "2000-09-09");
            put("sender", "0987654321");
        }};
        XformHttpClient.XformResponse response = mobile.upload(MobileForm.queryClientForm(), inputParams);
        assertEquals(1, response.getSuccessCount());

        String responseBodyAsString = getMessageGatewayResponse();

        assertThat(responseBodyAsString, containsString(secondPatientId));
        assertThat(responseBodyAsString, containsString(secondPatientName));
        assertThat(responseBodyAsString, containsString("9 Sep, 2000"));
    }


    private String getMessageGatewayResponse() throws IOException {
        GetMethod getMethod = new GetMethod("http://localhost:8080/deliverytools/motech-delivery-tools/outbound/all");
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(getMethod);
        return getMethod.getResponseBodyAsString();
    }

}