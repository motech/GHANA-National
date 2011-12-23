package org.motechproject.functional.pages;

import org.motechproject.functional.base.WebDriverProvider;
import org.motechproject.functional.util.DataGenerator;
import org.motechproject.functional.util.JavascriptExecutor;
import org.motechproject.functional.util.Utilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SearchPatientPage {

    @FindBy(id = "motechId")
    @CacheLookup
    WebElement motechId;

    @FindBy(id = "name")
    @CacheLookup
    WebElement name;

    @FindBy(id = "searchPatient")
    @CacheLookup
    WebElement searchPatient;


    @Autowired
    WebDriverProvider webDriverProvider;

    @Autowired
    DataGenerator dataGenerator;

    @Autowired
    JavascriptExecutor javascriptExecutor;

    @Autowired
    Utilities myUtilities;

    private WebDriver driver;

    public SearchPatientPage withName(String fullName){
        name.sendKeys(fullName);
        return this;
    }

    public SearchPatientPage withMotechId(String motechId) {
        this.motechId.sendKeys(motechId);
        return this;
    }

    @Autowired
    public SearchPatientPage(WebDriverProvider webDriverProvider) {
        this.driver = webDriverProvider.getWebDriver();
    }

    public void search() {
        searchPatient.click();
        webDriverProvider.waitForElementID("footer");
    }


    public void open() {
        WebElement PatientParentLink = driver.findElement(By.linkText("Patient"));
        if (System.getProperty("os.name").contains("Wind")) {
            javascriptExecutor.clickOnLink("searchpatient", driver);
        } else {
            WebElement temp = javascriptExecutor.getElementById("searchpatient", driver);
            PatientParentLink.click();
            webDriverProvider.waitForElementID("searchpatient");
            temp.click();
        }
        webDriverProvider.waitForElementID("searchPatient");
        PageFactory.initElements(driver, this);
    }

    public boolean checkIfSearchReturned(String motechId, String fName, String mName, String lName, String gender, String dateOfBirth) {
        String source = driver.getPageSource();
        return source.contains(motechId) && source.contains(mName) && source.contains(lName) && source.contains(gender) && source.contains(dateOfBirth);
    }
}
