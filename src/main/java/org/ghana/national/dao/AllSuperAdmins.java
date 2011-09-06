package org.ghana.national.dao;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.ghana.national.domain.SuperAdmin;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.security.SecurityGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllSuperAdmins extends MotechAuditableRepository<SuperAdmin> implements SecurityGroup {

    @Autowired
    protected AllSuperAdmins(CouchDbConnector db) {
        super(SuperAdmin.class, db);
    }

    public UserDetails getAuthenticatedUser(String userName, String password) {
        return findByUserNameAndPassword(userName, password);
    }

    @View(name = "find_by_user_name_and_password", map = "function(doc) {{emit([doc.username]);}}")
    public SuperAdmin findByUserNameAndPassword(String username, String password) {
        List<SuperAdmin> superAdmins = queryView("find_by_user_name_and_password", ComplexKey.of(username));
        if (superAdmins.isEmpty()) return null;
        return superAdmins.get(0);
    }
}
