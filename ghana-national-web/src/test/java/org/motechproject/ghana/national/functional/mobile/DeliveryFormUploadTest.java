package org.motechproject.ghana.national.functional.mobile;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.pages.patient.PatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.testng.AssertJUnit.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class DeliveryFormUploadTest extends LoggedInUserFunctionalTest{
    @Test
    public void shouldUploadDeliveryFormSuccessfully() {
        // create
        final String staffId = staffGenerator.createStaff(browser, homePage);

        DataGenerator dataGenerator = new DataGenerator();
        String patientFirstName = "patient first name" + dataGenerator.randomString(5);
        final TestPatient testPatient = TestPatient.with(patientFirstName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);

        final PatientPage patientPage = browser.toCreatePatient(homePage);
        patientPage.create(testPatient);

        XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.deliveryForm(), new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", testPatient.facilityId());
            put("motechId", patientPage.motechId());
            put("date", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date()));
            put("mode", ChildDeliveryMode.NORMAL.name());
            put("outcome", ChildDeliveryOutcome.SINGLETON.name());
            put("maleInvolved", "Y");
            put("deliveryLocation", ChildDeliveryLocation.CHAG.name());
            put("deliveredBy", ChildDeliveredBy.DOCTOR.name());
            put("maternalDeath", "N");
            put("child1Outcome", BirthOutcome.ALIVE.name());
            put("child1RegistrationType", RegistrationType.AUTO_GENERATE_ID.name());
            put("child1Sex", "M");
            put("child1FirstName", "baby");
            put("child1Weight", "3.5");
            put("sender", "0987654321");
        }});
        assertEquals(1, xformResponse.getSuccessCount());
    }
}
