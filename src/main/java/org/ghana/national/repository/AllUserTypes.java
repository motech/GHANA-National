package org.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.ghana.national.domain.UserType;
import org.motechproject.dao.MotechAuditableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllUserTypes extends MotechAuditableRepository<UserType> {
    @Autowired
    protected AllUserTypes(@Qualifier("couchDbConnector") CouchDbConnector db) {
        super(UserType.class, db);
    }
}
