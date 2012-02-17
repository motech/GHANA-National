package org.motechproject.ghana.national.functional.mobile;


import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.util.DateUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.joda.time.format.DateTimeFormat.forPattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class OutPatientVisitFormUploadTest extends LoggedInUserFunctionalTest {

    @Test
    public void shouldUploadOutPatientVisitFormSuccessfullyIfPatientIsNotAVisitor() throws Exception {
        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String facilityId = facilityGenerator.createFacility(browser, homePage);
        final String patientId = patientGenerator.createPatientWithStaff(browser, homePage, staffId);
        final String serialNumber = "serialNumber";
        final LocalDate visitDate= DateUtil.today();

        final XformHttpClient.XformResponse response = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XformHttpClient.XFormParser.parse("out-patient-visit-template.xml", new HashMap<String, String>() {{


            put("staffId", staffId);
            put("facilityId", facilityId);
            put("registrantType", TestPatient.PATIENT_TYPE.PREGNANT_MOTHER.toString());
            put("visitor","N");
            put("motechId", patientId);
            put("serialNumber", serialNumber);
            put("visitDate", visitDate.toString(forPattern("yyyy-MM-dd")));
            put("newCase", "Y");
            put("newPatient", "Y");
            put("diagnosis", "78");
            put("otherDiagnosis", "10");
            put("referred", "Y");

        }}));


        assertEquals(1,response.getSuccessCount());
    }

    @Test
    public void shouldUploadOutPatientVisitFormSuccessfullyIfPatientIsAVisitor() throws Exception {
        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String facilityId = facilityGenerator.createFacility(browser, homePage);
        final String serialNumber = "serialNumber";
        final LocalDate visitDate= DateUtil.today();
        final LocalDate dateOfBirth= new LocalDate(2000,12,12);
        final LocalDate nhisExpires= new LocalDate(2012,12,12);
        final String nhis = "nhis";

        final XformHttpClient.XformResponse response = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XformHttpClient.XFormParser.parse("out-patient-visit-template.xml", new HashMap<String, String>() {{



            put("staffId", staffId);
            put("facilityId", facilityId);
            put("registrantType", TestPatient.PATIENT_TYPE.PREGNANT_MOTHER.toString());
            put("serialNumber", serialNumber);
            put("visitDate", visitDate.toString(forPattern("yyyy-MM-dd")));
            put("visitor","Y");
            put("dateOfBirth", dateOfBirth.toString(forPattern("yyyy-MM-dd")));
            put("insured", "Y");
            put("nhis", nhis);
            put("nhisExpires", nhisExpires.toString(forPattern("yyyy-MM-dd")));
            put("newCase", "Y");
            put("newPatient", "Y");
            put("diagnosis", "78");
            put("otherDiagnosis", "10");
            put("gender", "M");
            put("rdtGiven", "Y");
            put("rdtPositive", "Y");

            put("referred", "Y");

        }}));


        assertEquals(1,response.getSuccessCount());
    }

}
