package org.motechproject.functional.base;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;

public class MyDriver {
    static WebDriver driver;
    private static ChromeDriverService service;

    public static String URL = "http://10.10.13.52:8080/ghana-national-web/admin";
    static Logger logger = Logger.getLogger(MyDriver.class);
    //public static String URL = "http://localhost:8123/ghana-national/admin";

    public static WebDriver getDriverInstance() {
        if (driver == null) {
            FirefoxBinary firefoxBinary = new FirefoxBinary(new File("H:\\firefox7\\firefox.exe"));
            driver = new FirefoxDriver(firefoxBinary, new FirefoxProfile());
        }
        return driver;
    }

    public static void open(String pageurl) {
        driver.navigate().to(pageurl);
    }
}
