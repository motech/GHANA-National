package org.motechproject.ghana.national.repository;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.tools.Utility;
import org.motechproject.mrs.services.MRSFacilityAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.core.Is.is;

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
        return getFacilitiesWithAllInfo(facilityAdaptor.getFacilities(name));
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

    private List<Facility> getFacilitiesWithAllInfo(List<org.motechproject.mrs.model.Facility> mrsFacilities) {
        final List<String> facilityIdsAsString = extract(mrsFacilities, on(org.motechproject.mrs.model.Facility.class).getId());
        final List<Facility> facilities = findByFacilityIds(convert(facilityIdsAsString, Utility.stringToIntegerConverter()));

        return convert(mrsFacilities, new Converter<org.motechproject.mrs.model.Facility, Facility>() {
            @Override
            public Facility convert(org.motechproject.mrs.model.Facility mrsFacility) {
                final Facility facility = (Facility) selectUnique(facilities, having(on(Facility.class).mrsFacilityId(),
                        is(Integer.valueOf(mrsFacility.getId()))));
                return (facility != null) ? facility.mrsFacility(mrsFacility) : new Facility(mrsFacility);
            }
        });
    }

    public Facility getFacility(String mrsFacilityId) {
        org.motechproject.mrs.model.Facility mrsFacility = facilityAdaptor.getFacility(Integer.parseInt(mrsFacilityId));
        return (mrsFacility != null) ? findByMrsFacilityId(Integer.parseInt(mrsFacilityId)).mrsFacility(mrsFacility) : null;
    }

    @View(name = "find_by_facility_ids", map = "function(doc) { if(doc.type === 'Facility') emit(doc.mrsFacilityId, doc) }")
    public List<Facility> findByFacilityIds(List<Integer> facilityIds) {
        ViewQuery viewQuery = createQuery("find_by_facility_ids").keys(facilityIds);
        return db.queryView(viewQuery, Facility.class);
    }

    @View(name = "find_by_mrs_facility_id", map = "function(doc) { if(doc.type === 'Facility') emit(doc.mrsFacilityId, doc) }")
    public Facility findByMrsFacilityId(Integer facilityId) {
        ViewQuery viewQuery = createQuery("find_by_mrs_facility_id").key(facilityId).includeDocs(true);
        List<Facility> facilities = db.queryView(viewQuery, Facility.class);
        return CollectionUtils.isEmpty(facilities) ? null : facilities.get(0);
    }
}
