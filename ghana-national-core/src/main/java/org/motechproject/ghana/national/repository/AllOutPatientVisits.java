package org.motechproject.ghana.national.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.ghana.national.domain.OutPatientVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;


@Repository
public class AllOutPatientVisits extends MotechBaseRepository<OutPatientVisit> {
    private AllMotechModuleOutPatientVisits allMotechModuleOutPatientVisits;

    @Autowired
    public AllOutPatientVisits(@Qualifier("couchDbConnector") CouchDbConnector db,
                               AllMotechModuleOutPatientVisits allMotechModuleOutPatientVisits) {
        super(OutPatientVisit.class, db);
        this.allMotechModuleOutPatientVisits = allMotechModuleOutPatientVisits;
    }

    @Override
    public void add(OutPatientVisit outPatientVisit) {
        List<OutPatientVisit> existingOutPatients = findBy(outPatientVisit.getFacilityId(), outPatientVisit.getStaffId(), outPatientVisit.getSerialNumber(), outPatientVisit.getVisitDate());
        OutPatientVisit existingOutPatientVisit = selectFirst(existingOutPatients, having(on(OutPatientVisit.class), equalTo(outPatientVisit)));
        if (existingOutPatientVisit == null) {
            super.add(outPatientVisit);
            allMotechModuleOutPatientVisits.save(outPatientVisit);
        }
    }

    @View(name = "find_by_facility_id_staff_id_serialNo_visitDate",
            map = "function(doc) { if(doc.type === 'OutPatientVisit') emit([doc.facilityId, doc.staffId, doc.serialNumber, doc.visitDate], doc) }")
    List<OutPatientVisit> findBy(String facilityId, String staffId, String serialNo, Date visitDate) {
        ViewQuery viewQuery = createQuery("find_by_facility_id_staff_id_serialNo_visitDate").key(
                ComplexKey.of(facilityId, staffId, serialNo, visitDate));
        return db.queryView(viewQuery, OutPatientVisit.class);
    }

    public void migrateToCouch(OutPatientVisit outPatientVisit) {
        super.add(outPatientVisit);
    }
}
