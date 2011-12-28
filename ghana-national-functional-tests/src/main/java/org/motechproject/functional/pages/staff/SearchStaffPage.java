package org.motechproject.functional.pages.staff;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.pages.home.HomePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

@Component
public class SearchStaffPage extends StaffPageElements{

    private String searchStaffElementId = "searchStaff";
    private String searchResultElementID = "staffsReturnedBySearch";
    private String searchResultTableId = "searchResultTable";

    @FindBy(id = "searchStaff")
    @CacheLookup
    WebElement search;

    @Autowired
    private HomePage homePage;

    @Autowired
    private StaffPage staffPage;

    @Autowired
    public SearchStaffPage(WebDriverProvider webDriverProvider) {
        super(webDriverProvider.getWebDriver());
    }

    public void waitForPageToLoad(){
        elementPoller.waitForElementID(searchStaffElementId, driver);
        PageFactory.initElements(driver, this);
    }

    public void waitForSearchResultsToLoad(){
        elementPoller.waitForElementID(searchResultElementID, driver);
    }

    public void open() {
        homePage.openSearchStaffPage();
        waitForPageToLoad();
    }

    public void search() {
        search.click();
        waitForSearchResultsToLoad();
    }

    public void clickEditLink(String firstName, String middleName, String lastName, String phoneNumber, String role) {
        htmlTableParser.clickEditLink(driver, searchResultTableId, mapTableRowDataWithColumns(firstName, middleName, lastName, phoneNumber, role));
        staffPage.waitForPageToLoad();
    }

    public void assertIfSearchReturned(String firstName, String middleName, String lastName, String phoneNumber, String role) {
        assertTrue(htmlTableParser.hasRow(driver, searchResultTableId, mapTableRowDataWithColumns(firstName, middleName, lastName, phoneNumber, role)));
    }

    private HashMap<String, String> mapTableRowDataWithColumns(final String firstName, final String middleName, final String lastName, final String phoneNumber, final String role) {
        return new HashMap<String, String>(){{put("First Name", firstName); put("Middle Name", middleName); put("Last Name", lastName); put("Phone Number", phoneNumber); put("Role", role);}};
    }
}
