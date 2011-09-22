package org.ghana.national.web.security;

import org.ghana.national.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public static final String LOGGED_IN_USER = "loggedInUser";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        request.getSession().setAttribute(LOGGED_IN_USER, user);
        super.onAuthenticationSuccess(request, response, authentication);
    }

}