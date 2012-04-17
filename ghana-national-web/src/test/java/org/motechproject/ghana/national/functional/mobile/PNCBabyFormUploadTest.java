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
import org.motechproject.util.DateUtil;
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
public class PNCBabyFormUploadTest extends OpenMRSAwareFunctionalTest {

    @Test
    public void shouldUploadPNCBabyForm() throws Exception {
        final String staffId = staffGenerator.createStaff(browser, homePage);

        DataGenerator dataGenerator = new DataGenerator();
        String patientFirstName = "patient first name" + dataGenerator.randomString(5);
        final TestPatient testPatient = TestPatient.with(patientFirstName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.CHILD_UNDER_FIVE)
                .dateOfBirth(DateUtil.now().minusYears(2).toLocalDate())
                .estimatedDateOfBirth(false);

        final PatientPage patientPage = browser.toCreatePatient(homePage);
        patientPage.create(testPatient);

        final String visitDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());
        final String patientId = patientPage.motechId();

        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.pncChildForm(), new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", "13212");
            put("date", visitDate);
            put("motechId", patientId);
            put("visitNumber", "2");
            put("weight", "12");
            put("temperature", "32");
            put("location", "2");
            put("house", "1");
            put("community", "1");
            put("referred", "Y");
            put("maleInvolved", "Y");
            put("respiration", "47");
            put("cordConditionNormal", "Y");
            put("babyConditionGood", "Y");
            put("bcg", "Y");
            put("opv0", "Y");
            put("comments", "nO cOMMENTS");
        }});

        assertEquals(1, xformResponse.getSuccessCount());
        OpenMRSPatientPage openMRSPatientPage = openMRSBrowser.toOpenMRSPatientPage(openMRSDB.getOpenMRSId(patientId));
        String encounterId = openMRSPatientPage.chooseEncounter("PNCCHILDVISIT");
        OpenMRSEncounterPage openMRSEncounterPage = openMRSBrowser.toOpenMRSEncounterPage(encounterId);
        openMRSEncounterPage.displaying(asList(
                new OpenMRSObservationVO("ANC PNC LOCATION", "2.0"),
                new OpenMRSObservationVO("COMMENTS", "nO cOMMENTS"),
                new OpenMRSObservationVO("COMMUNITY", "1"),
                new OpenMRSObservationVO("CONDITION OF BABY", "true"),
                new OpenMRSObservationVO("CORD CONDITION", "true"),
                new OpenMRSObservationVO("HOUSE", "1"),
                new OpenMRSObservationVO("BACILLE CAMILE-GUERIN VACCINATION", "Y"),
                new OpenMRSObservationVO("MALE INVOLVEMENT", "true"),
                new OpenMRSObservationVO("ORAL POLIO VACCINATION DOSE", "1.0"),
                new OpenMRSObservationVO("REFERRED", "true"),
                new OpenMRSObservationVO("RESPIRATORY RATE", "47.0"),
                new OpenMRSObservationVO("TEMPERATURE (C)", "32.0"),
                new OpenMRSObservationVO("VISIT NUMBER", "2.0"),
                new OpenMRSObservationVO("WEIGHT (KG)", "12.0")
        ));
    }
}