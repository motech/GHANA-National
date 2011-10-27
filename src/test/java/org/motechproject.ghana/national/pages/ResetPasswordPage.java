package org.motechproject.ghana.national.pages;

import org.jbehave.web.selenium.WebDriverProvider;

public class ResetPasswordPage extends GhanaPage {
    public ResetPasswordPage(WebDriverProvider driverProvider) {
        super(driverProvider, "security/password/reset");
    }

    public void resetPassword(String currentPassword, String newPassword, String newPasswordConfirmation) {
        inputField("currentPassword").sendKeys(currentPassword);
        inputField("newPassword").sendKeys(newPassword);
        inputField("newPasswordConfirmation").sendKeys(newPasswordConfirmation);
        inputField("submit").click();
    }
}
