package org.ghana.national.web;

import org.ghana.national.domain.Constants;
import org.ghana.national.domain.UserType;
import org.ghana.national.exception.UserAlreadyFoundException;
import org.ghana.national.service.UserService;
import org.ghana.national.web.form.CreateUserForm;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.model.UserAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Locale;

@Controller
@RequestMapping(value = "/admin/users")
public class UserController {
    private UserService userService;
    private MessageSource messageSource;

    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String FIRST_NAME = "firstName";
    public static final String CREATE_USER_FORM = "createUserForm";
    public static final String USER_ALREADY_EXISTS = "user_already_exists";
    public static final String NEW_USER_VIEW = "users/new";
    public static final String CREATE_USER_SUCCESS_VIEW = "users/success";

    @Autowired
    public UserController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newUser(ModelMap modelMap) {
        modelMap.addAttribute("createUserForm", new CreateUserForm());
        modelMap.addAttribute("roles", userService.fetchAllRoles());
        return NEW_USER_VIEW;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createUser(@Valid CreateUserForm createUserForm, BindingResult bindingResult, ModelMap model) {
        User user = new User();
        user.firstName(createUserForm.getFirstName()).middleName(createUserForm.getMiddleName()).lastName(createUserForm.getLastName());
        user.addAttribute(new UserAttribute(Constants.PERSON_ATTRIBUTE_TYPE_EMAIL, createUserForm.getEmail()));
        user.addAttribute(new UserAttribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, createUserForm.getPhoneNumber()));
        user.addAttribute(new UserAttribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, createUserForm.getRole()));
        user.securityRole(UserType.Role.securityRoleFor(createUserForm.getRole()));
        if (UserType.Role.isAdmin(createUserForm.getRole())) user.id(createUserForm.getEmail());

        try {
            String userId = userService.saveUser(user);
            model.put(USER_ID, userId);
            model.put(USER_NAME, user.fullName());
        } catch (UserAlreadyFoundException e) {
            bindingResult.addError(new FieldError(CREATE_USER_FORM, FIRST_NAME, messageSource.getMessage(USER_ALREADY_EXISTS, null, Locale.getDefault())));
            model.mergeAttributes(bindingResult.getModel());
            return NEW_USER_VIEW;
        }
        return CREATE_USER_SUCCESS_VIEW;
    }
}
