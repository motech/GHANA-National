package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.web.helper.StaffHelper;
import org.motechproject.ghana.national.service.EmailTemplateService;
import org.motechproject.ghana.national.service.IdentifierGenerationService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.web.form.StaffForm;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/staffs")
public class StaffController {
    public static final String REQUESTED_STAFFS = "requestedStaffs";
    private StaffService staffService;
    private MessageSource messageSource;
    private IdentifierGenerationService identifierGenerationService;
    private EmailTemplateService emailTemplateService;
    private StaffHelper staffHelper;

    public static final String STAFF_ID = "userId";
    public static final String STAFF_SEQUENTIAL_ID = "Id";
    public static final String STAFF_NAME = "userName";
    public static final String EMAIL = "email";
    public static final String STAFF_FORM = "staffForm";
    public static final String STAFF_ALREADY_EXISTS = "user_email_already_exists";
    public static final String NEW_STAFF_URL = "staffs/new";
    public static final String SEARCH_STAFF_FORM = "searchStaffForm";
    public static final String SEARCH_STAFF = "staffs/search";
    public static final String EDIT_STAFF_URL = "staffs/edit";

    public StaffController() {
    }

    @Autowired
    public StaffController(StaffService staffService, MessageSource messageSource, EmailTemplateService emailTemplateService,
                           IdentifierGenerationService identifierGenerationService, StaffHelper staffHelper) {
        this.staffService = staffService;
        this.messageSource = messageSource;
        this.emailTemplateService = emailTemplateService;
        this.identifierGenerationService = identifierGenerationService;
        this.staffHelper = staffHelper;
    }

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newStaff(ModelMap modelMap) {
        modelMap.addAttribute(STAFF_FORM, new StaffForm());
        staffHelper.populateRoles(modelMap, staffService.fetchAllRoles());
        return NEW_STAFF_URL;
    }

    @ApiSession
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(@Valid StaffForm staffForm, BindingResult bindingResult, ModelMap modelMap) {
        Map userData;
        MRSUser mrsUser = staffForm.createUser();
        String roleOfStaff = staffForm.getRole();

        mrsUser.systemId(identifierGenerationService.newStaffId());

        try {
            userData = staffService.saveUser(mrsUser);
            org.openmrs.User openMRSUser = (org.openmrs.User) userData.get("openMRSUser");
            modelMap.put(STAFF_ID, openMRSUser.getSystemId());
            modelMap.put(STAFF_NAME, mrsUser.getFullName());
            if (StaffType.Role.isAdmin(roleOfStaff)) {
                emailTemplateService.sendEmailUsingTemplates(openMRSUser.getUsername(), (String) userData.get("password"));
            }
            modelMap.put("successMessage", "Staff created successfully.");
            staffHelper.populateRoles(modelMap, staffService.fetchAllRoles());
            return staffHelper.getStaffForId(modelMap, staffService.getUserById(openMRSUser.getSystemId()));
        } catch (UserAlreadyExistsException e) {
            bindingResult.addError(new FieldError(STAFF_FORM, EMAIL, messageSource.getMessage(STAFF_ALREADY_EXISTS, null, Locale.getDefault())));
            staffHelper.populateRoles(modelMap, staffService.fetchAllRoles());
            modelMap.mergeAttributes(bindingResult.getModel());
            return NEW_STAFF_URL;
        }
    }

    @ApiSession
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String edit(ModelMap modelMap, HttpServletRequest httpServletRequest) {
        String staffId = httpServletRequest.getParameter(STAFF_SEQUENTIAL_ID);
        staffHelper.populateRoles(modelMap, staffService.fetchAllRoles());
        return staffHelper.getStaffForId(modelMap, staffService.getUserById(staffId));
    }

    @ApiSession
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(@ModelAttribute(StaffController.STAFF_FORM) StaffForm staffForm, BindingResult bindingResult, ModelMap modelMap) {
        MRSUser mrsUser = staffForm.createUser();
        try {
            staffService.updateUser(mrsUser);
            staffHelper.populateRoles(modelMap, staffService.fetchAllRoles());
            staffHelper.getStaffForId(modelMap, staffService.getUserById(mrsUser.getSystemId()));
            modelMap.put("successMessage", "Staff edited successfully.");
        } catch (UserAlreadyExistsException ignored) {
            //cannot happen as the Id is unique.
        }
        return EDIT_STAFF_URL;
    }

    @ApiSession
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String search(ModelMap modelMap) {
        modelMap.put(SEARCH_STAFF_FORM, new StaffForm());
        staffHelper.populateRoles(modelMap, staffService.fetchAllRoles());
        return SEARCH_STAFF;
    }

    @ApiSession
    @RequestMapping(value = "searchStaffs", method = RequestMethod.POST)
    public String search(@Valid final StaffForm staffForm, ModelMap modelMap) {
        final List<MRSUser> mrsUsers = staffService.searchStaff(staffForm.getStaffId(), staffForm.getFirstName(),
                staffForm.getMiddleName(), staffForm.getLastName(), staffForm.getPhoneNumber(), staffForm.getRole());

        final ArrayList<StaffForm> staffForms = new ArrayList<StaffForm>();
        for (MRSUser mrsUser : mrsUsers) {
            staffForms.add(new StaffForm(mrsUser.getId(), mrsUser.getSystemId(), mrsUser.getFirstName(), mrsUser.getMiddleName(), mrsUser.getLastName(),
                    staffHelper.getEmail(mrsUser), staffHelper.getPhoneNumber(mrsUser), staffHelper.getRole(mrsUser)));
        }
        modelMap.put(SEARCH_STAFF_FORM, new StaffForm());
        modelMap.put(REQUESTED_STAFFS, staffForms);
        staffHelper.populateRoles(modelMap, staffService.fetchAllRoles());
        return SEARCH_STAFF;
    }
}
