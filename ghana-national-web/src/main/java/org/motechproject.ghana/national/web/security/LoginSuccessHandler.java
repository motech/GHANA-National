package org.motechproject.ghana.national.web.security;

import org.motechproject.mrs.security.MRSSecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public static final String LOGGED_IN_USER = "loggedInUser";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MRSSecurityUser user = (MRSSecurityUser) authentication.getPrincipal();
        request.getSession().setAttribute(LOGGED_IN_USER, user);
        if (getRedirectUrl(request) == null) {
            response.sendRedirect(getDashboard(user));
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String getDashboard(MRSSecurityUser user) {
        if (hasRole(user, "System Developer")) return "admin";
        else if (hasRole(user, "Create/Edit MoTeCH Data")) return "callcenter";
        else return "facility";
    }

    private boolean hasRole(MRSSecurityUser user, final String role) {
        Collection<GrantedAuthority> authorities = user.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for (GrantedAuthority authority : authorities) {
            roles.add(authority.getAuthority());
        }
        return roles.contains(role);
    }

    protected SavedRequest getRedirectUrl(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        else return (SavedRequest) session.getAttribute(WebAttributes.SAVED_REQUEST);
    }
}