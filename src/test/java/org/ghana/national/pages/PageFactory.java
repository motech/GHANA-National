package org.ghana.national.pages;

import org.jbehave.web.selenium.WebDriverProvider;

public class PageFactory {
    private final WebDriverProvider webDriverProvider;


    public PageFactory(WebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

}
