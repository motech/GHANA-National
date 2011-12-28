package org.motechproject.functional.pages.patient;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.BasePage;
import org.motechproject.functional.pages.home.HomePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

@Component
public class SearchPatientPage extends BasePage {

    @FindBy(id = "motechId")
    @CacheLookup
    WebElement motechId;

    @FindBy(id = "name")
    @CacheLookup
    WebElement firstName;

    @FindBy(id = "searchPatient")
    @CacheLookup
    WebElement search;

    @Autowired
    private HomePage homePage;
    private String searchPatientElementId = "searchPatient";
    private String searchResultElementID = "patientsReturnedBySearch";
    private String searchResultTableId = "searchResultTable";

    @Autowired
    public SearchPatientPage(WebDriverProvider webDriverProvider) {
        super(webDriverProvider.getWebDriver());
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
        elementPoller.waitForElementID(searchPatientElementId, driver);
        initializePageElements();
    }

    public void initializePageElements() {
        PageFactory.initElements(driver, this);
    }

    public void waitForSearchResultsToLoad() {
        elementPoller.waitForElementID(searchResultElementID, driver);
    }

    public void open() {
        homePage.openSearchPatientPage();
        waitForPageToLoad();
    }

    public void search() {
        search.click();
        waitForSearchResultsToLoad();
    }

    public void assertIfSearchReturned(String motechId, String firstName, String middleName, String lastName, String gender, String dateOfBirth) {
        assertTrue(htmlTableParser.hasRow(driver, searchResultTableId, mapTableRowDataWithColumns(motechId, firstName, middleName, lastName, gender, dateOfBirth)));
    }

    public void assertIfSearchReturned(String firstName, String middleName, String lastName, String gender, String dateOfBirth) {
        assertTrue(htmlTableParser.hasRow(driver, searchResultTableId, mapTableRowDataWithColumns(firstName, middleName, lastName, gender, dateOfBirth)));
    }

    private Map<String, String> mapTableRowDataWithColumns(final String motechId, final String firstName, final String middleName, final String lastName, final String gender, final String dateOfBirth) {
        return new HashMap<String, String>(){{put("Patient ID", motechId); put("First Name", firstName); put("Middle Name", middleName); put("Last Name", lastName); put("Date of Birth", dateOfBirth); put("Sex", gender);}};  //To change body of created methods use File | Settings | File Templates.
    }

    private Map<String, String> mapTableRowDataWithColumns(final String firstName, final String middleName, final String lastName, final String gender, final String dateOfBirth) {
        return new HashMap<String, String>(){{put("First Name", firstName); put("Middle Name", middleName); put("Last Name", lastName); put("Date of Birth", dateOfBirth); put("Sex", gender);}};  //To change body of created methods use File | Settings | File Templates.
    }
}
