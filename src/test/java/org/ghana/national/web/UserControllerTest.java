package org.ghana.national.web;

import org.ghana.national.web.form.CreateUserForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserController controller;

    @Before
    public void setUp() {
        controller = new UserController();
    }

    @Test
    public void shouldReturnNewUserForm() {
        ModelMap model = mock(ModelMap.class);

        String view = controller.newUser(model);

        ArgumentCaptor<CreateUserForm> captor = ArgumentCaptor.forClass(CreateUserForm.class);
        verify(model).addAttribute(eq("createUserForm"), captor.capture());
        CreateUserForm captured = captor.getValue();
        assertNotNull(captured);
        assertEquals(UserController.NEW_USER_VIEW,view);
    }
}
