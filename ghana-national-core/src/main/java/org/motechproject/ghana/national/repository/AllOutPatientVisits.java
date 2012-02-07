package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;


@Repository
public class AllOutPatientVisits extends MotechBaseRepository<OutPatientVisit> {
     @Autowired
    public AllOutPatientVisits(@Qualifier("couchDbConnector") CouchDbConnector db) {
        super(OutPatientVisit.class, db);
    }
}
