package org.ghana.national.web;

import org.apache.log4j.Logger;
import org.ghana.national.dao.AllCallCenterAdmins;
import org.ghana.national.dao.AllFacilityAdmins;
import org.ghana.national.dao.AllSuperAdmins;
import org.ghana.national.domain.User;
import org.ghana.national.web.security.LoginSuccessHandler;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/security/password/reset")
public class PasswordResetController {
    private Logger log = Logger.getLogger("PasswordResetController");
    private AllSuperAdmins superAdmins;
    private AllCallCenterAdmins callCenterAdmins;
    private AllFacilityAdmins facilityAdmins;

    @Autowired
    public PasswordResetController(AllSuperAdmins superAdmins, AllCallCenterAdmins callCenterAdmins, AllFacilityAdmins facilityAdmins) {
        this.superAdmins = superAdmins;
        this.callCenterAdmins = callCenterAdmins;
        this.facilityAdmins = facilityAdmins;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String passwordResetForm() {
        return "password/reset";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String changePassword(@RequestParam(value = "currentPassword") String currentPassword,
                                 @RequestParam(value = "newPassword") String newPassword,
                                 @RequestParam(value = "newPasswordConfirmation") String newPasswordConfirmation,
                                 Model uiModel, HttpServletRequest request) {
        if (newPassword != newPasswordConfirmation) {
            uiModel.addAttribute("errors", new FieldError("password", "newPasswordConfirmation", "Your password confirmation didn't match your new password"));
            return "password/reset";
        }
        User user = (User) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        if (!new StrongPasswordEncryptor().checkPassword(currentPassword, user.getDigestedPassword())) {
            uiModel.addAttribute("errors", new FieldError("password", "oldPassword", "The current password is incorrect"));
            return "password/reset";
        }

        return "password/success";
    }
}
