package org.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.ghana.national.domain.Facility;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllFacilities extends MotechAuditableRepository<Facility> {
    private MRSFacilityAdapter facilityAdapter;

    @Autowired
    public AllFacilities(@Qualifier("couchDbConnector") CouchDbConnector db, MRSFacilityAdapter facilityAdapter) {
        super(Facility.class, db);
        this.facilityAdapter = facilityAdapter;
    }

    @Override
    public void add(Facility facility) {
        facilityAdapter.saveFacility(facility.mrsFacility());
        super.add(facility);
    }

    public List<Facility> facilitiesByName(String name) {
        final List<org.motechproject.mrs.model.Facility> mrsFacilities = facilityAdapter.getFacilities(name);
        final ArrayList<Facility> facilities = new ArrayList<Facility>();
        for (org.motechproject.mrs.model.Facility mrsFacility : mrsFacilities) {
            final Facility facility = new Facility(mrsFacility);
            facilities.add(facility);
        }
        return facilities;
    }

    public List<Facility> facilities() {
        final List<org.motechproject.mrs.model.Facility> mrsFacilities = facilityAdapter.getFacilities();
        final ArrayList<Facility> facilities = new ArrayList<Facility>();
        for (org.motechproject.mrs.model.Facility mrsFacility : mrsFacilities) {
            final Facility facility = new Facility(mrsFacility);
            facilities.add(facility);
        }
        return facilities;
    }
}
