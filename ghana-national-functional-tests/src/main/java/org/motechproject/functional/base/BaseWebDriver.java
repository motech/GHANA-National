package org.motechproject.functional.base;

import org.openqa.selenium.WebDriver;

public abstract class BaseWebDriver {
    public static String URL = "http://localhost:8080/ghana-national-web/admin";

    private WebDriver driver;

    public abstract WebDriver getDriver();

    public void open(String pageurl) {
        driver.navigate().to(pageurl);
    }
}
