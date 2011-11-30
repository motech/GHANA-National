package org.motechproject.functional.base;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebDriverProvider {

    private static final String BROWSER = "browser";
    private static final String FIREFOX = "fireFoxWebDriver";

    private Map<String, BaseWebDriver> webDrivers;

    @Autowired
    public WebDriverProvider(Map<String, BaseWebDriver> webDrivers) {
        this.webDrivers = webDrivers;
    }

    public WebDriver getWebDriver() {
        return webDrivers.get(System.getProperty(BROWSER, FIREFOX)).getDriver();
    }
}