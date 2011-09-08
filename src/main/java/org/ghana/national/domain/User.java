package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.model.MotechAuditableDataObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public abstract class User extends MotechAuditableDataObject implements UserDetails {
    String username;
    boolean accountNonExpired;
    boolean accountNonLocked;
    boolean isCredentialsNonExpired;
    boolean isEnabled;

    public User(String username) {
        this.username = username;
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

    public String getPassword() {
        return null;
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
}