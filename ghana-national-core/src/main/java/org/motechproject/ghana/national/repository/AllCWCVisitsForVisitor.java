package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.national.domain.CWCVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCWCVisitsForVisitor extends MotechBaseRepository<CWCVisit> {

    @Autowired
    public AllCWCVisitsForVisitor(@Qualifier("couchDbConnector") CouchDbConnector db) {
        super(CWCVisit.class, db);
    }
}
