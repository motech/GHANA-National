package org.motechproject.ghana.national.functional.pages.openmrs;

import org.motechproject.ghana.national.functional.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

public class OpenMRSSearchPage extends BasePage<OpenMRSSearchPage> {
    @FindBy(id = "inputNode")
    @CacheLookup
    private WebElement inputNode;

    public OpenMRSSearchPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void search(String patientMotechId) {
        enter(this.inputNode, patientMotechId);
    }
}
