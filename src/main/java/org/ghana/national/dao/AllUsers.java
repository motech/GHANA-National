package org.ghana.national.dao;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.ghana.national.domain.User;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.security.SecurityGroup;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public abstract class AllUsers<T extends User> extends MotechAuditableRepository<T> implements SecurityGroup {
    protected AllUsers(Class<T> type, CouchDbConnector db) {
        super(type, db);
    }

    public UserDetails getAuthenticatedUser(String userName, String password) {
        return findByUserNameAndPassword(userName, password);
    }

    @View(name = "find_by_user_name_and_password", map = "function(doc) {{emit([doc.username]);}}")
    public T findByUserNameAndPassword(String username, String password) {
        List<T> superAdmins = queryView("find_by_user_name_and_password", ComplexKey.of(username));
        if (superAdmins.isEmpty()) return null;
        return superAdmins.get(0);
    }
}
