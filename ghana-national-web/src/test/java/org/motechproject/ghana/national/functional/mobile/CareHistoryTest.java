package org.motechproject.ghana.national.functional.mobile;

import org.junit.runner.RunWith;
import org.motechproject.ghana.national.functional.data.TestCareHistory;
import org.motechproject.ghana.national.functional.data.TestPatient;
import org.motechproject.ghana.national.functional.data.TestStaff;
import org.motechproject.ghana.national.functional.framework.Mobile;
import org.motechproject.ghana.national.functional.mobileforms.MobileForm;
import org.motechproject.ghana.national.functional.pages.patient.PatientPage;
import org.motechproject.ghana.national.functional.pages.staff.StaffPage;
import org.motechproject.ghana.national.functional.util.DataGenerator;
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
        StaffPage staffPage = browser.toStaffCreatePage(homePage);
        staffPage.create(TestStaff.with("Staff First Name" + dataGenerator.randomString(5)));

        TestPatient patient = TestPatient.with("Patient First Name" + dataGenerator.randomString(5), staffPage.staffId()).
                patientType(TestPatient.PATIENT_TYPE.OTHER).estimatedDateOfBirth(false);

        PatientPage patientPage = browser.toCreatePatient(staffPage);
        patientPage.create(patient);
        patientPage.waitForSuccessfulCompletion();

        TestCareHistory careHistory = new TestCareHistory().staffId(staffPage.staffId()).motechId(patientPage.motechId());
        mobile.upload(MobileForm.careHistoryForm(), careHistory.forMobile());


    }
}
