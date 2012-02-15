package org.motechproject.ghana.national.functional.mobile;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.functional.data.TestANCEnrollment;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.framework.XformHttpClient;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.pages.patient.ANCEnrollmentPage;
import org.motechproject.ghana.national.functional.pages.patient.PatientEditPage;
import org.motechproject.ghana.national.functional.pages.patient.PatientPage;
import org.motechproject.ghana.national.functional.pages.patient.SearchPatientPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.testng.AssertJUnit.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class ANCVisitFormUploadTest extends LoggedInUserFunctionalTest {
    @Test
    public void shouldUploadANCVisitFormSuccessfully() {
        // create
        final String staffId = staffGenerator.createStaff(browser, homePage);

        DataGenerator dataGenerator = new DataGenerator();
        String patientFirstName = "patient first name" + dataGenerator.randomString(5);
        final TestPatient testPatient = TestPatient.with(patientFirstName, staffId)
                .patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER)
                .estimatedDateOfBirth(false);

        PatientPage patientPage = browser.toCreatePatient(homePage);
        patientPage.create(testPatient);

        TestANCEnrollment ancEnrollment = TestANCEnrollment.create().withStaffId(staffId)
                .withRegistrationToday(RegistrationToday.IN_PAST);
        SearchPatientPage searchPatientPage = browser.toSearchPatient(patientPage);

        searchPatientPage.searchWithName(patientFirstName);
        searchPatientPage.displaying(testPatient);

        PatientEditPage patientEditPage = browser.toPatientEditPage(searchPatientPage, testPatient);
        final ANCEnrollmentPage ancEnrollmentPage = browser.toEnrollANCPage(patientEditPage);
        ancEnrollmentPage.save(ancEnrollment);

        XformHttpClient.XformResponse xformResponse = mobile.upload(MobileForm.ancVisitForm(), new HashMap<String, String>() {{
            put("staffId", staffId);
            put("facilityId", testPatient.facilityId());
            put("motechId", ancEnrollmentPage.getMotechPatientId());
            put("date", "2012-01-03");
            put("serialNumber", "4ds65");
            put("visitNumber", "4");
            put("estDeliveryDate", "2012-01-03");
            put("bpDiastolic", "67");
            put("bpSystolic", "10");
            put("weight", "65.67");
            put("comments", "comments");
            put("ttdose", "4");
            put("iptdose", "2");
            put("iptReactive", "Y");
            put("itnUse", "N");
            put("fht", "4.3");
            put("fhr", "4");
            put("urineTestGlucosePositive", "1");
            put("urineTestProteinPositive", "1");
            put("hemoglobin", "13.8");
            put("vdrlReactive", "N");
            put("vdrlTreatment", "Y");
            put("dewormer", "Y");
            put("pmtct", "Y");
            put("preTestCounseled", "Y");
            put("hivTestResult", "POSITIVE");
            put("postTestCounseled", "Y");
            put("pmtctTreament", "Y");
            put("location", "2");
            put("house", "house");
            put("community", "community");
            put("referred", "Y");
            put("maleInvolved", "N");
            put("nextANCDate", "2012-02-20");
        }});

        assertEquals(1, xformResponse.getSuccessCount());
    }
}
