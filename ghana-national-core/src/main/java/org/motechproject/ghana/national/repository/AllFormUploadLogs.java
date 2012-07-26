package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.national.domain.FormUploadLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllFormUploadLogs extends MotechBaseRepository<FormUploadLog> {

    @Autowired
    public AllFormUploadLogs(@Qualifier("auditLogDbConnector") CouchDbConnector db) {
        super(FormUploadLog.class, db);
    }
}
