package org.motechproject.ghana.national.functional.util;

import org.motechproject.ghana.national.functional.base.BaseWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ElementPoller {
    private ScreenShotCaptor screenShotCaptor = new ScreenShotCaptor();

    public boolean waitForElementID(final String elementId, final WebDriver driver) {
        return waitFor(driver, By.id(elementId));
    }

    public boolean waitForElementClassName(final String className, final WebDriver driver) {
        return waitFor(driver, By.className(className));
    }

    public boolean waitFor(WebDriver driver, final By by) {
        try {
            (new WebDriverWait(driver, BaseWebDriver.WAIT_TIME_OUTS_AFTER)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return driver.findElement(by).isDisplayed();
                }
            });
        } catch (Exception exception) {
            screenShotCaptor.capture(driver);
            return false;
        }
        return true;
    }

    public boolean waitForElementLinkText(final String linkText, final WebDriver driver) {
        return waitFor(driver, By.linkText(linkText));
    }
}
