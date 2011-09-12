package org.motechproject.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuthenticationProviderTest {
    @Mock
    private UsernamePasswordAuthenticationToken authentication;
    @Mock
    private SecurityGroup securityGroup;
    @Mock
    private UserDetails user;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void shouldRaiseInvalidCredentialsExceptionForEmptyUsername() {
        new AuthenticationProvider(new ArrayList<SecurityGroup>()).retrieveUser("", authentication);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void shouldRaiseInvalidCredentialsExceptionForNullPassword() {
        when(authentication.getCredentials()).thenReturn(null);
        new AuthenticationProvider(new ArrayList<SecurityGroup>()).retrieveUser("foo", authentication);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void shouldRaiseInvalidCredentialsExceptionForEmptyPassword() {
        when(authentication.getCredentials()).thenReturn("");
        new AuthenticationProvider(new ArrayList<SecurityGroup>()).retrieveUser("foo", authentication);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void shouldRaiseInvalidCredentialsExceptionForUsernameAndPasswordMismatch() {
        when(authentication.getCredentials()).thenReturn("password");
        when(securityGroup.getAuthenticatedUser(anyString(), anyString())).thenReturn(null);
        new AuthenticationProvider(asList(securityGroup)).retrieveUser("foo", authentication);
    }

    @Test
    public void shouldAuthenticateForAValidUsernameAndPasswordCombination() {
        when(authentication.getCredentials()).thenReturn("password");
        when(securityGroup.getAuthenticatedUser("username", "password")).thenReturn(user);
        UserDetails authenticatedUser = new AuthenticationProvider(asList(securityGroup)).retrieveUser("username", authentication);

        assertThat(authenticatedUser, is(equalTo(user)));
    }
}
