package org.ghana.national.web;

import org.ghana.national.exception.FacilityAlreadyFoundException;
import org.ghana.national.service.FacilityService;
import org.ghana.national.tools.Constants;
import org.ghana.national.web.form.CreateFacilityForm;
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
import java.util.Locale;

@Controller
@RequestMapping(value = "/admin/facilities")
public class FacilitiesController {
    @Autowired
    FacilityService facilityService;

    @Autowired
    private MessageSource messageSource;

    final static String NEW_FACILITY = "facilities/new";
    final static String SUCCESS = "facilities/success";

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newFacilityForm(ModelMap modelMap) {
        modelMap.put(Constants.CREATE_FACILITY_FORM, new CreateFacilityForm());
        modelMap.mergeAttributes(facilityService.populateFacilityData());
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
            modelMap.mergeAttributes(facilityService.populateFacilityData());
        } catch (FacilityAlreadyFoundException e) {
            handleExistingFacilityError(bindingResult, modelMap, messageSource.getMessage("facility_already_exists", null, Locale.getDefault()));
            return NEW_FACILITY;
        }
        return SUCCESS;
    }

    private void handleExistingFacilityError(BindingResult bindingResult, ModelMap modelMap, String message) {
        modelMap.mergeAttributes(facilityService.populateFacilityData());
        bindingResult.addError(new FieldError(Constants.CREATE_FACILITY_FORM, "name", message));
        modelMap.mergeAttributes(bindingResult.getModel());
    }

}
