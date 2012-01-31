package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.national.domain.SMSAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllSMS extends MotechBaseRepository<SMSAudit> {

    @Autowired
    protected AllSMS(@Qualifier("couchDbConnector") CouchDbConnector db) {
        super(SMSAudit.class, db);
    }
}
