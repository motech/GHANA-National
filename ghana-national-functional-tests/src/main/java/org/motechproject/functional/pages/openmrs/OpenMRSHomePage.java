package org.motechproject.functional.pages.openmrs;

import org.motechproject.functional.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OpenMRSHomePage extends BasePage {
    public OpenMRSHomePage(WebDriver webDriver) {
        super(webDriver);
        elementPoller.waitFor(webDriver, By.id("userLogout"));
    }
}
