package org.ghana.national.service;

import org.apache.commons.lang.StringUtils;
import org.ghana.national.domain.Facility;
import org.ghana.national.exception.FacilityAlreadyFoundException;
import org.ghana.national.repository.AllFacilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FacilityService {

    private AllFacilities allFacilities;

    @Autowired
    public FacilityService(AllFacilities allFacilities) {
        this.allFacilities = allFacilities;
    }

    public void create(String name, String country, String region, String district, String province, String phoneNumber, String additionalPhoneNumber1, String additionalPhoneNumber2, String additionalPhoneNumber3) throws FacilityAlreadyFoundException {
        final List<Facility> facilities = allFacilities.facilitiesByName(name);
        final org.motechproject.mrs.model.Facility mrsFacility = new org.motechproject.mrs.model.Facility(name, country, region, district, province);
        if (isDuplicate(facilities, mrsFacility)) {
            throw new FacilityAlreadyFoundException();
        }
        final Facility facility = new Facility(mrsFacility).phoneNumber(phoneNumber).additionalPhoneNumber1(additionalPhoneNumber1).
                additionalPhoneNumber2(additionalPhoneNumber2).additionalPhoneNumber3(additionalPhoneNumber3);
        allFacilities.add(facility);
    }

    private boolean isDuplicate(List<Facility> facilities, org.motechproject.mrs.model.Facility mrsFacility) {
        for (Facility facility : facilities) {
            org.motechproject.mrs.model.Facility thatMrsFacility = facility.mrsFacility();
            if ((thatMrsFacility != null
                    && StringUtils.equals(thatMrsFacility.getName(), mrsFacility.getName())
                    && StringUtils.equals(thatMrsFacility.getStateProvince(), mrsFacility.getStateProvince())
                    && StringUtils.equals(thatMrsFacility.getCountyDistrict(), mrsFacility.getCountyDistrict())
                    && StringUtils.equals(thatMrsFacility.getRegion(), mrsFacility.getRegion())
                    && StringUtils.equals(thatMrsFacility.getCountry(), mrsFacility.getCountry()))) {
                return true;
            }
        }
        return false;
    }

    public List<Facility> facilities() {
        return allFacilities.facilities();
    }
}
