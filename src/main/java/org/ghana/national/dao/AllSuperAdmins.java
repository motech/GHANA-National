package org.ghana.national.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ghana.national.domain.SuperAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllSuperAdmins extends AllUsers<SuperAdmin> {
    @Autowired
    protected AllSuperAdmins(CouchDbConnector db) {
        super(SuperAdmin.class, db);
    }

    @Override
    @GenerateView
    public SuperAdmin findByUsername(String username) {
        List<SuperAdmin> users = queryView("by_username", username);
        if (users.isEmpty()) return null;
        return users.get(0);
    }
}
