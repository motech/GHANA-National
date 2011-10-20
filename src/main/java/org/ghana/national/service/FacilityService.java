package org.ghana.national.service;

import org.apache.commons.lang.StringUtils;
import org.ghana.national.domain.Facility;
import org.ghana.national.exception.FacilityAlreadyFoundException;
import org.ghana.national.repository.AllFacilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class FacilityService {
    @Autowired
    private MessageSource messageSource;

    private AllFacilities allFacilities;

    @Autowired
    public FacilityService(AllFacilities allFacilities) {
        this.allFacilities = allFacilities;
    }

    public void create(String name, String country, String region, String district, String province) throws FacilityAlreadyFoundException {
        final List<Facility> facilities = allFacilities.facilitiesByName(name);
        if (isIn(facilities, new org.motechproject.mrs.model.Facility(name, country, region, district, province))) {
            throw new FacilityAlreadyFoundException(messageSource.getMessage("facility_already_exists", null, Locale.getDefault()));
        }
        final Facility facility = new Facility(new org.motechproject.mrs.model.Facility(name, country, region, district, province));
        allFacilities.add(facility);
    }

    private boolean isDuplicate(org.motechproject.mrs.model.Facility thatMrsFacility, org.motechproject.mrs.model.Facility mrsFacility) {
        return (thatMrsFacility != null
                && StringUtils.equals(thatMrsFacility.getName(), mrsFacility.getName())
                && StringUtils.equals(thatMrsFacility.getStateProvince(), mrsFacility.getStateProvince())
                && StringUtils.equals(thatMrsFacility.getCountyDistrict(), mrsFacility.getCountyDistrict())
                && StringUtils.equals(thatMrsFacility.getRegion(), mrsFacility.getRegion())
                && StringUtils.equals(thatMrsFacility.getCountry(), mrsFacility.getCountry()));
    }

    private boolean isIn(List<Facility> facilities, org.motechproject.mrs.model.Facility mrsFacility) {
        for (Facility facility : facilities) {
            if (this.isDuplicate(facility.mrsFacility(), mrsFacility)) {
                return true;
            }
        }
        return false;
    }

    public List<Facility> facilities() {
        return allFacilities.facilities();
    }
}
