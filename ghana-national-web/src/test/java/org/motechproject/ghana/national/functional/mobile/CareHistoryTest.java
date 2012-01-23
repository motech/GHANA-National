package org.motechproject.ghana.national.functional.mobile;

import org.junit.runner.RunWith;
import org.motechproject.functional.data.TestCareHistory;
import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.data.TestStaff;
import org.motechproject.functional.framework.Mobile;
import org.motechproject.functional.mobileforms.MobileForm;
import org.motechproject.functional.pages.patient.PatientPage;
import org.motechproject.functional.pages.staff.StaffPage;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.ghana.national.functional.LoggedInUserFunctionalTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.annotations.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class CareHistoryTest extends LoggedInUserFunctionalTest{
    private DataGenerator dataGenerator;

    public CareHistoryTest() {
        dataGenerator = new DataGenerator();
    }

    @Autowired
    protected Mobile mobile;

    @Test(enabled = false)
    public void shouldAddCareHistoryDetailsOfAPatientFromMobilePhone(){

        DataGenerator dataGenerator = new DataGenerator();

        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        staffPage.create(TestStaff.with("Staff First Name" + dataGenerator.randomString(5)));

        TestPatient patient = TestPatient.with("Patient First Name" + dataGenerator.randomString(5)).
                registrationMode(TestPatient.PATIENT_REGN_MODE.AUTO_GENERATE_ID).
                patientType(TestPatient.PATIENT_TYPE.OTHER).estimatedDateOfBirth(false).
                staffId(staffPage.staffId());

        PatientPage patientPage = browser.toCreatePatient(staffPage);
        patientPage.create(patient);
        patientPage.waitForSuccessfulCompletion();

        TestCareHistory careHistory = new TestCareHistory().staffId(staffPage.staffId()).motechId(patientPage.motechId());
        mobile.upload(MobileForm.careHistoryForm(), careHistory.forMobile());


    }
}
