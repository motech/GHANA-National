package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.exception.FacilityAlreadyFoundException;
import org.motechproject.ghana.national.helper.FacilityHelper;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.web.form.FacilityForm;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping(value = "/admin/facilities")
public class FacilityController {
    public static final String CREATE_FACILITY_FORM = "createFacilityForm";
    public static final String SEARCH_FACILITY_FORM = "searchFacilityForm";
    static final String EDIT_FACILITY_FORM = "editFacilityForm";
    public static final String SUCCESS = "facilities/success";
    public static final String NEW_FACILITY_URL = "facilities/new";
    public static final String SEARCH_FACILITY_URL = "facilities/search";
    static final String EDIT_FACILITY_URL = "facilities/edit";

    public static final String FACILITY_ID = "facilityId";
    private FacilityService facilityService;
    private MessageSource messageSource;
    private FacilityHelper facilityHelper;

    public FacilityController() {
    }

    @Autowired
    public FacilityController(FacilityService facilityService, MessageSource messageSource, FacilityHelper facilityHelper) {
        this.facilityService = facilityService;
        this.messageSource = messageSource;
        this.facilityHelper = facilityHelper;
    }

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newFacilityForm(ModelMap modelMap) {
        modelMap.put(CREATE_FACILITY_FORM, new FacilityForm());
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return NEW_FACILITY_URL;
    }

    @ApiSession
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createFacility(@Valid FacilityForm createFacilityForm, BindingResult bindingResult, ModelMap modelMap) {
        String facilityId;
        try {
            facilityId = facilityService.create(createFacilityForm.getName(), createFacilityForm.getCountry(), createFacilityForm.getRegion(),
                    createFacilityForm.getCountyDistrict(), createFacilityForm.getStateProvince(), createFacilityForm.getPhoneNumber(),
                    createFacilityForm.getAdditionalPhoneNumber1(), createFacilityForm.getAdditionalPhoneNumber2(),
                    createFacilityForm.getAdditionalPhoneNumber3());
        } catch (FacilityAlreadyFoundException e) {
            handleExistingFacilityError(bindingResult, modelMap, messageSource.getMessage("facility_already_exists", null, Locale.getDefault()));
            return NEW_FACILITY_URL;
        }
        return getFacilityForId(modelMap, facilityId);
    }

    @ApiSession
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String searchFacilityForm(ModelMap modelMap) {
        modelMap.put(SEARCH_FACILITY_FORM, new SearchFacilityForm());
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return SEARCH_FACILITY_URL;
    }

    @ApiSession
    @RequestMapping(value = "searchFacilities", method = RequestMethod.POST)
    public String searchFacility(@Valid final SearchFacilityForm searchFacilityForm, ModelMap modelMap) {
        final List<Facility> requestedFacilities = facilityService.searchFacilities(searchFacilityForm.getFacilityID(),
                searchFacilityForm.getName(), searchFacilityForm.getCountry(), searchFacilityForm.getRegion(),
                searchFacilityForm.getCountyDistrict(), searchFacilityForm.getStateProvince());
        modelMap.put("requestedFacilities", requestedFacilities);
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return SEARCH_FACILITY_URL;
    }

    @ApiSession
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String editFacilityForm(ModelMap modelMap, HttpServletRequest httpServletRequest) {
        String facilityId = httpServletRequest.getParameter(FACILITY_ID);
        return getFacilityForId(modelMap, facilityId);
    }

    private String getFacilityForId(ModelMap modelMap, String facilityId) {
        Facility facility = facilityService.getFacility(facilityId);
        modelMap.addAttribute(EDIT_FACILITY_FORM, copyFacilityValuesToForm(facility));
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return EDIT_FACILITY_URL;
    }

    private FacilityForm copyFacilityValuesToForm(Facility facility) {
        FacilityForm facilityForm = new FacilityForm();
        facilityForm.setAdditionalPhoneNumber1(facility.additionalPhoneNumber1());
        facilityForm.setAdditionalPhoneNumber2(facility.additionalPhoneNumber2());
        facilityForm.setAdditionalPhoneNumber3(facility.additionalPhoneNumber3());
        facilityForm.setPhoneNumber(facility.phoneNumber());
        facilityForm.setCountry(facility.country());
        facilityForm.setCountyDistrict(facility.district());
        facilityForm.setName(facility.name());
        facilityForm.setRegion(facility.region());
        facilityForm.setStateProvince(facility.province());
        facilityForm.setId(facility.motechId().toString());
        return facilityForm;
    }

    private void handleExistingFacilityError(BindingResult bindingResult, ModelMap modelMap, String message) {
        modelMap.mergeAttributes(facilityHelper.locationMap());
        bindingResult.addError(new FieldError(CREATE_FACILITY_FORM, "name", message));
        modelMap.mergeAttributes(bindingResult.getModel());
    }
}
