package org.motechproject.ghana.national.web.form;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;

public class PasswordResetForm {
    @NotNull
    private String currentPassword;
    @NotNull
    private String newPassword;
    @NotNull
    private String newPasswordConfirmation;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getNewPasswordConfirmation() {
        return newPasswordConfirmation;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }

    @AssertFalse(message = "Your password confirmation didn't match your new password")
    public boolean isPasswordConfirmed(){
        return newPassword == newPasswordConfirmation;
    }
}
