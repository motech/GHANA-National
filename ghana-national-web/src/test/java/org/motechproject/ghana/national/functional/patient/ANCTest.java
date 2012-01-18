package org.motechproject.ghana.national.functional.patient;

import org.junit.runner.RunWith;
import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.data.TestStaff;
import org.motechproject.functional.pages.patient.ANCEnrollmentPage;
import org.motechproject.functional.pages.patient.PatientPage;
import org.motechproject.functional.pages.staff.StaffPage;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.motechproject.util.DateUtil;
import org.openqa.selenium.By;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

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
    public void shouldEnrollForANCForAPatient() {
        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        staffPage.create(TestStaff.with("First Name" + dataGenerator.randomString(5)));
        String staffId = staffPage.staffId();

        patientPage = browser.toCreatePatient(staffPage);
        patientPage.create(TestPatient.with("First Name" + dataGenerator.randomString(5)).
                registrationMode(TestPatient.PATIENT_REGN_MODE.AUTO_GENERATE_ID).
                patientType(TestPatient.PATIENT_TYPE.PATIENT_MOTHER).estimatedDateOfBirth(false));

        ANCEnrollmentPage ancEnrollmentPage = browser.toANCEnrollmentForm(patientPage);

        ancEnrollmentPage.withStaffId(staffId).withRegistrationToday(RegistrationToday.IN_PAST.toString()).withSerialNumber("trew654gf")
                .withCountry("Ghana").withRegion("Central Region").withDistrict("Awutu Senya").withSubDistrict("Awutu")
                .withRegistrationDate(DateUtil.newDate(2011, 11, 30)).withAddHistory(true).withFacility("Awutu HC").withHeight(123.4)
                .withGravida("3").withParity("4").withDeliveryDateConfirmed(true).withEstimatedDateOfDelivery(DateUtil.newDate(2011, 11, 30))
                .withIPT(true).withTT(true).withLastIPTValue1(true).withLastTTValue1(true).
                withIPTDate(DateUtil.newDate(2011, 10, 10)).withTTDate(DateUtil.newDate(2011, 10, 10)).submit();

        assertTrue(ancEnrollmentPage.getDriver().findElement(By.className("success")).getText().equals("Updated successfully."));
    }
}
