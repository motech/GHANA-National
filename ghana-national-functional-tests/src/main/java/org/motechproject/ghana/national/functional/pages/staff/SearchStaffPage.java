package org.motechproject.ghana.national.functional.pages.staff;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

public class SearchStaffPage extends StaffPageElements {
    @FindBy(id = "searchStaff")
    @CacheLookup
    WebElement search;
    public final String searchResultTable = "searchResultTable";

    public SearchStaffPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitForElementID("searchStaff", driver);
        PageFactory.initElements(driver, this);
    }

    public void waitForSearchResultsToLoad() {
        elementPoller.waitForElementID("staffsReturnedBySearch", driver);
    }

    public void search() {
        search.click();
        waitForSearchResultsToLoad();
    }

    public void clickEditLink(String firstName, String middleName, String lastName, String phoneNumber, String role) {
        htmlTableParser.clickEditLink(driver, searchResultTable, mapTableRowDataWithColumns(firstName, middleName, lastName, phoneNumber, role));
    }

    public void assertIfSearchReturned(String firstName, String middleName, String lastName, String phoneNumber, String role) {
        assertTrue(htmlTableParser.hasRow(driver, searchResultTable, mapTableRowDataWithColumns(firstName, middleName, lastName, phoneNumber, role)));
    }

    private HashMap<String, String> mapTableRowDataWithColumns(final String firstName, final String middleName, final String lastName, final String phoneNumber, final String role) {
        return new HashMap<String, String>() {{
            put("First Name", firstName);
            put("Middle Name", middleName);
            put("Last Name", lastName);
            put("Phone Number", phoneNumber);
            put("Role", role);
        }};
    }
}
