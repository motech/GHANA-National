package org.motechproject.functional.base;

import org.openqa.selenium.WebDriver;

public abstract class BaseWebDriver {
    public static int IMPLICIT_WAIT_TIME = 2;

    public static int WAIT_TIME_OUTS_AFTER = 15;

    protected WebDriver driver;

    public abstract WebDriver getDriver();
}
