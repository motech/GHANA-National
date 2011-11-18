package org.motechproject.ghana.national.web;

import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.UserType;
import org.motechproject.ghana.national.exception.FacilityAlreadyFoundException;
import org.motechproject.ghana.national.service.EmailTemplateService;
import org.motechproject.ghana.national.service.UserService;
import org.motechproject.ghana.national.web.form.CreateUserForm;
import org.motechproject.ghana.national.web.form.SearchFacilityForm;
import org.motechproject.ghana.national.web.form.SearchUserForm;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.User;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserControllerTest {

    private UserController controller;
    @Mock
    private UserService userService;
    @Mock
    private MessageSource messageSource;
    @Mock
    EmailTemplateService emailTemplateService;
    @Mock
    BindingResult mockBindingResult;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new UserController(userService, messageSource, emailTemplateService);
        mockBindingResult = mock(BindingResult.class);
    }

    @Test
    public void shouldReturnNewUserForm() {
        ModelMap model = mock(ModelMap.class);
        List<String> roles = new ArrayList<String>();

        when(userService.fetchAllRoles()).thenReturn(roles);

        String view = controller.newUser(model);

        assertEquals(UserController.NEW_USER_VIEW, view);
        ArgumentCaptor<CreateUserForm> captor = ArgumentCaptor.forClass(CreateUserForm.class);
        verify(model).addAttribute("roles", roles);
        verify(model).addAttribute(eq("createUserForm"), captor.capture());
        CreateUserForm captured = captor.getValue();
        assertNotNull(captured);
    }

    @Test
    public void shouldAddNewUser() throws UserAlreadyExistsException {
        CreateUserForm form = new CreateUserForm();
        form.setEmail("jack@daniels.com");
        form.setFirstName("Jack");
        form.setMiddleName("H");
        form.setLastName("Daniels");
        form.setPhoneNumber("1234");
        form.setRole(UserType.Role.CALL_CENTER_ADMIN.key());

        BindingResult bindingResult = mock(BindingResult.class);
        ModelMap model = mock(ModelMap.class);
        String userId = "1234";
        final org.openmrs.User openMRSuser= new org.openmrs.User();
        openMRSuser.setSystemId("1234");
        Map test = new HashMap() {{
            put("openMRSUser", openMRSuser);
            put("password", "P@ssw0rd");
        }};
        when(userService.saveUser(any(User.class))).thenReturn(test);

        String view = controller.createUser(form, bindingResult, model);

        assertEquals(UserController.CREATE_USER_SUCCESS_VIEW, view);
        verify(model).put("userId","1234");
        verify(model).put("userName", "Jack H Daniels");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).saveUser(captor.capture());
        User captured = captor.getValue();
        assertEquals("jack@daniels.com", captured.getId());
        assertEquals(form.getRole(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE));
        assertEquals(form.getEmail(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_EMAIL));
        assertEquals(form.getPhoneNumber(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER));
    }

    private String getAttrValue(User user, String name) {
        for (Attribute userAttribute : user.getAttributes())
            if (userAttribute.name().equalsIgnoreCase(name)) return userAttribute.value();
        return null;
    }

    @Test
    public void shouldReturnErrorMessageIfUserAlreadyExists() throws UserAlreadyExistsException {
        CreateUserForm form = new CreateUserForm();
        form.setEmail("jack@daniels.com");
        form.setFirstName("Jack");
        form.setMiddleName("H");
        form.setLastName("Daniels");
        form.setPhoneNumber("1234");
        form.setRole(UserType.Role.CALL_CENTER_ADMIN.key());

        BindingResult bindingResult = mock(BindingResult.class);
        ModelMap model = mock(ModelMap.class);
        List<String> roles = Arrays.asList("role1");
        Map<String, Object> boundModel = new HashMap<String, Object>();

        when(userService.saveUser(any(User.class))).thenThrow(new UserAlreadyExistsException());
        when(bindingResult.getModel()).thenReturn(boundModel);
        when(userService.fetchAllRoles()).thenReturn(roles);

        String view = controller.createUser(form, bindingResult, model);

        assertEquals(UserController.NEW_USER_VIEW, view);
        verify(model).mergeAttributes(boundModel);
        verify(model).addAttribute("roles", roles);
        verify(bindingResult).addError(any(FieldError.class));
    }

    @Test
    public void shouldSearchForUser() throws FacilityAlreadyFoundException {
        String staffID = "1";
        String firstName = "TEST";
        String middleName = "TEST";
        String lastName = "TEST";
        String email = "TEST@gmail.COM";
        String phoneNumber = "0987654321";
        String role = "System Developer";

        SearchUserForm searchUserForm = new SearchUserForm();
        searchUserForm.setStaffID(staffID);
        searchUserForm.setFirstName(firstName);
        searchUserForm.setMiddleName(middleName);
        searchUserForm.setLastName(lastName);
        searchUserForm.setPhoneNumber(phoneNumber);
        searchUserForm.setRole(role);

        ModelMap modelMap = new ModelMap();
        controller.searchUsers(searchUserForm, mockBindingResult, modelMap);
        Assert.assertNotNull(modelMap.get("requestedUsers"));

    }

}
