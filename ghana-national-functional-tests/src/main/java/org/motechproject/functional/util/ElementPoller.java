package org.motechproject.functional.util;

import org.motechproject.functional.base.BaseWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElementPoller {

    @Autowired
    private ScreenShotCaptor screenShotCaptor;

    public boolean waitForElementID(final String elementId, final WebDriver driver) {
        try {
            (new WebDriverWait(driver, BaseWebDriver.WAIT_TIME_OUTS_AFTER)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return driver.findElement(By.id(elementId)).isDisplayed();
                }
            });
        } catch (Exception exception) {
            screenShotCaptor.capture(driver, elementId);
            return false;
        }
        return true;
    }

    public boolean waitForElementClassName(final String className, final WebDriver driver) {
        try {
            (new WebDriverWait(driver, BaseWebDriver.WAIT_TIME_OUTS_AFTER)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return driver.findElement(By.className(className)).isDisplayed();
                }
            });
        } catch (Exception exception) {
            screenShotCaptor.capture(driver, className);
            return false;
        }
        return true;
    }

    public boolean waitForElementLinkText(final String linkText, final WebDriver driver) {
        try {
            (new WebDriverWait(driver, BaseWebDriver.WAIT_TIME_OUTS_AFTER)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return driver.findElement(By.linkText(linkText)).isDisplayed();
                }
            });
        } catch (Exception e) {
            screenShotCaptor.capture(driver, linkText);
            return false;
        }
        return true;
    }
}
