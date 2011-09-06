package org.motechproject.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityGroup {
    UserDetails getAuthenticatedUser(String username, String password);
}
