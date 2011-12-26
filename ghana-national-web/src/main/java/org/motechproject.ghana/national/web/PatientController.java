package org.motechproject.ghana.national.web;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.service.IdentifierGenerationService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.web.form.PatientForm;
import org.motechproject.ghana.national.web.form.SearchPatientForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.ghana.national.web.helper.PatientHelper;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.openmrs.patient.UnallowedIdentifierException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static ch.lambdaj.Lambda.convert;


@Controller
@RequestMapping(value = "/admin/patients")
public class PatientController {
    public static final String NEW_PATIENT_URL = "patients/new";
    public static final String PATIENT_FORM = "patientForm";
    public static final String SEARCH_PATIENT_URL = "patients/search";
    public static final String EDIT_PATIENT_URL = "patients/edit";
    public static final String SUCCESS = "patients/success";

    @Autowired
    private FacilityHelper facilityHelper;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PatientHelper patientHelper;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private IdentifierGenerationService identifierGenerationService;
    @Autowired
    private MotechIdVerhoeffValidator motechIdVerhoeffValidator;

    public static final String SEARCH_PATIENT_FORM = "searchPatientForm";


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newPatientForm(ModelMap modelMap) {
        modelMap.put(PATIENT_FORM, new PatientForm());
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return NEW_PATIENT_URL;
    }

    @ApiSession
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createPatient(PatientForm createPatientForm, BindingResult result, ModelMap modelMap) {
        try {
            if (createPatientForm.getRegistrationMode().equals(RegistrationType.AUTO_GENERATE_ID)) {
                createPatientForm.setMotechId(identifierGenerationService.newPatientId());
            } else {
                if (!motechIdVerhoeffValidator.isValid(createPatientForm.getMotechId())) {
                    throw new UnallowedIdentifierException("User Id is not allowed");
                }
            }
            final String motechId = patientService.registerPatient(patientHelper.getPatientVO(createPatientForm), createPatientForm.getTypeOfPatient(), createPatientForm.getParentId());
            if (StringUtils.isNotEmpty(motechId)) {
                modelMap.put("successMessage", "Patient created successfully.");
                modelMap.addAttribute(PATIENT_FORM, patientHelper.getPatientForm(patientService.getPatientById(motechId)));
                modelMap.mergeAttributes(facilityHelper.locationMap());
                return EDIT_PATIENT_URL;
            }
        } catch (ParentNotFoundException e) {
            handleError(result, modelMap, messageSource.getMessage("patient_parent_not_found", null, Locale.getDefault()));
            return NEW_PATIENT_URL;
        } catch (PatientIdNotUniqueException e) {
            handleError(result, modelMap, messageSource.getMessage("patient_id_duplicate", null, Locale.getDefault()));
            return NEW_PATIENT_URL;
        } catch (PatientIdIncorrectFormatException e) {
            handleError(result, modelMap, messageSource.getMessage("patient_id_incorrect", null, Locale.getDefault()));
            return NEW_PATIENT_URL;
        } catch (UnallowedIdentifierException e) {
            handleError(result, modelMap, messageSource.getMessage("patient_id_incorrect", null, Locale.getDefault()));
            return NEW_PATIENT_URL;
        } catch (ParseException ignored) {
        }
        return SUCCESS;
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String search(ModelMap modelMap) {
        modelMap.put(SEARCH_PATIENT_FORM, new SearchPatientForm());
        return SEARCH_PATIENT_URL;
    }

    @ApiSession
    @RequestMapping(value = "searchPatients", method = RequestMethod.POST)
    public String search(@Valid final SearchPatientForm searchPatientForm, ModelMap modelMap) {
        List<Patient> returnedPatient = patientService.search(searchPatientForm.getName(), searchPatientForm.getMotechId());
        modelMap.put(SEARCH_PATIENT_FORM, new SearchPatientForm(convert(returnedPatient, new Converter<Patient, PatientForm>() {
            @Override
            public PatientForm convert(Patient patient) {
                return new PatientForm(patient);
            }
        })));
        return PatientController.SEARCH_PATIENT_URL;
    }

    @ApiSession
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String edit(ModelMap modelMap, @RequestParam String motechId) {
        final Patient patient = patientService.getPatientById(motechId);
        try {
            modelMap.put(PATIENT_FORM, patientHelper.getPatientForm(patient));
        } catch (ParseException e) {

        }
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return PatientController.EDIT_PATIENT_URL;
    }

    private void handleError(BindingResult bindingResult, ModelMap modelMap, String message) {
        modelMap.mergeAttributes(facilityHelper.locationMap());
        bindingResult.addError(new FieldError(PATIENT_FORM, "parentId", message));
        modelMap.mergeAttributes(bindingResult.getModel());
    }
}
