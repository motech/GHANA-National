package org.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.ghana.national.domain.Facility;
import org.motechproject.dao.MotechAuditableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class FacilityRepository extends MotechAuditableRepository<Facility> {

    @Autowired
    protected FacilityRepository(@Qualifier("couchDbConnector") CouchDbConnector db) {
        super(Facility.class, db);
    }
}
