package org.ghana.national.web;

import org.ghana.national.dao.AllCallCenterAdmins;
import org.ghana.national.dao.AllFacilityAdmins;
import org.ghana.national.dao.AllSuperAdmins;
import org.ghana.national.domain.SuperAdmin;
import org.ghana.national.domain.User;
import org.ghana.national.web.form.PasswordResetForm;
import org.ghana.national.web.security.LoginSuccessHandler;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CustomTypeSafeMatcher;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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
    private AllSuperAdmins superAdmins;
    @Mock
    private AllCallCenterAdmins callCenterAdmins;
    @Mock
    private AllFacilityAdmins facilityAdmins;
    @Mock
    private PasswordEncryptor encryptor;

    private PasswordResetController passwordResetController;

    @Before
    public void setUp() {
        initMocks(this);
        passwordResetController = new PasswordResetController(superAdmins, callCenterAdmins, facilityAdmins, encryptor);
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
        User authenticatedUser = mock(User.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(authenticatedUser);
        when(authenticatedUser.getDigestedPassword()).thenReturn(new StrongPasswordEncryptor().encryptPassword("oldPassword"));
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
    public void shouldChangePasswordForSuperAdmin() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        SuperAdmin superAdmin = mock(SuperAdmin.class);



        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(superAdmin);
        when(superAdmin.getDigestedPassword()).thenReturn("oldEncryptedDigest");
        when(superAdmin.getAuthority()).thenReturn(SuperAdmin.class.getSimpleName());
        when(encryptor.checkPassword("oldPassword","oldEncryptedDigest")).thenReturn(true);
        when(encryptor.encryptPassword("newPassword")).thenReturn("newEncryptedDigest");

        PasswordResetForm passwordResetForm = new PasswordResetForm() {{
            setCurrentPassword("oldPassword");
            setNewPassword("newPassword");
            setNewPasswordConfirmation("newPassword");
        }};
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(passwordResetForm, "passwordResetForm");

        String viewName = passwordResetController.changePassword(passwordResetForm, bindingResult, request);
        verify(superAdmin, times(1)).setDigestedPassword("newEncryptedDigest");
        verify(superAdmins, times(1)).update(superAdmin);
        assertEquals("password/success", viewName);
    }
}
