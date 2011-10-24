package org.ghana.national.web;

import com.sun.mail.iap.Argument;
import org.ghana.national.domain.Constants;
import org.ghana.national.exception.UserAlreadyFoundException;
import org.ghana.national.service.UserService;
import org.ghana.national.web.form.CreateUserForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.mrs.model.User;
import org.motechproject.mrs.model.UserAttribute;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.xml.ws.Service;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserControllerTest {

    private UserController controller;
    @Mock
    private UserService userService;
    @Mock
    private MessageSource messageSource;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new UserController(userService, messageSource);
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
    public void shouldAddNewUser() throws UserAlreadyFoundException {
        CreateUserForm form = new CreateUserForm();
        form.setEmail("jack@daniels.com");
        form.setFirstName("Jack");
        form.setMiddleName("H");
        form.setLastName("Daniels");
        form.setPhoneNumber("1234");
        form.setRole("super");

        BindingResult bindingResult = mock(BindingResult.class);
        ModelMap model = mock(ModelMap.class);
        String userId = "1234";
        when(userService.saveUser(any(User.class))).thenReturn(userId);

        String view = controller.createUser(form, bindingResult, model);

        assertEquals(UserController.CREATE_USER_SUCCESS_VIEW, view);
        verify(model).put("userId", userId);
        verify(model).put("userName", "Jack H Daniels");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).saveUser(captor.capture());
        User captured = captor.getValue();
        assertEquals(form.getRole(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE));
        assertEquals(form.getEmail(), getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_EMAIL));
        assertEquals(form.getPhoneNumber(),getAttrValue(captured, Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER));
    }

    private String getAttrValue(User user, String name) {
        for (UserAttribute userAttribute : user.attributes())
            if (userAttribute.name().equalsIgnoreCase(name)) return userAttribute.value();
        return null;
    }

    @Test
    public void shouldReturnErrorMessageIfUserAlreadyExists() throws UserAlreadyFoundException {
        CreateUserForm form = new CreateUserForm();
        form.setEmail("jack@daniels.com");
        form.setFirstName("Jack");
        form.setMiddleName("H");
        form.setLastName("Daniels");
        form.setPhoneNumber("1234");
        form.setRole("super");

        BindingResult bindingResult = mock(BindingResult.class);
        ModelMap model = mock(ModelMap.class);
        Map<String, Object> boundModel = new HashMap<String, Object>();

        when(userService.saveUser(any(User.class))).thenThrow(new UserAlreadyFoundException());
        when(bindingResult.getModel()).thenReturn(boundModel);

        String view = controller.createUser(form, bindingResult, model);

        assertEquals(UserController.NEW_USER_VIEW, view);
        verify(model).mergeAttributes(boundModel);
        verify(bindingResult).addError(any(FieldError.class));
    }

}
