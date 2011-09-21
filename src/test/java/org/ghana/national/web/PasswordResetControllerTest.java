package org.ghana.national.web;

import org.ghana.national.dao.AllCallCenterAdmins;
import org.ghana.national.dao.AllFacilityAdmins;
import org.ghana.national.dao.AllSuperAdmins;
import org.ghana.national.domain.SuperAdmin;
import org.ghana.national.domain.User;
import org.ghana.national.web.security.LoginSuccessHandler;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CustomTypeSafeMatcher;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static java.lang.String.format;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PasswordResetControllerTest {
    @Mock
    private AllSuperAdmins superAdmins;
    @Mock
    private AllCallCenterAdmins callCenterAdmins;
    @Mock
    private AllFacilityAdmins facilityAdmins;
    private PasswordResetController passwordResetController;

    @Before
    public void setUp() {
        initMocks(this);
        passwordResetController = new PasswordResetController(superAdmins, callCenterAdmins, facilityAdmins);
    }

    @Test
    public void shouldRenderPasswordResetForm() throws Exception {
        assertThat(passwordResetController.passwordResetForm(), is(equalTo("password/reset")));
    }

    @Test
    public void shouldValidateForConfirmationOFPassword() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Model uiModel = new ExtendedModelMap();
        String viewName = passwordResetController.changePassword("old", "new", "newFoo", uiModel, request);
        assertThat("password/reset", is(equalTo(viewName)));
        assertThat(uiModel, hasErrorsOn("newPasswordConfirmation"));
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
        Model uiModel = new ExtendedModelMap();
        HttpSession session = mock(HttpSession.class);
        User authenticatedUser = mock(User.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(authenticatedUser);
        when(authenticatedUser.getDigestedPassword()).thenReturn(new StrongPasswordEncryptor().encryptPassword("oldPassword"));
        String viewName = passwordResetController.changePassword("old", "new", "new", uiModel, request);
        assertThat("password/reset", is(equalTo(viewName)));
        assertThat(uiModel, hasErrorsOn("oldPassword"));
    }

    @Test
    @Ignore("irfn: wip")
    public void shouldChangePasswordForSuperAdmin() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Model uiModel = new ExtendedModelMap();
        HttpSession session = mock(HttpSession.class);
        SuperAdmin superAdmin = mock(SuperAdmin.class);


        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(superAdmin);
        when(superAdmin.getDigestedPassword()).thenReturn(new StrongPasswordEncryptor().encryptPassword("oldPassword"));

        String viewName = passwordResetController.changePassword("oldPassword", "new", "new", uiModel, request);
        verify(superAdmin, times(1)).setPassword("new");
        verify(superAdmins, times(1)).update(superAdmin);
        assertEquals("password/success", viewName);
    }
}
