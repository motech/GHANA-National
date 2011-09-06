package org.ghana.national.dao;

import org.ektorp.CouchDbConnector;
import org.ghana.national.domain.SuperAdmin;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.security.SecurityGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AllSuperAdmins extends MotechAuditableRepository<SuperAdmin> implements SecurityGroup {

    @Autowired
    protected AllSuperAdmins(CouchDbConnector db) {
        super(SuperAdmin.class, db);
    }

    public UserDetails getAuthenticatedUser(String username, String password) {
        return null;
    }

    public SuperAdmin findByUserNameAndPassword(String userName, String password) {
        String hashedPassword = password;


        return new SuperAdmin();
    }
}
