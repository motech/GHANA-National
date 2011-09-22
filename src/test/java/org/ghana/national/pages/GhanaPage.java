package org.ghana.national.pages;

import org.jbehave.web.selenium.WebDriverPage;
import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static java.lang.String.format;

public class
        GhanaPage extends WebDriverPage {
    private String page;

    public GhanaPage(WebDriverProvider driverProvider, String page) {
        super(driverProvider);
        this.page = page;
    }

    protected String url() {
        return url(page);
    }

    protected String url(String page) {
        return format("%s/%s", "http://localhost:8080/GHANA-National-1.0-SNAPSHOT", page);
    }

    public void go() {
        get(url());
    }

    protected void go(String page) {
        get(url(page));
    }

    public void follow(String linkName) {
        findElement(By.linkText(linkName)).click();
    }

    public boolean isCurrent() {
        return getCurrentUrl().contains(url());
    }

    public void hasErrorText(String error) {

    }

    protected WebElement inputField(String name) {
        return findElement(By.name(name));
    }


}
