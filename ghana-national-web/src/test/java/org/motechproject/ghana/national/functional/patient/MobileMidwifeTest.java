
package org.motechproject.ghana.national.functional.patient;

import org.junit.runner.RunWith;
import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.data.TestStaff;
import org.motechproject.functional.pages.home.HomePage;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class MobileMidwifeTest extends LoggedInUserFunctionalTest {

    @Autowired
    private IdentifierGenerationService identifierGenerationService;
    private DataGenerator dataGenerator;
    private TestStaff staff;
    private TestPatient patient;

    @org.testng.annotations.BeforeClass
    protected void runOnce() {
        login();
        staff = createStaff("Staff Jence");
        createPatient("Samy Johnson");
    }

    @BeforeMethod
    public void setUp() {
         dataGenerator = new DataGenerator();
    }

    @Test
    public void shouldNotSubmitDayOfWeekAndTimeOfDayForSMSMedium_ForMobileMidwifeEnrollment() {

        MobileMidwifeEnrollmentPage enrollmentPage = goToMobileMidwifePage(homePage, patient);
        enrollmentPage.withStaffMotechId(staff.motechId()).withFacilityMotechId(patient.facilityId()).withConsent(true)
                .withServiceType(ServiceType.PREGNANCY.toString()) .withPhoneOwnership(PhoneOwnership.PERSONAL.toString())
                .withMedium(Medium.SMS.toString());

        assertTrue(enrollmentPage.validate());

        enrollmentPage.withMedium(Medium.VOICE.toString()).withDayOfWeek(DayOfWeek.Sunday.toString())
                .withTime("10", "2");
        assertTrue(enrollmentPage.validate());

        enrollmentPage.withConsent(false);
        assertTrue(enrollmentPage.validate());
    }
        
    @Test
    public void shouldCreateAndEditMobileMidwifeProgramEnrollmentForPatient() {


        MobileMidwifeEnrollmentPage enrollmentPage = goToMobileMidwifePage(homePage, patient);
        enrollmentPage = createPregnancyEnrollmentAndSubmit(enrollmentPage);

        assertTrue(enrollmentPage.getDriver().findElement(By.className("success")).getText().equals("Enrolled successfully."));
        
        enrollmentPage.withStaffMotechId(staff.motechId())
                .withFacilityMotechId(patient.facilityId()).withConsent(true)
                .withServiceType(ServiceType.CHILD_CARE.toString())
                .withPhoneOwnership(PhoneOwnership.HOUSEHOLD.toString())
                .withPhoneNumber("0999111100")
                .withMedium(Medium.VOICE.toString())
                .withDayOfWeek(DayOfWeek.Sunday.toString())
                .withTime("10", "2")
                .withLanguage(Language.FAN.toString())
                .withLearnedFrom(LearnedFrom.POSTERS_ADS.toString())
                .withReasonToJoin(ReasonToJoin.FAMILY_FRIEND_DELIVERED.toString())
                .withMessageStartWeek(MessageStartWeek.messageStartWeeks().get(40 + 10).getValue())
                .submit();
        enrollmentPage = browser.toMobileMidwifeEnrollmentForm(enrollmentPage);
        assertTrue(enrollmentPage.getDriver().findElement(By.className("success")).getText().equals("Enrolled successfully."));
        assertThat(enrollmentPage.serviceType(), is(equalTo(ServiceType.CHILD_CARE.toString())));
        assertThat(enrollmentPage.medium(), is(equalTo(Medium.VOICE.toString())));
    }

    private MobileMidwifeEnrollmentPage goToMobileMidwifePage(HomePage homePage, TestPatient patient) {
        PatientPage patientPage = browser.openPatientPageBySearch(homePage, patient);
        return browser.toMobileMidwifeEnrollmentForm(patientPage);
    }

    private MobileMidwifeEnrollmentPage createPregnancyEnrollmentAndSubmit(MobileMidwifeEnrollmentPage enrollmentPage) {

        assertTrue(enrollmentPage.validate());
        enrollmentPage.withStaffMotechId(staff.motechId())
                .withFacilityMotechId(patient.facilityId()).withConsent(true)
                .withServiceType(ServiceType.PREGNANCY.toString())
                .withPhoneOwnership(PhoneOwnership.PERSONAL.toString())
                .withPhoneNumber("0967891000")
                .withMedium(Medium.SMS.toString())
                .withLanguage(Language.EN.toString())
                .withLearnedFrom(LearnedFrom.MOTECH_FIELD_AGENT.toString())
                .withReasonToJoin(ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH.toString())
                .withMessageStartWeek(MessageStartWeek.messageStartWeeks().get(2).getValue())
                .submit();
        return browser.toMobileMidwifeEnrollmentForm(enrollmentPage);
    }

    private TestPatient createPatient(PatientPage patientPage, String patientName) {
        TestPatient patient = TestPatient.with(patientName).
                registrationMode(TestPatient.PATIENT_REGN_MODE.AUTO_GENERATE_ID).
                patientType(TestPatient.PATIENT_TYPE.PATIENT_MOTHER).estimatedDateOfBirth(false);
        patientPage.create(patient);
        return patient.motechId(browser.toCreatePatientSuccess(patientPage).motechId());
    }

    private TestStaff createStaff(String firstName) {
        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        TestStaff staff = TestStaff.with(firstName);
        staffPage.create(staff);
        return staff;
    }
    
    private void createPatient(String patientName) {
        PatientPage patientPage = browser.toCreatePatient(homePage);
        patient = createPatient(patientPage, patientName + new DataGenerator().randomString(5));
    }
}
