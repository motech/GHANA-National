package org.ghana.national.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mrs.services.Facility;
import org.motechproject.mrs.services.FacilityService;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Controller
@RequestMapping(value = "/admin/facilities")
public class FacilitiesController {

    @Autowired
    FacilityService facilityService;

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newFacilityForm() {
        return "admin/facilities/new";
    }

    private void populateLocation(List<Facility> facilities) {
        List<String> countries = extract(facilities, on(Facility.class).getCountry());
        Map<String, TreeSet<String>> regions = new HashMap<String, TreeSet<String>>();
        Map<String, TreeSet<String>> districts = new HashMap<String, TreeSet<String>>();
        Map<String, TreeSet<String>> provinces = new HashMap<String, TreeSet<String>>();
        for (Facility facility : facilities) {
            populate(regions, facility.getCountry(), facility.getRegion());
            populate(districts, facility.getRegion(), facility.getCountyDistrict());
            populate(provinces, facility.getCountyDistrict(), facility.getStateProvince());
        }
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("countries", countries);
        modelMap.addAttribute("regions", regions);
        modelMap.addAttribute("districts", districts);
        modelMap.addAttribute("provinces", provinces);
    }

    private void populate(Map<String, TreeSet<String>> map, String key, String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) return;
        if (map.containsKey(key)) {
            map.get(key).add(value);
            return;
        }
        TreeSet<String> values = new TreeSet<String>();
        values.add(value);
        map.put(key, values);
    }
}
