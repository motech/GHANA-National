package org.motechproject.ghana.national.functional.pages.openmrs;

import org.motechproject.ghana.national.functional.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenMRSPatientPage extends BasePage<OpenMRSPatientPage> {
    public static final String PATIENT_ENCOUNTERS_TABLE = "patientEncountersTable";
    @FindBy(id = "patientEncountersTab")
    @CacheLookup
    private WebElement encounterTab;

    public OpenMRSPatientPage(WebDriver webDriver) {
        super(webDriver);
        setHtmlTableParser(new OpenMRSHtmlTableParser());
        elementPoller.waitFor(webDriver, By.id("patientEncountersTab"));
        PageFactory.initElements(webDriver, this);
    }

    public void encounterTab() {
        encounterTab.click();
    }

    public String chooseEncounter(final String encounterType) {
        encounterTab();
        WebElement encounterRow = htmlTableParser.getRow(driver, PATIENT_ENCOUNTERS_TABLE, new HashMap<String, String>() {{
            put("Encounter Type", encounterType);
        }});
        
        String link = encounterRow.findElement(By.className("encounterEdit")).findElement(By.tagName("a")).getAttribute("href");
        Matcher matcher = Pattern.compile("^(.*encounterId=(.*)?)$").matcher(link);
        return matcher.matches() ? matcher.group(2) : null;
    }
}
