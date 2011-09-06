package org.motechproject.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private List<SecurityGroup> groups;

    public AuthenticationProvider(List<SecurityGroup> groups) {
        this.groups = groups;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String userName, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String password = (String) authentication.getCredentials();
        for (SecurityGroup securityGroup : groups) {
            UserDetails authenticatedUser = securityGroup.getAuthenticatedUser(userName, password);
            if (authenticatedUser != null) return authenticatedUser;
        }

        throw new InvalidCredentialsException();
    }
}
