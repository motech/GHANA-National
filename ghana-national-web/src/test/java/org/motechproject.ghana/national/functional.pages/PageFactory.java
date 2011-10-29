package org.motechproject.ghana.national.functional.pages;

import org.jbehave.web.selenium.WebDriverProvider;

import java.util.HashMap;
import java.util.Map;

public class PageFactory {
    private final WebDriverProvider webDriverProvider;
    private Map<String,GhanaPage> pages = new HashMap<String, GhanaPage>();


    public PageFactory(WebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
        pages.put("adminDashboard", new AdminDashboardPage(webDriverProvider));
        pages.put("login" ,new LoginPage(webDriverProvider));
        pages.put("resetPassword" ,new ResetPasswordPage(webDriverProvider));
    }

    public AdminDashboardPage getAdminDashBoard() {
        return (AdminDashboardPage) getPage("adminDashboard");
    }

    public LoginPage getLogin() {
        return (LoginPage) getPage("login");
    }

    public GhanaPage getPage(String page) {
        return pages.get(page);
    }
}
