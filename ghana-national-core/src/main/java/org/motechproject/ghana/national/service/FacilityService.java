package org.motechproject.ghana.national.service;

import ch.lambdaj.group.Group;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.exception.FacilityAlreadyFoundException;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.tools.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.map;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectDistinct;
import static ch.lambdaj.group.Groups.by;
import static ch.lambdaj.group.Groups.group;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;

@Service
public class FacilityService {

    private AllFacilities allFacilities;

    @Autowired
    public FacilityService(AllFacilities allFacilities) {
        this.allFacilities = allFacilities;
    }

    public void create(String name, String country, String region, String district, String province, String phoneNumber, String additionalPhoneNumber1, String additionalPhoneNumber2, String additionalPhoneNumber3) throws FacilityAlreadyFoundException {
        final List<Facility> facilities = allFacilities.facilitiesByName(name);
        final org.motechproject.mrs.model.Facility mrsFacility = new org.motechproject.mrs.model.Facility(name, country, region, district, province);
        if (isDuplicate(facilities, mrsFacility, phoneNumber)) {
            throw new FacilityAlreadyFoundException();
        }
        final Facility facility = new Facility(mrsFacility).phoneNumber(phoneNumber).additionalPhoneNumber1(additionalPhoneNumber1).
                additionalPhoneNumber2(additionalPhoneNumber2).additionalPhoneNumber3(additionalPhoneNumber3);
        allFacilities.add(facility);
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

    public List<Facility> facilities() {
        return allFacilities.facilities();
    }

    public Map<String, Object> locationMap() {
        List<Facility> facilities = facilities();

        final HashMap<String, String> facilityMap = new HashMap<String, String>();
        for (Facility facility : facilities) {
            if (facility.province() != null) {
                facilityMap.put(facility.name(), facility.province());
                continue;
            }
            if (facility.district() != null) {
                facilityMap.put(facility.name(), facility.district());
                continue;
            }
            if (facility.region() != null) {
                facilityMap.put(facility.name(), facility.region());
            }
        }

        final HashMap<String, Object> modelMap = new HashMap<String, Object>();
        List<Facility> withValidCountryNames = select(facilities, having(on(Facility.class).country(), is(not(equalTo(StringUtils.EMPTY)))));
        final Group<Facility> byCountryRegion = group(withValidCountryNames, by(on(Facility.class).country()), by(on(Facility.class).region()));
        final Group<Facility> byRegionDistrict = group(withValidCountryNames, by(on(Facility.class).region()), by(on(Facility.class).district()));
        final Group<Facility> byDistrictProvince = group(withValidCountryNames, by(on(Facility.class).district()), by(on(Facility.class).province()));

        modelMap.put(Constants.COUNTRIES, extract(selectDistinct(withValidCountryNames, "country"), on(Facility.class).country()));
        modelMap.put(Constants.REGIONS, Utility.reverseKeyValues(map(byCountryRegion.keySet(), Utility.mapConverter(byCountryRegion))));
        modelMap.put(Constants.DISTRICTS, Utility.reverseKeyValues(map(byRegionDistrict.keySet(), Utility.mapConverter(byRegionDistrict))));
        modelMap.put(Constants.PROVINCES, Utility.reverseKeyValues(map(byDistrictProvince.keySet(), Utility.mapConverter(byDistrictProvince))));
        modelMap.put("facilities", facilityMap);
        return modelMap;
    }
}
