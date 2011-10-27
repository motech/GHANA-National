package org.motechproject.ghana.national.pages;

import org.jbehave.web.selenium.WebDriverProvider;

public class LoginPage extends GhanaPage {
    public LoginPage(WebDriverProvider driverProvider) {
        super(driverProvider, "spring_security_login");
    }

    public void login(String user, String password) {
        inputField("j_username").sendKeys(user);
        inputField("j_password").sendKeys(password);
        inputField("submit").click();
    }

    public void logout() {
        go("logout");
    }
}
