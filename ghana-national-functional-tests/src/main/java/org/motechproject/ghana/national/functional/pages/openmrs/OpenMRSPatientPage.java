package org.motechproject.ghana.national.functional.pages.openmrs;

import org.motechproject.ghana.national.functional.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class OpenMRSPatientPage extends BasePage<OpenMRSPatientPage> {
    @FindBy(id = "patientEncountersTab")
    @CacheLookup
    private WebElement encounterTab;

    public OpenMRSPatientPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitFor(webDriver, By.id("patientEncountersTab"));
        PageFactory.initElements(webDriver, this);
    }

    public void encounterTab() {
        encounterTab.click();
    }
}
