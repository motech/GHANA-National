package org.motechproject.ghana.national.repository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.national.domain.Enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllEnrollment extends MotechAuditableRepository<Enrollment> {

    @Autowired
    public AllEnrollment(@Qualifier("couchDbConnector") CouchDbConnector db) {
        super(Enrollment.class, db);
    }

    @View(name = "find_by_motech_patient_id", map = "function(doc) { if(doc.type === 'Enrollment') emit(doc.patientId, doc); }")
    public Enrollment findBy(String patientId) {
        if (StringUtils.isEmpty(patientId)) return null;
        ViewQuery viewQuery = createQuery("find_by_motech_patient_id").key(patientId).includeDocs(true);
        List<Enrollment> enrollments = db.queryView(viewQuery, Enrollment.class);
        return CollectionUtils.isEmpty(enrollments) ? null : enrollments.get(0);
    }
}
