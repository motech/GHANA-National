package org.ghana.national.dao;

import org.ektorp.CouchDbConnector;
import org.ghana.national.domain.CallCenterAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllCallCenterAdmins extends AllUsers<CallCenterAdmin> {
    @Autowired
    protected AllCallCenterAdmins(CouchDbConnector db) {
        super(CallCenterAdmin.class, db);
    }
}
