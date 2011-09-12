package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.model.MotechAuditableDataObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public abstract class User extends MotechAuditableDataObject implements UserDetails {
    private String username;
    private transient String password;

    private String digestedPassword;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
    }

    public User() {
    }

    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        GrantedAuthority grantedAuthority = new GrantedAuthority() {
            public String getAuthority() {
                return User.this.getAuthority();
            }
        };
        return Arrays.asList(grantedAuthority);
    }

    protected abstract String getAuthority();

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getDigestedPassword() {
        return digestedPassword;
    }

    public void setDigestedPassword(String digestedPassword) {
        this.digestedPassword = digestedPassword;
    }
}