package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.exception.FacilityAlreadyFoundException;
import org.motechproject.ghana.national.helper.FacilityHelper;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.web.form.CreateFacilityForm;
import org.motechproject.ghana.national.web.form.SearchFacilityForm;
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

@Controller
@RequestMapping(value = "/admin/facilities")
public class FacilitiesController {
    public static final String CREATE_FACILITY_FORM = "createFacilityForm";
    public static final String SEARCH_FACILITY_FORM = "searchFacilityForm";
    public static final String SUCCESS = "facilities/success";
    public static final String NEW_FACILITY = "facilities/new";
    public static final String SEARCH_FACILITY = "facilities/search";

    private FacilityService facilityService;
    private MessageSource messageSource;
    private FacilityHelper facilityHelper;

    public FacilitiesController() {
    }

    @Autowired
    public FacilitiesController(FacilityService facilityService, MessageSource messageSource, FacilityHelper facilityHelper) {
        this.facilityService = facilityService;
        this.messageSource = messageSource;
        this.facilityHelper = facilityHelper;
    }

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newFacilityForm(ModelMap modelMap) {
        modelMap.put(CREATE_FACILITY_FORM, new CreateFacilityForm());
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return NEW_FACILITY;
    }

    @ApiSession
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createFacility(@Valid CreateFacilityForm createFacilityForm, BindingResult bindingResult, ModelMap modelMap) {
        try {
            facilityService.create(createFacilityForm.getName(), createFacilityForm.getCountry(), createFacilityForm.getRegion(),
                    createFacilityForm.getCountyDistrict(), createFacilityForm.getStateProvince(), createFacilityForm.getPhoneNumber(),
                    createFacilityForm.getAdditionalPhoneNumber1(), createFacilityForm.getAdditionalPhoneNumber2(),
                    createFacilityForm.getAdditionalPhoneNumber3());
            modelMap.mergeAttributes(facilityHelper.locationMap());
        } catch (FacilityAlreadyFoundException e) {
            handleExistingFacilityError(bindingResult, modelMap, messageSource.getMessage("facility_already_exists", null, Locale.getDefault()));
            return NEW_FACILITY;
        }
        return SUCCESS;
    }

    @ApiSession
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String searchFacilityForm(ModelMap modelMap) {
        modelMap.put(SEARCH_FACILITY_FORM, new SearchFacilityForm());
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return SEARCH_FACILITY;
    }

    @ApiSession
    @RequestMapping(value = "searchFacilities", method = RequestMethod.POST)
    public String searchFacility(@Valid final SearchFacilityForm searchFacilityForm, ModelMap modelMap) {
        final List<Facility> requestedFacilities = facilityService.searchFacilities(searchFacilityForm.getFacilityID(),
                searchFacilityForm.getName(), searchFacilityForm.getCountry(), searchFacilityForm.getRegion(),
                searchFacilityForm.getCountyDistrict(), searchFacilityForm.getStateProvince());
        modelMap.put("requestedFacilities", requestedFacilities);
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return SEARCH_FACILITY;
    }

    private void handleExistingFacilityError(BindingResult bindingResult, ModelMap modelMap, String message) {
        modelMap.mergeAttributes(facilityHelper.locationMap());
        bindingResult.addError(new FieldError(CREATE_FACILITY_FORM, "name", message));
        modelMap.mergeAttributes(bindingResult.getModel());
    }
}
