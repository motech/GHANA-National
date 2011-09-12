package org.ghana.national.dao;

import org.ektorp.CouchDbConnector;
import org.ghana.national.domain.User;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.security.SecurityGroup;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class AllUsers<T extends User> extends MotechAuditableRepository<T> implements SecurityGroup {

    protected AllUsers(Class<T> type, CouchDbConnector db) {
        super(type, db);
    }

    public UserDetails getAuthenticatedUser(String userName, String password) {
        return findByUsername(userName);
    }

    protected abstract UserDetails findByUsername(String userName);
}
