package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.web.form.CreatePatientForm;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping(value = "/admin/patients")
public class PatientController {
    public static final String NEW_PATIENT = "patients/new";
    public static final String CREATE_PATIENT_FORM = "createPatientForm";

    private FacilityService facilityService;
    private PatientService patientService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    public PatientController() {
    }

    @Autowired
    public PatientController(FacilityService facilityService, PatientService patientService) {
        this.facilityService = facilityService;
        this.patientService = patientService;
    }

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newPatientForm(ModelMap modelMap) {
        modelMap.put(CREATE_PATIENT_FORM, new CreatePatientForm());
        modelMap.mergeAttributes(facilityService.locationMap());
        return NEW_PATIENT;
    }

    @ApiSession
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createPatient(CreatePatientForm createPatientForm, ModelMap modelMap) {
        try {
            patientService.registerPatient(createPatientForm.getPatient(), createPatientForm.getTypeOfPatient(), createPatientForm.getParentId());
        } catch (ParentNotFoundException e) {
            e.printStackTrace();
        }
        return NEW_PATIENT;
    }
}
