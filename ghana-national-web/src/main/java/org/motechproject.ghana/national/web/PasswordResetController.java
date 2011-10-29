package org.motechproject.ghana.national.web;

import org.apache.log4j.Logger;
import org.motechproject.ghana.national.web.form.PasswordResetForm;
import org.motechproject.ghana.national.web.security.LoginSuccessHandler;
import org.motechproject.mrs.security.MRSSecurityUser;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.MRSUserAdaptor;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "/security/password/reset")
public class PasswordResetController {
    private Logger log = Logger.getLogger("PasswordResetController");

    @Autowired
    private MRSUserAdaptor userService;

    protected PasswordResetController() {
    }

    public PasswordResetController(MRSUserAdaptor userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String passwordResetForm(Model model) {
        model.addAttribute("passwordResetForm", new PasswordResetForm());
        return "password/reset";
    }

    @ApiSession
    @RequestMapping(method = RequestMethod.POST)
    public String changePassword(@Valid PasswordResetForm passwordResetForm, BindingResult bindingResult, HttpServletRequest request) {
        MRSSecurityUser user = (MRSSecurityUser) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        try {
            userService.changeCurrentUserPassword(passwordResetForm.getCurrentPassword(), passwordResetForm.getNewPassword());
        } catch (MRSException e) {
            bindingResult.addError(new FieldError("passwordResetForm", "currentPassword", e.getMessage()));
        }
        return bindingResult.hasErrors() ? "password/reset" : "password/success";
    }
}
