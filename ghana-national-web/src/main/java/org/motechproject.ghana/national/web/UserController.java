package org.motechproject.ghana.national.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.UserType;
import org.motechproject.ghana.national.service.EmailTemplateService;
import org.motechproject.ghana.national.service.UserService;
import org.motechproject.ghana.national.web.form.CreateUserForm;
import org.motechproject.ghana.national.web.form.SearchFacilityForm;
import org.motechproject.ghana.national.web.form.SearchUserForm;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.User;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping(value = "/admin/users")
public class UserController {
    private UserService userService;
    private MessageSource messageSource;

    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String EMAIL = "email";
    public static final String CREATE_USER_FORM = "createUserForm";
    public static final String USER_ALREADY_EXISTS = "user_email_already_exists";
    public static final String NEW_USER_VIEW = "users/new";
    public static final String CREATE_USER_SUCCESS_VIEW = "users/success";
    private EmailTemplateService emailTemplateService;
    public static final String SEARCH_USER_FORM = "searchUserForm";
    public static final String SEARCH_USER = "users/search";

    public UserController() {
    }

    @Autowired
    public UserController(UserService userService, MessageSource messageSource, EmailTemplateService emailTemplateService) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.emailTemplateService = emailTemplateService;
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
        user.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_EMAIL, createUserForm.getEmail()));
        user.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER, createUserForm.getPhoneNumber()));
        String roleOfStaff = createUserForm.getRole();
        user.addAttribute(new Attribute(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE, roleOfStaff));

        user.securityRole(UserType.Role.securityRoleFor(roleOfStaff));
        if (UserType.Role.isAdmin(roleOfStaff)) user.id(createUserForm.getEmail());
        try {
            HashMap userData = userService.saveUser(user);
            model.put(USER_ID, userData.get("userLoginId"));
            model.put(USER_NAME, user.fullName());
            if (roleOfStaff.equals("Super Admin") || roleOfStaff.equals("Facility Admin") || roleOfStaff.equals("CallCenter Admin"))
                emailTemplateService.sendEmailUsingTemplates((String) userData.get("userLoginId"), (String) userData.get("password"));
        } catch (UserAlreadyExistsException e) {
            bindingResult.addError(new FieldError(CREATE_USER_FORM, EMAIL, messageSource.getMessage(USER_ALREADY_EXISTS, null, Locale.getDefault())));
            model.addAttribute("roles", userService.fetchAllRoles());
            model.mergeAttributes(bindingResult.getModel());
            return NEW_USER_VIEW;
        }
        return CREATE_USER_SUCCESS_VIEW;
    }

    @ApiSession
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String searchFacilityForm(ModelMap modelMap) {
        modelMap.put(SEARCH_USER_FORM, new SearchUserForm());
        return SEARCH_USER;
    }

    @ApiSession
    @RequestMapping(value = "searchUsers", method = RequestMethod.POST)
    public String searchFacility(@Valid final SearchUserForm searchUserForm, BindingResult bindingResult, ModelMap modelMap) {
        List<User> allUsers= userService.getAllUsers();
        List<User> requestedUsers = new ArrayList<User>();
        Map searchFields = new HashMap() {{
            put(1, searchUserForm.getStaffId());
            put(2, searchUserForm.getFirstName().toLowerCase());
            put(3, searchUserForm.getMiddleName().toLowerCase());
            put(4, searchUserForm.getLastName().toLowerCase());
            put(5, searchUserForm.getRole());
        }};
        int fieldsForSearch = 1;
        int fieldIndex;
        Map searchFieldsCombination = new HashMap();
        for (int loopCounter = 1; loopCounter <= 5; loopCounter++) {
            if (!searchFields.get(loopCounter).equals("")) {
                searchFieldsCombination.put(loopCounter, searchFields.get(loopCounter));
                fieldsForSearch++;
            } else {
                searchFieldsCombination.put(loopCounter, "fieldNotRequiredForSearch");
            }
        }
//        if (fieldsForSearch == 1) {
//            modelMap.put("requestedUsers", requestedUsers);
//            return SEARCH_USER;
//        }
        for (User searchUser : allUsers) {
            try {
                int combinationFieldSize = 1;
                for (fieldIndex = 1; fieldIndex <= searchFields.size(); fieldIndex++) {
                    String searchValue = (String) searchFieldsCombination.get(fieldIndex);
                    if (StringUtils.equals(searchUser.id(), searchValue)
                            || StringUtils.equals(StringUtils.substring(searchValue, 0, 3), StringUtils.substring(searchUser.firstName(), 0, 3).toLowerCase())
                            || StringUtils.equals(StringUtils.substring(searchValue, 0, 3), StringUtils.substring(searchUser.middleName(), 0, 3).toLowerCase())
                            || StringUtils.equals(StringUtils.substring(searchValue, 0, 3), StringUtils.substring(searchUser.lastName(), 0, 3).toLowerCase())){

                        combinationFieldSize++;
                    }
                }
                if (combinationFieldSize == fieldsForSearch)
                    requestedUsers.add(searchUser);
            } catch (Exception e) {
            }
        }
        modelMap.put("requestedUsers", requestedUsers);
        return SEARCH_USER;
    }
}
