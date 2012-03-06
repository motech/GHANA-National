package org.motechproject.ghana.national.functional.mobile;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.pages.patient.PatientEditPage;
import org.motechproject.ghana.national.functional.pages.patient.PatientPage;
import org.motechproject.ghana.national.functional.pages.patient.SearchPatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class PNCMotherFormUploadTest extends LoggedInUserFunctionalTest {

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

        final XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.pncMotherForm(), new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", "13212");
            put("date", visitDate);
            put("motechId", patientPage.motechId());
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
    }

    private PatientEditPage toPatientEditPage(TestPatient testPatient) {
        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithName(testPatient.firstName());
        searchPatientPage.displaying(testPatient);
        return browser.toPatientEditPage(searchPatientPage, testPatient);
    }
}
