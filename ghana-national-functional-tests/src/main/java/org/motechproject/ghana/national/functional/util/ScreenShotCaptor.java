package org.motechproject.ghana.national.functional.util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.motechproject.MotechException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

@Component
public class ScreenShotCaptor {
    private static ThreadLocal<String> testIdentification = new ThreadLocal<String>();
    private Logger logger = Logger.getLogger(this.getClass());

    public static void setupFor(Method method) {
        testIdentification.set(String.format("%s_%s", method.getDeclaringClass().getSimpleName(), method.getName()));
    }

    public void capture(WebDriver driver) {
        File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            File destFile = new File(String.format("%s_failed.bmp", testIdentification.get()));
            FileUtils.copyFile(screenShot, destFile);
            System.out.println(String.format("Copying screenshot to %s", destFile.getAbsolutePath()));
        } catch (IOException e) {
            throw new MotechException("Error encountered while capturing screen shot of failed functional test", e);
        }
    }
}
