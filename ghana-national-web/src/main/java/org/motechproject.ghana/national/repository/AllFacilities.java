package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.mrs.services.MRSFacilityAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllFacilities extends MotechAuditableRepository<Facility> {
    private MRSFacilityAdaptor facilityAdaptor;

    @Autowired
    public AllFacilities(@Qualifier("couchDbConnector") CouchDbConnector db, MRSFacilityAdaptor facilityAdaptor) {
        super(Facility.class, db);
        this.facilityAdaptor = facilityAdaptor;
    }

    @Override
    public void add(Facility facility) {
        final org.motechproject.mrs.model.Facility savedFacility = facilityAdaptor.saveFacility(facility.mrsFacility());
        facility.mrsFacilityId(Integer.parseInt(savedFacility.getId()));
        super.add(facility);
    }

    public List<Facility> facilitiesByName(String name) {
        final List<org.motechproject.mrs.model.Facility> mrsFacilities = facilityAdaptor.getFacilities(name);
        final ArrayList<Facility> facilities = new ArrayList<Facility>();
        for (org.motechproject.mrs.model.Facility mrsFacility : mrsFacilities) {
            final Facility facility = new Facility(mrsFacility);
            facilities.add(facility);
        }
        return facilities;
    }

    public List<Facility> facilities() {
        final List<org.motechproject.mrs.model.Facility> mrsFacilities = facilityAdaptor.getFacilities();
        final ArrayList<Facility> facilities = new ArrayList<Facility>();
        for (org.motechproject.mrs.model.Facility mrsFacility : mrsFacilities) {
            final Facility facility = new Facility(mrsFacility);
            facilities.add(facility);
        }
        return facilities;
    }
}
