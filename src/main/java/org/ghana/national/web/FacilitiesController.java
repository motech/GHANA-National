package org.ghana.national.web;

import ch.lambdaj.group.Group;
import org.apache.commons.lang.StringUtils;
import org.ghana.national.tools.Utility;
import org.ghana.national.web.form.CreateFacilityForm;
import org.motechproject.mrs.services.Facility;
import org.motechproject.mrs.services.FacilityService;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

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

@Controller
@RequestMapping(value = "/admin/facilities")
public class FacilitiesController {

    @Autowired
    FacilityService facilityService;

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newFacilityForm(ModelMap modelMap) {
        populateLocation(facilityService.getFacilities(), modelMap);
        return "common/facilities/new";
    }

    ModelMap populateLocation(List<Facility> facilities, ModelMap modelMap) {
        List<Facility> withValidCountryNames = select(facilities, having(on(Facility.class).getCountry(), is(not(equalTo(StringUtils.EMPTY)))));
        final Group<Facility> byCountryRegion = group(withValidCountryNames, by(on(Facility.class).getCountry()), by(on(Facility.class).getRegion()));
        final Group<Facility> byRegionDistrict = group(withValidCountryNames, by(on(Facility.class).getRegion()), by(on(Facility.class).getCountyDistrict()));
        final Group<Facility> byDistrictProvince = group(withValidCountryNames, by(on(Facility.class).getCountyDistrict()), by(on(Facility.class).getStateProvince()));

        modelMap.addAttribute("facility", new CreateFacilityForm());
        modelMap.addAttribute("countries", extract(selectDistinct(withValidCountryNames, "country"), on(Facility.class).getCountry()));
        modelMap.addAttribute("regions", Utility.reverseKeyValues(map(byCountryRegion.keySet(), Utility.mapConverter(byCountryRegion))));
        modelMap.addAttribute("districts", Utility.reverseKeyValues(map(byRegionDistrict.keySet(), Utility.mapConverter(byRegionDistrict))));
        modelMap.addAttribute("provinces", Utility.reverseKeyValues(map(byDistrictProvince.keySet(), Utility.mapConverter(byDistrictProvince))));
        return modelMap;
    }
}
