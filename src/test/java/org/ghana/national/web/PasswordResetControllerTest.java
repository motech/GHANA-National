package org.ghana.national.web;

import org.ghana.national.web.form.PasswordResetForm;
import org.ghana.national.web.security.LoginSuccessHandler;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.security.MRSUser;
import org.motechproject.mrs.services.MRSException;
import org.motechproject.mrs.services.UserService;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static java.lang.String.format;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PasswordResetControllerTest {
    @Mock
    private UserService userService;

    private PasswordResetController passwordResetController;

    @Before
    public void setUp() {
        initMocks(this);
        passwordResetController = new PasswordResetController(userService);
    }

    @Test
    public void shouldRenderPasswordResetForm() throws Exception {
        assertThat(passwordResetController.passwordResetForm(new ExtendedModelMap()), is(equalTo("password/reset")));
    }

    private BaseMatcher<Model> hasErrorsOn(final String errorField) {
        return new CustomTypeSafeMatcher<Model>(format("no errors on field %s", errorField)) {
            @Override
            public boolean matchesSafely(Model model) {
                FieldError errors = (FieldError) model.asMap().get("errors");
                return errors != null && errors.getField().equals(errorField);
            }
        };
    }

    @Test
    public void shouldValidateForWrongOldPassword() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        HttpSession session = mock(HttpSession.class);
        MRSUser authenticatedUser = mock(MRSUser.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(authenticatedUser);
        doThrow(mock(MRSException.class)).when(userService).changeCurrentUserPassword("p1","p2");
        PasswordResetForm passwordResetForm = new PasswordResetForm() {{
            setCurrentPassword("old");
            setNewPassword("new");
            setNewPasswordConfirmation("new");
        }};
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(passwordResetForm, "passwordResetForm");
        String viewName = passwordResetController.changePassword(passwordResetForm, bindingResult, request);
        assertThat("password/reset", is(equalTo(viewName)));
        assertThat(bindingResult.getFieldError("currentPassword"), is(notNullValue()));
    }

    @Test
    public void shouldChangePasswordForMRSUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        MRSUser mrsUser = mock(MRSUser.class);



        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(mrsUser);

        PasswordResetForm passwordResetForm = new PasswordResetForm() {{
            setCurrentPassword("oldPassword");
            setNewPassword("newPassword");
            setNewPasswordConfirmation("newPassword");
        }};
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(passwordResetForm, "passwordResetForm");

        String viewName = passwordResetController.changePassword(passwordResetForm, bindingResult, request);
        verify(userService).changeCurrentUserPassword("oldPassword","newPassword");
        assertEquals("password/success", viewName);
    }
}
