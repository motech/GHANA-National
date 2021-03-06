package org.motechproject.ghana.national.web.security;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mrs.security.MRSSecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class LoginSuccessHandlerTest {
    private HttpServletRequest servletRequest;
    private Authentication authentication;
    private MRSSecurityUser user;
    private HttpSession httpSession;
    private HttpServletResponse response;

    @Before
    public void setUp() {
        servletRequest = mock(HttpServletRequest.class);
        authentication = mock(Authentication.class);
        user = mock(MRSSecurityUser.class);
        httpSession = mock(HttpSession.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void shouldSetUserOnSessionOnAuthenticationSuccess() throws Exception {
        when(authentication.getPrincipal()).thenReturn(user);
        when(servletRequest.getSession()).thenReturn(httpSession);

        new LoginSuccessHandler().onAuthenticationSuccess(servletRequest, response, authentication);
        verify(httpSession).setAttribute(LoginSuccessHandler.LOGGED_IN_USER, user);
    }

    @Test
    public void shouldRedirectSystemDeveloperToAdminIfNoSavedRequest() throws Exception {
        when(authentication.getPrincipal()).thenReturn(user);
        when(servletRequest.getSession()).thenReturn(httpSession);
        when(user.getAuthorities()).thenReturn(new ArrayList<GrantedAuthority>() {{
            add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return "System Developer";
                }
            });
        }});
        when(servletRequest.getSession(false)).thenReturn(httpSession);

        new LoginSuccessHandler().onAuthenticationSuccess(servletRequest, response, authentication);
        verify(response).sendRedirect("admin");
    }

    @Test
    public void shouldRedirectCallcenterAdminToCallcenterIfNoSavedRequest() throws Exception {
        when(authentication.getPrincipal()).thenReturn(user);
        when(servletRequest.getSession()).thenReturn(httpSession);
        when(user.getAuthorities()).thenReturn(new ArrayList<GrantedAuthority>() {{
            add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return "Create/Edit MoTeCH Data";
                }
            });
        }});
        when(servletRequest.getSession(false)).thenReturn(httpSession);

        new LoginSuccessHandler().onAuthenticationSuccess(servletRequest, response, authentication);
        verify(response).sendRedirect("admin"); // TODO: hardcoded until the story for staff role login is played <geet/balaji>
    }

    @Test
    public void shouldRedirectFacilityUserToFacilityIfNoSavedRequest() throws Exception {
        when(authentication.getPrincipal()).thenReturn(user);
        when(servletRequest.getSession()).thenReturn(httpSession);
        when(user.getAuthorities()).thenReturn(new ArrayList<GrantedAuthority>() {{
            add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return "MoTeCH View Only";
                }
            });
        }});
        when(servletRequest.getSession(false)).thenReturn(httpSession);

        new LoginSuccessHandler().onAuthenticationSuccess(servletRequest, response, authentication);
        verify(response).sendRedirect("admin"); // TODO: hardcoded until the story for staff role login is played <geet/balaji>
    }
}
