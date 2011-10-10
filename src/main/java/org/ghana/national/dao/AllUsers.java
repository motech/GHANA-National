package org.ghana.national.dao;

import org.ektorp.CouchDbConnector;
import org.ghana.national.domain.User;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.motechproject.dao.MotechAuditableRepository;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class AllUsers<T extends User> extends MotechAuditableRepository<T> {

    protected AllUsers(Class<T> type, CouchDbConnector db) {
        super(type, db);
    }

    public UserDetails getAuthenticatedUser(String userName, String password) {
        User found = findByUsername(userName);
        if (found == null) return null;
        if (!new StrongPasswordEncryptor().checkPassword(password, found.getDigestedPassword()))
            return null;
        return found;
    }

    @Override
    public void add(T entity) {
        entity.setDigestedPassword(new StrongPasswordEncryptor().encryptPassword(entity.getPassword()));
        super.add(entity);
    }

    protected abstract User findByUsername(String userName);
}
