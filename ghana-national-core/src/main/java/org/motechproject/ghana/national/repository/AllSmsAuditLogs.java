package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.national.domain.SmsAuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllSmsAuditLogs extends MotechBaseRepository<SmsAuditLog> {

    @Autowired
    public AllSmsAuditLogs(@Qualifier("auditLogDbConnector") CouchDbConnector db) {
        super(SmsAuditLog.class, db);
    }
}
