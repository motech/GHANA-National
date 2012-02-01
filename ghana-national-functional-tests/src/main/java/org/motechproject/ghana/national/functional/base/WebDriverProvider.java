package org.motechproject.ghana.national.functional.base;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class WebDriverProvider {

    private static final String BROWSER = "browser";
    private static final String FIREFOX = "fireFoxWebDriver";

    private WebDriver driver;

    @Autowired
    public WebDriverProvider(Map<String, BaseWebDriver> webDrivers) {
        driver = webDrivers.get(System.getProperty(BROWSER, FIREFOX)).getDriver();
        driver.manage().timeouts().implicitlyWait(BaseWebDriver.IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
    }

    public WebDriver getWebDriver() {
        return driver;
    }
}
