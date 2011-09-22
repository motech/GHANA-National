package org.ghana.national.web.security;

import org.ghana.national.domain.SuperAdmin;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginSuccessHandlerTest {
    @Test
    public void shouldSetUserOnSessionOnAuthenticationSuccess() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        Authentication authentication = mock(Authentication.class);
        SuperAdmin user = new SuperAdmin("foo", "bar");
        when(authentication.getPrincipal()).thenReturn(user);
        HttpSession httpSession = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(httpSession);


        new LoginSuccessHandler().onAuthenticationSuccess(servletRequest, mock(HttpServletResponse.class), authentication);
        verify(httpSession).setAttribute(LoginSuccessHandler.LOGGED_IN_USER, user);
    }
}
