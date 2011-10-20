package org.ghana.national.web;

import org.ghana.national.web.form.CreateUserForm;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/admin/users")
public class UserController {

    public static final String NEW_USER_VIEW = "users/new";

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newUser(ModelMap modelMap) {
        modelMap.addAttribute("createUserForm", new CreateUserForm());
        return NEW_USER_VIEW;
    }
}
