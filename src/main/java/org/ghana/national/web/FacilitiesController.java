package org.ghana.national.web;

import ch.lambdaj.group.Group;
import org.apache.commons.lang.StringUtils;
import org.ghana.national.tools.Constants;
import org.ghana.national.tools.Utility;
import org.ghana.national.web.form.CreateFacilityForm;
import org.motechproject.mrs.services.Facility;
import org.motechproject.mrs.services.FacilityService;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

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
    MessageSource messageSource;

    @Autowired
    FacilityService facilityService;

    final static String NEW_FACILITY = "common/facilities/new";
    final static String SUCCESS = "common/facilities/success";

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newFacilityForm(ModelMap modelMap) {
        populateLocation(facilityService.getFacilities(), modelMap);
        return NEW_FACILITY;
    }

    ModelMap populateLocation(List<Facility> facilities, ModelMap modelMap) {
        List<Facility> withValidCountryNames = select(facilities, having(on(Facility.class).getCountry(), is(not(equalTo(StringUtils.EMPTY)))));
        final Group<Facility> byCountryRegion = group(withValidCountryNames, by(on(Facility.class).getCountry()), by(on(Facility.class).getRegion()));
        final Group<Facility> byRegionDistrict = group(withValidCountryNames, by(on(Facility.class).getRegion()), by(on(Facility.class).getCountyDistrict()));
        final Group<Facility> byDistrictProvince = group(withValidCountryNames, by(on(Facility.class).getCountyDistrict()), by(on(Facility.class).getStateProvince()));

        modelMap.addAttribute(Constants.CREATE_FACILITY_FORM, new CreateFacilityForm());
        modelMap.addAttribute(Constants.COUNTRIES, extract(selectDistinct(withValidCountryNames, "country"), on(Facility.class).getCountry()));
        modelMap.addAttribute(Constants.REGIONS, Utility.reverseKeyValues(map(byCountryRegion.keySet(), Utility.mapConverter(byCountryRegion))));
        modelMap.addAttribute(Constants.DISTRICTS, Utility.reverseKeyValues(map(byRegionDistrict.keySet(), Utility.mapConverter(byRegionDistrict))));
        modelMap.addAttribute(Constants.PROVINCES, Utility.reverseKeyValues(map(byDistrictProvince.keySet(), Utility.mapConverter(byDistrictProvince))));
        return modelMap;
    }

    @ApiSession
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createFacilityForm(@Valid CreateFacilityForm createFacilityForm, BindingResult bindingResult, ModelMap modelMap) {
        final List<Facility> facilities = facilityService.getFacilities(createFacilityForm.getName());
        populateLocation(facilityService.getFacilities(), modelMap);
        if (!createFacilityForm.isIn(facilities)) {
            facilityService.saveFacility(new Facility(createFacilityForm.getName(), createFacilityForm.getCountry(),
                    createFacilityForm.getRegion(), createFacilityForm.getCountyDistrict(), createFacilityForm.getStateProvince()));
            return SUCCESS;
        }

        bindingResult.addError(new FieldError(Constants.CREATE_FACILITY_FORM, "name", messageSource.getMessage("facility_already_exists", null, Locale.getDefault())));
        modelMap.mergeAttributes(bindingResult.getModel());
        return NEW_FACILITY;
    }
}
