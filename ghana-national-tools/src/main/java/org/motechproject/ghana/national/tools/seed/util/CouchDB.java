package org.motechproject.ghana.national.tools.seed.util;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CouchDB {
    @Autowired
    private CouchDbInstance couchDbInstance;
    @Autowired
    @Qualifier("couchDbConnector")
    private CouchDbConnector dbConnector;
    @Autowired
    @Qualifier("cmsLiteDatabase")
    private CouchDbConnector cmsLiteDatabase;

    public void recreate() {
        recreate(dbConnector.getDatabaseName());
        recreate(cmsLiteDatabase.getDatabaseName());
    }

    private void recreate(String dbName) {
        couchDbInstance.deleteDatabase(dbName);
        couchDbInstance.createDatabase(dbName);
    }
}