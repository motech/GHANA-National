package org.motechproject.ghana.national.functional.mobile;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.OpenMRSAwareFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSEncounterPage;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSPatientPage;
import org.motechproject.ghana.national.functional.pages.openmrs.vo.OpenMRSObservationVO;
import org.motechproject.ghana.national.functional.pages.patient.PatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class PNCMotherFormUploadTest extends OpenMRSAwareFunctionalTest {

    @Test
    public void shouldUploadPNCMotherForm() {
        final String staffId = staffGenerator.createStaff(browser, homePage);

        DataGenerator dataGenerator = new DataGenerator();
        String patientFirstName = "patient first name" + dataGenerator.randomString(5);
        final TestPatient testPatient = TestPatient.with(patientFirstName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);

        final PatientPage patientPage = browser.toCreatePatient(homePage);
        patientPage.create(testPatient);

        final String visitDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());
        final String patientId = patientPage.motechId();

        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.pncMotherForm(), new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", "13212");
            put("date", visitDate);
            put("motechId", patientId);
            put("visitNumber", "2");
            put("vitaminA", "Y");
            put("ttDose", "1");
            put("location", "1");
            put("referred", "Y");
            put("maleInvolved", "Y");
            put("lochiaColour", "1");
            put("lochiaOdourFoul", "Y");
            put("lochiaAmountExcess", "Y");
            put("temperature", "32");
            put("fht", "22");
            put("comments", "nO cOMMENTS");
        }});

        assertEquals("Errors: " + xformResponse.getErrors(), 1, xformResponse.getSuccessCount());
        OpenMRSPatientPage openMRSPatientPage = openMRSBrowser.toOpenMRSPatientPage(openMRSDB.getOpenMRSId(patientId));
        String encounterId = openMRSPatientPage.chooseEncounter("PNCMOTHERVISIT");
        OpenMRSEncounterPage openMRSEncounterPage = openMRSBrowser.toOpenMRSEncounterPage(encounterId);
        openMRSEncounterPage.displaying(asList(
//                new OpenMRSObservationVO("ANC PNC LOCATION", "1.0"),
                new OpenMRSObservationVO("COMMENTS", "nO cOMMENTS"),
                new OpenMRSObservationVO("FUNDAL HEIGHT", "22.0"),
                new OpenMRSObservationVO("VITAMIN A", "1.0"),
                new OpenMRSObservationVO("LOCHIA COLOUR", "1.0"),
                new OpenMRSObservationVO("LOCHIA EXCESS AMOUNT", "true"),
                new OpenMRSObservationVO("LOCHIA FOUL ODOUR", "true"),
                new OpenMRSObservationVO("VISIT NUMBER", "2.0"),
                new OpenMRSObservationVO("MALE INVOLVEMENT", "true"),
                new OpenMRSObservationVO("REFERRED", "true"),
                new OpenMRSObservationVO("TEMPERATURE (C)", "32.0"),
                new OpenMRSObservationVO("TETANUS TOXOID DOSE", "1.0")
        ));
    }
}