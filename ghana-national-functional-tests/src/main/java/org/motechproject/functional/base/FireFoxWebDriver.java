package org.motechproject.functional.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FireFoxWebDriver extends BaseWebDriver {

    @Value("#{functionalTestProperties['firefox.loc']}")
    private String firefoxLocation;

    public WebDriver getDriver() {
        FirefoxBinary firefoxBinary = new FirefoxBinary(new File(firefoxLocation));
        return new FirefoxDriver(firefoxBinary, new FirefoxProfile());
    }
}
