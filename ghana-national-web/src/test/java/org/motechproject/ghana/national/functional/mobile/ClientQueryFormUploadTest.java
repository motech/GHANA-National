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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("#{functionalTestProperties['port']}")
    private String port;

    @Value("#{functionalTestProperties['host']}")
    private String host;

    @Test
    public void shouldUploadFormWithClientQueryTypeAsFindClientID() throws IOException {
        DataGenerator dataGenerator = new DataGenerator();

        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String firstPatientNameGenerated = dataGenerator.randomString(5);
        String firstPatientName = firstPatientNameGenerated + "XXX Client First Name";
        final TestPatient firstTestPatient = TestPatient.with(firstPatientName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);
        final String firstPatientId = patientGenerator.createPatientWithStaff(firstTestPatient, browser, homePage);

        String secondPatientName = dataGenerator.randomString(5)+"XXXXXXClient First ";
        TestPatient secondTestPatient = TestPatient.with(secondPatientName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);
        final String secondPatientId = patientGenerator.createPatientWithStaff(secondTestPatient, browser, homePage);

        HashMap<String, String> inputParams = new HashMap<String, String>() {{
            put("facilityId", firstTestPatient.facilityId());
            put("staffId", staffId);
            put("queryType", ClientQueryType.FIND_CLIENT_ID.toString());
            put("firstName", firstPatientNameGenerated);
            put("sender", "0987654321");
        }};
        XformHttpClient.XformResponse response = mobile.upload(MobileForm.queryClientForm(), inputParams);
        assertEquals(1, response.getSuccessCount());

        String responseBodyAsString = getMessageGatewayResponse();

        assertThat(responseBodyAsString, containsString(firstPatientId));
        assertThat(responseBodyAsString, containsString(firstPatientName));
    }

    @Value("#{functionalTestProperties['deliveryPath']}")
    private String deliveryPath;

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

    private String getMessageGatewayResponse() throws IOException {
        String url = String.format("http://%s:%s/%s", host, port, deliveryPath);
        GetMethod getMethod = new GetMethod(url);
        HttpClient httpClient = new HttpClient();
        httpClient.executeMethod(getMethod);
        return getMethod.getResponseBodyAsString();
    }

}