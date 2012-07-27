package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.national.domain.FormUploadLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllFormUploadLogs extends MotechBaseRepository<FormUploadLog> {

    @Autowired
    public AllFormUploadLogs(@Qualifier("auditLogDbConnector") CouchDbConnector db) {
        super(FormUploadLog.class, db);
    }

    @View(name = "getAllFormUploads", map = "function(doc) { emit(doc.id, doc); }")
    public List<FormUploadLog> getAll() {
        return queryView("getAllFormUploads");
    }

}
