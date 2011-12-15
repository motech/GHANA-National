package org.motechproject.ghana.national.tools.seed.util;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CouchDB {
    @Autowired
    private CouchDbInstance couchDbInstance;
    @Autowired
    private CouchDbConnector dbConnector;

    public void recreate() {
        String dbName = dbConnector.getDatabaseName();
        couchDbInstance.deleteDatabase(dbName);
        couchDbInstance.createDatabase(dbName);
    }
}