package org.ghana.national.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ghana.national.domain.CallCenterAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllCallCenterAdmins extends AllUsers<CallCenterAdmin> {
    @Autowired
    protected AllCallCenterAdmins(CouchDbConnector db) {
        super(CallCenterAdmin.class, db);
    }

    @Override
    @GenerateView
    public CallCenterAdmin findByUsername(String username) {
        List<CallCenterAdmin> users = queryView("by_username", username);
        if (users.isEmpty()) return null;
        return users.get(0);
    }
}
