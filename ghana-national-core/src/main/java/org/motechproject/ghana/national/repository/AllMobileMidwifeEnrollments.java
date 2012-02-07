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

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

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
    
    @View(name = "find_active_by_patientId", map = "function(doc){ if(doc.type === 'MobileMidwifeEnrollment') emit([doc.patientId, doc.active],doc) }")
    public MobileMidwifeEnrollment findActiveByPatientId(String patientId) {
        ViewQuery viewQuery = createQuery("find_active_by_patientId").key(ComplexKey.of(patientId, true)).includeDocs(true);
        List<MobileMidwifeEnrollment> enrollments = db.queryView(viewQuery, MobileMidwifeEnrollment.class);
        return CollectionUtils.isEmpty(enrollments) ? null : enrollments.get(0);
    }

    @View(name = "find_by_patientId", map = "function(doc){ if(doc.type === 'MobileMidwifeEnrollment') emit([doc.patientId], doc) }")
    public MobileMidwifeEnrollment findByPatientId(String patientId) {
        ViewQuery viewQuery = createQuery("find_by_patientId").key(ComplexKey.of(patientId)).includeDocs(true);
        List<MobileMidwifeEnrollment> enrollments = db.queryView(viewQuery, MobileMidwifeEnrollment.class);
        final List<MobileMidwifeEnrollment> sortedEnrollments = sort(enrollments,
                on(MobileMidwifeEnrollment.class).getEnrollmentDateTime());

        return CollectionUtils.isEmpty(enrollments) ? null : enrollments.get(sortedEnrollments.size()-1);
    }
}
