package org.motechproject.ghana.national.functional.mobile;


import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.OpenMRSAwareFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSEncounterPage;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSPatientPage;
import org.motechproject.ghana.national.functional.pages.openmrs.vo.OpenMRSObservationVO;
import org.motechproject.util.DateUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.util.HashMap;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.joda.time.format.DateTimeFormat.forPattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class OutPatientVisitFormUploadTest extends OpenMRSAwareFunctionalTest {

    @Test
    public void shouldUploadOutPatientVisitFormSuccessfullyIfPatientIsNotAVisitor() throws Exception {
        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String facilityId = facilityGenerator.createFacility(browser, homePage);
        final String patientId = patientGenerator.createPatient(browser, homePage, staffId);
        final String serialNumber = "serialNumber";
        final LocalDate visitDate = DateUtil.today();

        final XformHttpClient.XformResponse response = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XformHttpClient.XFormParser.parse("out-patient-visit-template.xml", new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", facilityId);
            put("registrantType", TestPatient.PATIENT_TYPE.PREGNANT_MOTHER.toString());
            put("visitor", "N");
            put("motechId", patientId);
            put("serialNumber", serialNumber);
            put("visitDate", visitDate.toString(forPattern("yyyy-MM-dd")));
            put("newCase", "Y");
            put("newPatient", "Y");
            put("diagnosis", "10");
            put("secondDiagnosis", "78");
            put("rdtGiven", "Y");
            put("rdtPositive", "N");
            put("referred", "Y");
            put("comments", "Out Patient Visit");

        }}));

        assertEquals(1, response.getSuccessCount());

        OpenMRSPatientPage openMRSPatientPage = openMRSBrowser.toOpenMRSPatientPage(openMRSDB.getOpenMRSId(patientId));
        String encounterId = openMRSPatientPage.chooseEncounter("OUTPATIENTVISIT");
        OpenMRSEncounterPage openMRSEncounterPage = openMRSBrowser.toOpenMRSEncounterPage(encounterId);
        openMRSEncounterPage.displaying(asList(
                new OpenMRSObservationVO("NEW CASE", "true"),
                new OpenMRSObservationVO("NEW PATIENT", "true"),
                new OpenMRSObservationVO("PRIMARY DIAGNOSIS", "10.0"),
                new OpenMRSObservationVO("SECONDARY DIAGNOSIS", "78.0"),
                new OpenMRSObservationVO("COMMENTS", "Out Patient Visit"),
                new OpenMRSObservationVO("SERIAL NUMBER", serialNumber),
                new OpenMRSObservationVO("MALARIA RAPID TEST", "NEGATIVE"),
                new OpenMRSObservationVO("REFERRED", "true")
        ));
    }

    @Test
    public void shouldUploadOutPatientVisitFormSuccessfullyIfPatientIsAVisitor() throws Exception {
        final String staffId = staffGenerator.createStaff(browser, homePage);
        final String facilityId = facilityGenerator.createFacility(browser, homePage);
        final String serialNumber = "serialNumber";
        final LocalDate visitDate = DateUtil.today();
        final LocalDate dateOfBirth = new LocalDate(2000, 12, 12);
        final LocalDate nhisExpires = new LocalDate(2012, 12, 12);
        final String nhis = "nhis";

        final XformHttpClient.XformResponse response = XformHttpClient.execute("http://localhost:8080/ghana-national-web/formupload",
                "NurseDataEntry", XformHttpClient.XFormParser.parse("out-patient-visit-template.xml", new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", facilityId);
            put("registrantType", TestPatient.PATIENT_TYPE.PREGNANT_MOTHER.toString());
            put("serialNumber", serialNumber);
            put("visitDate", visitDate.toString(forPattern("yyyy-MM-dd")));
            put("visitor", "Y");
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

        assertEquals(1, response.getSuccessCount());
    }
}
