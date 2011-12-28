package org.motechproject.functional.util;

import org.apache.commons.io.FileUtils;
import org.motechproject.MotechException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ScreenShotCaptor {
    public void capture(WebDriver driver, String namePrefix) {
        File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenShot, new File(namePrefix + "_failed.bmp"));
        } catch (IOException e) {
            throw new MotechException("Error encountered while capturing screen shot of failed functional test", e);
        }
    }
}
