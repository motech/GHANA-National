package org.motechproject.functional.util;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component
public class JavascriptExecutor {


    public void clickOnLink(String id,WebDriver driver) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("document.getElementById(arguments[0]).click();", id);
    }
}
