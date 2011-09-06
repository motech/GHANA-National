package org.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static java.util.Arrays.asList;

@TypeDiscriminator("doc.type === 'SUPER_ADMIN'")
public class SuperAdmin extends MotechAuditableDataObject implements UserDetails {
    @JsonProperty("type")
    private final String type = "SUPER_ADMIN";
    private String username;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;


    private SuperAdmin() {
    }

    public SuperAdmin(String username) {
        this.username = username;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
    }

    @JsonIgnore
    public Collection<GrantedAuthority> getAuthorities() {
        GrantedAuthority grantedAuthority = new GrantedAuthority() {
            public String getAuthority() {
                return "admin";
            }
        };
        return asList(grantedAuthority);
    }

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
