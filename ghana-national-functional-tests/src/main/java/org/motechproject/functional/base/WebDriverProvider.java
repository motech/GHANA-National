package org.motechproject.functional.base;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
public class WebDriverProvider  {

    private static final String BROWSER = "browser";
    private static final String FIREFOX = "fireFoxWebDriver";


    private WebDriver driver;

    @Autowired
    public WebDriverProvider(Map<String, BaseWebDriver> webDrivers) {
        driver = webDrivers.get(System.getProperty(BROWSER, FIREFOX)).getDriver();
    }

    public WebDriver getWebDriver() {
        return driver;
    }


    public boolean WaitForElement_ID(final String elementid) {
         try {

            (new WebDriverWait(driver, 15)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return driver.findElement(By.id(elementid)).isDisplayed();
                }
            });
        } catch (Exception ignored) {
             File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshot, new File(elementid + "_failed.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        return true;
    }

    public boolean WaitForElement_Class(final String elementClass) {
               try {

            (new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return driver.findElement(By.className(elementClass)).isDisplayed();
                }
            });
        } catch (Exception ignored) {
             File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshot, new File(elementClass+ "_failedClass.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        return true;
    }
}
