package org.ghana.national.web;

import org.ghana.national.service.UserService;
import org.ghana.national.web.form.CreateUserForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;

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

        String view = controller.newUser(model);

        ArgumentCaptor<CreateUserForm> captor = ArgumentCaptor.forClass(CreateUserForm.class);
        verify(model).addAttribute(eq("createUserForm"), captor.capture());
        CreateUserForm captured = captor.getValue();
        assertNotNull(captured);
        assertEquals(UserController.NEW_USER_VIEW, view);
    }
}
