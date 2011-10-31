package org.motechproject.ghana.national.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.mrs.services.MRSFacilityAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
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
        return getFacilitiesWithAllinfo(facilityAdaptor.getFacilities(name));
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

    private List<Facility> getFacilitiesWithAllinfo(List<org.motechproject.mrs.model.Facility> mrsFacilities) {
        final List<Facility> facilities = this.getAll();
        for (org.motechproject.mrs.model.Facility mrsFacility : mrsFacilities) {
            List<Facility> facilitiesWithPhoneNumber = select(facilities, having(on(Facility.class).mrsFacilityId(), is(equalTo(Integer.valueOf(mrsFacility.getId())))));
            if (facilitiesWithPhoneNumber.size() == 1)
                facilities.add(facilitiesWithPhoneNumber.get(0).mrsFacility(mrsFacility));
        }
        return facilities;
    }


}
