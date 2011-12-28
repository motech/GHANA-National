package org.motechproject.functional.pages;

import org.motechproject.functional.util.*;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

public class BasePage {
    @Autowired
    protected JavascriptExecutor javascriptExecutor;

    @Autowired
    protected PlatformSpecificExecutor platformSpecificExecutor;

    @Autowired
    protected HtmlTableParser htmlTableParser;

    @Autowired
    private ScreenShotCaptor screenShotCaptor;

    @Autowired
    protected DateSelector dateSelector;

    @Autowired
    protected ElementPoller elementPoller;

    protected WebDriver driver;

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver(){
        return driver;
    }
}
