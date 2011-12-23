package org.motechproject.ghana.national.functional;

import org.junit.runner.RunWith;
import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.CreatePatientPage;
import org.motechproject.functional.pages.SearchPatientPage;
import org.motechproject.ghana.national.functional.helper.CreatePatientHelper;
import org.motechproject.ghana.national.functional.helper.IdGenHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Calendar;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-functional-tests.xml"})
public class SearchPatientTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebDriverProvider driverProvider;

    @Autowired
    private CreatePatientHelper createPatientHelper;

    protected WebDriver driver;

    @Autowired
    private SearchPatientPage searchPatientPage;

    @Autowired
    private IdGenHelper idGenHelper;

    @BeforeMethod
    public void setUp() {
        driver = driverProvider.getWebDriver();
    }

    @Test
    public void searchAnExistingPatient() {
        String fName = "Ayyappan";
        String mName = "from";
        String lName = "Africa";
        boolean gender = true;
        Calendar dateOfBirth = Calendar.getInstance();
        dateOfBirth.set(2011, 11, 20);
        CreatePatientPage.PATIENT_TYPE patientType = CreatePatientPage.PATIENT_TYPE.CHILD_UNDER_FIVE;
        String motechId = idGenHelper.generatePrePrintedPatientId();

        createPatientHelper.createPatient(fName, mName, lName, gender, dateOfBirth, patientType, motechId);
        searchPatientPage.open();
        searchPatientPage.withName(fName).withMotechId(motechId).search();
        assertTrue(searchPatientPage.checkIfSearchReturned(motechId, fName, mName, lName, "Male", "2011-11-20"));

        PageFactory.initElements(driver, searchPatientPage);
        searchPatientPage.withMotechId(motechId).search();
        assertTrue(searchPatientPage.checkIfSearchReturned(motechId, fName, mName, lName, "Male", "2011-11-20"));

        PageFactory.initElements(driver, searchPatientPage);
        searchPatientPage.withName(lName).search();
        assertTrue(searchPatientPage.checkIfSearchReturned(motechId, fName, mName, lName, "Male", "2011-11-20"));

        PageFactory.initElements(driver, searchPatientPage);
        searchPatientPage.withName(mName).search();
        assertTrue(searchPatientPage.checkIfSearchReturned(motechId, fName, mName, lName, "Male", "2011-11-20"));

        PageFactory.initElements(driver, searchPatientPage);
        searchPatientPage.withName("Ayyap").search();
        assertTrue(searchPatientPage.checkIfSearchReturned(motechId, fName, mName, lName, "Male", "2011-11-20"));
    }

}
