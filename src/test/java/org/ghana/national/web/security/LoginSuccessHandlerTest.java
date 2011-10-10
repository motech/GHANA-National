package org.ghana.national.web.security;

import org.junit.Test;
import org.motechproject.mrs.security.MRSUser;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

public class LoginSuccessHandlerTest {
    @Test
    public void shouldSetUserOnSessionOnAuthenticationSuccess() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        Authentication authentication = mock(Authentication.class);
        MRSUser user = mock(MRSUser.class);
        when(authentication.getPrincipal()).thenReturn(user);
        HttpSession httpSession = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(httpSession);


        new LoginSuccessHandler().onAuthenticationSuccess(servletRequest, mock(HttpServletResponse.class), authentication);
        verify(httpSession).setAttribute(LoginSuccessHandler.LOGGED_IN_USER, user);
    }
}
