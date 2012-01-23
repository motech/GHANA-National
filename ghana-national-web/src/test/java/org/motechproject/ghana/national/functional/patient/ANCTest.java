package org.motechproject.ghana.national.functional.patient;

import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.motechproject.functional.data.TestANCEnrollment;
import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.data.TestStaff;
import org.motechproject.functional.framework.XformHttpClient;
import org.motechproject.functional.mobileforms.MobileForm;
import org.motechproject.functional.pages.patient.ANCEnrollmentPage;
import org.motechproject.functional.pages.patient.PatientEditPage;
import org.motechproject.functional.pages.patient.PatientPage;
import org.motechproject.functional.pages.patient.SearchPatientPage;
import org.motechproject.functional.pages.staff.StaffPage;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.util.DateUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class ANCTest extends LoggedInUserFunctionalTest {
    private PatientPage patientPage;
    private DataGenerator dataGenerator;

    @BeforeMethod
    public void setUp() {
        dataGenerator = new DataGenerator();
    }

    @Test
    public void shouldEnrollForANCForAPatientAndUpdate() {
        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        staffPage.create(TestStaff.with("First Name" + dataGenerator.randomString(5)));
        String staffId = staffPage.staffId();
        String patientFirstName = "First Name" + dataGenerator.randomString(5);
        TestPatient testPatient = TestPatient.with(patientFirstName).
                registrationMode(TestPatient.PATIENT_REGN_MODE.AUTO_GENERATE_ID).
                patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER).estimatedDateOfBirth(false);

        patientPage = browser.toCreatePatient(staffPage);
        patientPage.create(testPatient);

        ANCEnrollmentPage ancEnrollmentPage = browser.toANCEnrollmentForm(patientPage);

        String country = "Ghana";
        String serialNumber = "trew654gf";
        String subDistrict = "Awutu";
        String district = "Awutu Senya";
        String region = "Central Region";
        LocalDate registrationDate = DateUtil.newDate(2011, 11, 30);
        boolean addHistory = true;
        String facility = "Awutu HC";
        double height = 123.4;
        LocalDate estimatedDeliveryDate = DateUtil.newDate(2011, 11, 30);
        boolean deliveryDateConfirmed = true;
        String parity = "4";
        String gravida = "3";
        boolean iptCareHistory = true;
        boolean ttCareHistory = true;
        boolean lastTPTValue1 = true;
        boolean lastTTValue1 = true;
        LocalDate ttDate = DateUtil.newDate(2011, 10, 10);
        LocalDate iptDate = DateUtil.newDate(2011, 10, 10);


        String updatedGravida = "5";
        double updatedHeight = 134.2;


        ancEnrollmentPage.withStaffId(staffId).withRegistrationToday(RegistrationToday.IN_PAST.toString()).withSerialNumber(serialNumber)
                .withCountry(country).withRegion(region).withDistrict(district).withSubDistrict(subDistrict)
                .withRegistrationDate(registrationDate).withAddHistory(addHistory).withFacility(facility).withHeight(height)
                .withGravida(gravida).withParity(parity).withDeliveryDateConfirmed(deliveryDateConfirmed).withEstimatedDateOfDelivery(estimatedDeliveryDate)
                .withIPT(iptCareHistory).withTT(ttCareHistory).withLastIPTValue1(lastTPTValue1).withLastTTValue1(lastTTValue1).
                withIPTDate(iptDate).withTTDate(ttDate).submit();

        assertTrue(ancEnrollmentPage.getDriver().findElement(By.className("success")).getText().equals("Updated successfully."));

        PatientEditPage patientEditPage = searchPatient(patientFirstName, testPatient, ancEnrollmentPage);
        ancEnrollmentPage = browser.toANCEnrollmentForm(patientEditPage);

        assertEquals(facility, ancEnrollmentPage.getFacilities());
        assertEquals(height, Double.parseDouble(ancEnrollmentPage.getHeight()));
        assertTrue(ancEnrollmentPage.getTtCareHistory());
        assertEquals("10/10/2011", ancEnrollmentPage.getLastTTDate());

        ancEnrollmentPage.withFacility(facility).withGravida(updatedGravida).withHeight(updatedHeight)
                .withTT(true).submit();

        final WebElement success = ancEnrollmentPage.getDriver().findElement(By.className("success"));
        assertTrue(success.isDisplayed());
        assertTrue(success.getText().equals("Updated successfully."));

        patientEditPage = searchPatient(patientFirstName, testPatient, ancEnrollmentPage);
        ancEnrollmentPage = browser.toANCEnrollmentForm(patientEditPage);
        assertEquals(updatedGravida, ancEnrollmentPage.getGravida());
        assertEquals(updatedHeight, Double.parseDouble(ancEnrollmentPage.getHeight()));
        assertFalse(ancEnrollmentPage.getTtCareHistory());
        assertFalse(ancEnrollmentPage.getDriver().findElement(By.id("lastTTDate")).isDisplayed());
    }

    @Test
    public void shouldCreateANCForAPatientWithMobileDeviceAndSearchForItInWeb() {
        DataGenerator dataGenerator = new DataGenerator();
        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        staffPage.create(TestStaff.with("First Name" + dataGenerator.randomString(5)));

        String staffId = staffPage.staffId();
        String patientFirstName = "First Name" + dataGenerator.randomString(5);
        TestPatient testPatient = TestPatient.with(patientFirstName).
                registrationMode(TestPatient.PATIENT_REGN_MODE.AUTO_GENERATE_ID).
                patientType(TestPatient.PATIENT_TYPE.PREGNANT_MOTHER).estimatedDateOfBirth(false);

        patientPage = browser.toCreatePatient(staffPage);
        patientPage.create(testPatient);

        final TestANCEnrollment testANCEnrollment = new TestANCEnrollment().with(patientPage.motechId(), staffId, "13212");
        XformHttpClient.XformResponse response = mobile.upload(MobileForm.registerANCForm(), testANCEnrollment.forMobile());

        assertEquals(1, response.getSuccessCount());
        SearchPatientPage searchPatientPage = browser.toSearchPatient();
        searchPatientPage.searchWithName(testPatient.firstName());
        searchPatientPage.displaying(testPatient);

        PatientEditPage patientEditPage = browser.toPatientEditPage(searchPatientPage, testPatient);
        ANCEnrollmentPage ancEnrollmentPage = browser.toANCEnrollmentForm(patientEditPage);
        assertEquals(testANCEnrollment.motechPatientId(), ancEnrollmentPage.motechId());
    }

    private PatientEditPage searchPatient(String patientFirstName, TestPatient testPatient, ANCEnrollmentPage ancEnrollmentPage) {
        SearchPatientPage searchPatientPage = browser.toSearchPatient(ancEnrollmentPage);

        searchPatientPage.searchWithName(patientFirstName);
        searchPatientPage.displaying(testPatient);

        return browser.toPatientEditPage(searchPatientPage, testPatient);
    }
}
