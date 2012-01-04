package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.national.domain.MotechProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllMotechProgram extends MotechAuditableRepository<MotechProgram> {

    @Autowired
    public AllMotechProgram(@Qualifier("couchDbConnector") CouchDbConnector db) {
        super(MotechProgram.class,db);
    }
}
