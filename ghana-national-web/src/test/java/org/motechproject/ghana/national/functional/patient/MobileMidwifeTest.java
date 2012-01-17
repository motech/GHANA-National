package org.motechproject.ghana.national.functional.patient;

import org.junit.runner.RunWith;
import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.data.TestStaff;
import org.motechproject.functional.pages.patient.MobileMidwifeEnrollmentPage;
import org.motechproject.functional.pages.patient.PatientPage;
import org.motechproject.functional.pages.staff.StaffPage;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.ghana.national.service.IdentifierGenerationService;
import org.motechproject.model.DayOfWeek;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml","cl"})
public class MobileMidwifeTest extends LoggedInUserFunctionalTest {

    @Autowired
    private IdentifierGenerationService identifierGenerationService;
    private DataGenerator dataGenerator;

    @BeforeMethod
    public void setUp() {
        dataGenerator = new DataGenerator();
    }

    @Test
    public void shouldEnrollPatientForMobileMidwifeProgram() {

        TestStaff staff = createStaff("Staff Jence");
        PatientPage patientPage = browser.toCreatePatient(homePage);
        TestPatient patient = createPatient(patientPage, "Samy Johnson"  + dataGenerator.randomString(5));

        MobileMidwifeEnrollmentPage enrollmentPage = browser.toMobileMidwifeEnrollmentForm(patientPage);

        enrollmentPage.withStaffMotechId(staff.motechId())
                .withFacilityMotechId(patient.facilityId()).withConsent(true)
                .withServiceType(ServiceType.PREGNANCY.toString())
                .withPhoneOwnership(PhoneOwnership.PERSONAL.toString())
                .withPhoneNumber("0967891000")
                .withMedium(Medium.SMS.toString())
                .withDayOfWeek(DayOfWeek.Sunday.toString())
                .withTime("10", "2")
                .withLanguage(Language.EN.toString())
                .withLearnedFrom(LearnedFrom.MOTECH_FIELD_AGENT.toString())
                .withReasonToJoin(ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH.toString())
                .withMessageStartWeek(MessageStartWeek.messageStartWeeks().get(2).getValue())
                .submit();
        assertTrue(enrollmentPage.getDriver().findElement(By.className("success")).getText().equals("Enrolled successfully."));
    }

    private TestPatient createPatient(PatientPage patientPage, String patientName) {
        TestPatient patient = TestPatient.with(patientName).
                registrationMode(TestPatient.PATIENT_REGN_MODE.AUTO_GENERATE_ID).
                patientType(TestPatient.PATIENT_TYPE.PATIENT_MOTHER).estimatedDateOfBirth(false);
        patientPage.create(patient);
        return patient;
    }

    private TestStaff createStaff(String firstName) {
        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        TestStaff staff = TestStaff.with(firstName);
        staffPage.create(staff);
        return staff;
    }

}
