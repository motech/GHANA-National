package org.motechproject.functional.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@Component
public class JavascriptExecutor {
    public WebElement getElementById(String id, WebDriver driver){
        return (WebElement) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return document.getElementById(arguments[0]);", id);
    }
}
