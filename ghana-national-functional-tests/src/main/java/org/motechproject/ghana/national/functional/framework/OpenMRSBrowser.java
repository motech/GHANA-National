package org.motechproject.ghana.national.functional.framework;

import org.motechproject.ghana.national.functional.TestEnvironmentConfiguration;
import org.motechproject.ghana.national.functional.base.WebDriverProvider;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSHomePage;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSLoginPage;
import org.motechproject.ghana.national.functional.pages.openmrs.OpenMRSPatientPage;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSBrowser implements DisposableBean {
    protected WebDriver webDriver;
    protected ApplicationURLs applicationURLs;

    @Autowired
    public OpenMRSBrowser(WebDriverProvider webDriverProvider, TestEnvironmentConfiguration configuration) {
        applicationURLs = new ApplicationURLs(configuration);
        this.webDriver = webDriverProvider.getWebDriver();
    }

    public OpenMRSLoginPage toOpenMrsLoginPage() {
        webDriver.get(applicationURLs.forOpenMRSHomePage());
        return new OpenMRSLoginPage(webDriver);
    }

    public OpenMRSLoginPage toOpenMRSLogin() {
        webDriver.get(applicationURLs.forOpenMRSHomePage());
        return new OpenMRSLoginPage(webDriver);
    }

    public OpenMRSPatientPage toOpenMRSPatientPage(String openMrsId) {
        webDriver.get(applicationURLs.forOpenMRSPatientPage(openMrsId));
        return new OpenMRSPatientPage(webDriver);
    }

    public OpenMRSHomePage openMRSHomePage() {
        return new OpenMRSHomePage(webDriver);
    }

    public void quit() {
        webDriver.quit();
    }

    @Override
    public void destroy() throws Exception {
        quit();
    }
}
