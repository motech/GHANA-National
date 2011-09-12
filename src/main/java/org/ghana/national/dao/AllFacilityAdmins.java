package org.ghana.national.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ghana.national.domain.FacilityAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllFacilityAdmins extends AllUsers<FacilityAdmin> {
    @Autowired
    protected AllFacilityAdmins(CouchDbConnector db) {
        super(FacilityAdmin.class, db);
    }

    @Override
    @GenerateView
    public FacilityAdmin findByUsername(String username) {
        List<FacilityAdmin> users = queryView("by_username", username);
        if (users.isEmpty()) return null;
        return users.get(0);
    }
}
