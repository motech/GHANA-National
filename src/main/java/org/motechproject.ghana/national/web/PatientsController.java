package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.web.form.CreatePatientForm;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/admin/patients")
public class PatientsController {

    public static final String NEW_PATIENT = "patients/new";

    @Autowired
    private FacilityService facilityService;

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newPatientForm(ModelMap modelMap) {
        modelMap.put(Constants.CREATE_PATIENT_FORM, new CreatePatientForm());
        modelMap.mergeAttributes(facilityService.populateFacilityData());
        return NEW_PATIENT;
    }
}
