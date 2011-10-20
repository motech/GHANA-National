package org.ghana.national.web;

import ch.lambdaj.group.Group;
import org.apache.commons.lang.StringUtils;
import org.ghana.national.domain.Facility;
import org.ghana.national.exception.FacilityAlreadyFoundException;
import org.ghana.national.service.FacilityService;
import org.ghana.national.tools.Constants;
import org.ghana.national.tools.Utility;
import org.ghana.national.web.form.CreateFacilityForm;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
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

    final static String NEW_FACILITY = "common/facilities/new";
    final static String SUCCESS = "common/facilities/success";

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newFacilityForm(ModelMap modelMap) {
        populateFacilityData(facilityService.facilities(), modelMap);
        return NEW_FACILITY;
    }

    @ApiSession
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createFacility(@Valid CreateFacilityForm createFacilityForm, BindingResult bindingResult, ModelMap modelMap) {
        try {
            facilityService.create(createFacilityForm.getName(), createFacilityForm.getCountry(), createFacilityForm.getRegion(),
                    createFacilityForm.getCountyDistrict(), createFacilityForm.getStateProvince());
            populateFacilityData(facilityService.facilities(), modelMap);
        } catch (FacilityAlreadyFoundException e) {
            handleExistingFacilityError(bindingResult, modelMap, e.getMessage());
            return NEW_FACILITY;
        }
        return SUCCESS;
    }

    private void handleExistingFacilityError(BindingResult bindingResult, ModelMap modelMap, String message) {
        bindingResult.addError(new FieldError(Constants.CREATE_FACILITY_FORM, "name", message));
        modelMap.mergeAttributes(bindingResult.getModel());
        populateFacilityData(facilityService.facilities(), modelMap);
    }

    ModelMap populateFacilityData(List<Facility> facilities, ModelMap modelMap) {
        List<Facility> withValidCountryNames = select(facilities, having(on(Facility.class).country(), is(not(equalTo(StringUtils.EMPTY)))));
        final Group<Facility> byCountryRegion = group(withValidCountryNames, by(on(Facility.class).country()), by(on(Facility.class).region()));
        final Group<Facility> byRegionDistrict = group(withValidCountryNames, by(on(Facility.class).region()), by(on(Facility.class).district()));
        final Group<Facility> byDistrictProvince = group(withValidCountryNames, by(on(Facility.class).district()), by(on(Facility.class).province()));

        modelMap.addAttribute(Constants.CREATE_FACILITY_FORM, new CreateFacilityForm());
        modelMap.addAttribute(Constants.COUNTRIES, extract(selectDistinct(withValidCountryNames, "country"), on(Facility.class).country()));
        modelMap.addAttribute(Constants.REGIONS, Utility.reverseKeyValues(map(byCountryRegion.keySet(), Utility.mapConverter(byCountryRegion))));
        modelMap.addAttribute(Constants.DISTRICTS, Utility.reverseKeyValues(map(byRegionDistrict.keySet(), Utility.mapConverter(byRegionDistrict))));
        modelMap.addAttribute(Constants.PROVINCES, Utility.reverseKeyValues(map(byDistrictProvince.keySet(), Utility.mapConverter(byDistrictProvince))));
        return modelMap;
    }
}
