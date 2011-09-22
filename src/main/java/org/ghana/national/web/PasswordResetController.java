package org.ghana.national.web;

import org.apache.log4j.Logger;
import org.ghana.national.dao.AllCallCenterAdmins;
import org.ghana.national.dao.AllFacilityAdmins;
import org.ghana.national.dao.AllSuperAdmins;
import org.ghana.national.dao.AllUsers;
import org.ghana.national.domain.CallCenterAdmin;
import org.ghana.national.domain.FacilityAdmin;
import org.ghana.national.domain.SuperAdmin;
import org.ghana.national.domain.User;
import org.ghana.national.web.form.PasswordResetForm;
import org.ghana.national.web.security.LoginSuccessHandler;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/security/password/reset")
public class PasswordResetController {
    private Logger log = Logger.getLogger("PasswordResetController");
    private Map<String, AllUsers> usersByAuthorityMap = new HashMap<String, AllUsers>();
    private PasswordEncryptor encryptor;

    @Autowired
    public PasswordResetController(AllSuperAdmins superAdmins, AllCallCenterAdmins callCenterAdmins, AllFacilityAdmins facilityAdmins, PasswordEncryptor encryptor) {
        usersByAuthorityMap.put(SuperAdmin.class.getSimpleName(), superAdmins);
        usersByAuthorityMap.put(CallCenterAdmin.class.getSimpleName(), callCenterAdmins);
        usersByAuthorityMap.put(FacilityAdmin.class.getSimpleName(), facilityAdmins);
        this.encryptor = encryptor;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String passwordResetForm(Model model) {
        model.addAttribute("passwordResetForm", new PasswordResetForm());
        return "password/reset";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String changePassword(@Valid PasswordResetForm passwordResetForm, BindingResult bindingResult, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        if (encryptor.checkPassword(passwordResetForm.getCurrentPassword(), user.getDigestedPassword())) {
            user.setDigestedPassword(encryptor.encryptPassword(passwordResetForm.getNewPassword()));
            usersByAuthorityMap.get(user.getAuthority()).update(user);
        } else {
            bindingResult.addError(new FieldError("passwordResetForm", "currentPassword", "The current password is incorrect"));
        }
        return bindingResult.hasErrors() ? "password/reset" : "password/success";
    }
}
