package org.ghana.national.dao;

import org.ektorp.CouchDbConnector;
import org.ghana.national.domain.FacilityAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllFacilityAdmins extends AllUsers<FacilityAdmin> {
    @Autowired
    protected AllFacilityAdmins(CouchDbConnector db) {
        super(FacilityAdmin.class, db);
    }
}
