package org.motechproject.ghana.national.repository;

import org.apache.commons.collections.CollectionUtils;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllMobileMidwifeEnrollments extends MotechBaseRepository<MobileMidwifeEnrollment> {

    @Autowired
    public AllMobileMidwifeEnrollments(@Qualifier("couchDbConnector") CouchDbConnector db) {
        super(MobileMidwifeEnrollment.class, db);
    }

    public void createOrUpdate(MobileMidwifeEnrollment enrollment) {
        if(enrollment.getId() == null)
            super.add(enrollment);
        else super.update(enrollment);
    }
    
    @View(name = "find_by_patientId", map = "function(doc){ if(doc.type === 'MobileMidwifeEnrollment') emit([doc.patientId, doc.active],doc) }")
    public MobileMidwifeEnrollment findByPatientId(String patientId) {
        ViewQuery viewQuery = createQuery("find_by_patientId").key(ComplexKey.of(patientId, true)).includeDocs(true);
        List<MobileMidwifeEnrollment> enrollments = db.queryView(viewQuery, MobileMidwifeEnrollment.class);
        return CollectionUtils.isEmpty(enrollments) ? null : enrollments.get(0);
    }
}
