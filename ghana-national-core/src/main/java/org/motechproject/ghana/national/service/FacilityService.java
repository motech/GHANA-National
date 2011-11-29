package org.motechproject.ghana.national.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.exception.FacilityAlreadyFoundException;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.tools.StartsWithMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.lambdaj.Lambda.*;

@Service
public class FacilityService {

    private AllFacilities allFacilities;
    private IdentifierGenerationService identifierGenerationService;

    @Autowired
    public FacilityService(AllFacilities allFacilities, IdentifierGenerationService identifierGenerationService) {
        this.allFacilities = allFacilities;
        this.identifierGenerationService = identifierGenerationService;
    }

    public String create(String name, String country, String region, String district, String province, String phoneNumber, String additionalPhoneNumber1, String additionalPhoneNumber2, String additionalPhoneNumber3) throws FacilityAlreadyFoundException {
        final List<Facility> facilities = allFacilities.facilitiesByName(name);
        final org.motechproject.mrs.model.Facility mrsFacility = new org.motechproject.mrs.model.Facility(name, country, region, district, province);
        if (isDuplicate(facilities, mrsFacility, phoneNumber)) {
            throw new FacilityAlreadyFoundException();
        }
        final Facility facility = new Facility(mrsFacility).phoneNumber(phoneNumber).additionalPhoneNumber1(additionalPhoneNumber1).
                additionalPhoneNumber2(additionalPhoneNumber2).additionalPhoneNumber3(additionalPhoneNumber3).motechId(Integer.parseInt(identifierGenerationService.newFacilityId()));
        allFacilities.add(facility);
        return String.valueOf(facility.mrsFacilityId());
    }

    public List<Facility> facilities() {
        return allFacilities.facilities();
    }

    public List<Facility> searchFacilities(String facilityId, String name, String country, String region, String district, String province) {
        List<Facility> filteredFacilities = facilities();
        filteredFacilities = filterFacilities(on(Facility.class).getMrsFacility().getCountry(), country, filteredFacilities);
        filteredFacilities = filterFacilities(on(Facility.class).getMrsFacility().getRegion(), region, filteredFacilities);
        filteredFacilities = filterFacilities(on(Facility.class).getMrsFacility().getCountyDistrict(), district, filteredFacilities);
        filteredFacilities = filterFacilities(on(Facility.class).getMrsFacility().getStateProvince(), province, filteredFacilities);
        filteredFacilities = filterFacilities(on(Facility.class).getMrsFacility().getName(), name, filteredFacilities);
        filteredFacilities = filterFacilities(on(Facility.class).getMrsFacility().getId(), facilityId, filteredFacilities);
        return filteredFacilities;
    }

    private boolean isDuplicate(List<Facility> facilities, org.motechproject.mrs.model.Facility mrsFacility, String phoneNumber) {
        for (Facility facility : facilities) {
            org.motechproject.mrs.model.Facility thatMrsFacility = facility.mrsFacility();
            boolean isFacilityNotUnique = false;
            if ((thatMrsFacility != null
                    && StringUtils.equals(thatMrsFacility.getName(), mrsFacility.getName())
                    && StringUtils.equals(thatMrsFacility.getStateProvince(), mrsFacility.getStateProvince())
                    && StringUtils.equals(thatMrsFacility.getCountyDistrict(), mrsFacility.getCountyDistrict())
                    && StringUtils.equals(thatMrsFacility.getRegion(), mrsFacility.getRegion())
                    && StringUtils.equals(thatMrsFacility.getCountry(), mrsFacility.getCountry()))) {
                isFacilityNotUnique = true;
            }
            if (isFacilityNotUnique || StringUtils.equals(facility.phoneNumber(), phoneNumber)) {
                return true;
            }
        }
        return false;
    }

    private List<Facility> filterFacilities(String field, String matcher, List<Facility> filteredFacilities) {
        return (StringUtils.isNotEmpty(matcher)) ? filter(having(field, StartsWithMatcher.ignoreCaseStartsWith(matcher)), filteredFacilities) : filteredFacilities;
    }

    public Facility getFacility(String facilityId) {
        return allFacilities.getFacility(facilityId);
    }
}