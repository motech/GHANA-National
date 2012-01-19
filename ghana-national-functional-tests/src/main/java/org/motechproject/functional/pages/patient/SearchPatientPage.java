package org.motechproject.functional.pages.patient;

import org.motechproject.functional.data.TestPatient;
import org.motechproject.functional.pages.home.HomePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

public class SearchPatientPage extends HomePage {
    @FindBy(id = "motechId")
    @CacheLookup
    WebElement motechId;

    @FindBy(id = "name")
    @CacheLookup
    WebElement firstName;

    @FindBy(id = "searchPatient")
    @CacheLookup
    WebElement search;

    private String searchResultTableId = "searchResultTable";

    @Autowired
    public SearchPatientPage(WebDriver webDriver) {
        super(webDriver);
        openSearchPatientPage();
        waitForPageToLoad();
    }

    public SearchPatientPage withName(String fullName) {
        this.firstName.clear();
        firstName.sendKeys(fullName);
        return this;
    }

    public SearchPatientPage withMotechId(String motechId) {
        this.motechId.clear();
        this.motechId.sendKeys(motechId);
        return this;
    }

    public void waitForPageToLoad() {
        elementPoller.waitForElementID("searchPatient", driver);
        initializePageElements();
    }

    public void initializePageElements() {
        PageFactory.initElements(driver, this);
    }

    public void waitForSearchResultsToLoad() {
        elementPoller.waitForElementID("patientsReturnedBySearch", driver);
    }

    public void searchWithName(String name) {
        withName(name);
        search();
    }

    public void searchWithMotechId(String id) {
        withMotechId(id);
        search();
    }

    private void search() {
        search.click();
        waitForSearchResultsToLoad();
        initializePageElements();
    }

    public void displaying(TestPatient patient) {
        Map<String, String> map = mapTableRowDataWithColumns(patient.firstName(), patient.middleName(), patient.lastName(),
                patient.genderCode(), patient.dateOfBirth().toString("dd-MM-YYYY"));
        assertTrue(map.toString(), htmlTableParser.hasRow(driver, searchResultTableId, map));
    }

    public void clickEditLink(TestPatient patient) {
        htmlTableParser.clickEditLink(driver, searchResultTableId, mapTableRowDataWithColumns(
                patient.firstName(), patient.middleName(), patient.lastName(), patient.genderCode(), patient.dateOfBirth().toString("dd-MM-YYYY")));
    }

    private Map<String, String> mapTableRowDataWithColumns(final String firstName, final String middleName, final String lastName, final String gender, final String dateOfBirth) {
        return new HashMap<String, String>() {{
            put("First Name", firstName);
            put("Middle Name", middleName);
            put("Last Name", lastName);
            put("Date of Birth", dateOfBirth);
            put("Sex", gender);
        }};
    }

    public void open(TestPatient patient) {
        WebElement rowElement = htmlTableParser.getRow(driver, searchResultTableId, mapTableRowDataWithColumns(patient.firstName(), patient.middleName(), patient.lastName(),
                patient.genderCode(), patient.dateOfBirth().toString("dd-MM-YYYY")));
        List<WebElement> elementList = (rowElement).findElements(new By.ByCssSelector(".action a"));
        elementList.get(0).click();
    }
}
