package org.motechproject.ghana.national.functional.pages.openmrs;

import org.motechproject.ghana.national.functional.pages.BasePage;
import org.motechproject.ghana.national.functional.pages.openmrs.vo.OpenMRSObservationVO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.HashMap;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;

public class OpenMRSEncounterPage extends BasePage<OpenMRSEncounterPage> {
    public static final String PATIENT_OBSERVATION_TABLE = "obs";
    @FindBy(id = "obs")
    @CacheLookup
    private WebElement observationTable;

    public OpenMRSEncounterPage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitFor(webDriver, By.id("obs"));
        PageFactory.initElements(webDriver, this);
    }


    public void displaying(final List<OpenMRSObservationVO> openMRSObservations) {
        for(final OpenMRSObservationVO observation : openMRSObservations){
            assertTrue("Observation is not present: " + observation.getObservationName() + " with Value: " + observation.getValue(),
                    htmlTableParser.hasRow(driver, PATIENT_OBSERVATION_TABLE, new HashMap<String, String>() {{
                        put("Question Concept", observation.getObservationName());
                        put("Value", observation.getValue());
                }}));
        }
    }
}
