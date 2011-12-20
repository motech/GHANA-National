package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.service.IdentifierGenerationService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.web.form.PatientForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.motechproject.ghana.national.tools.Utility.safeToString;

@Controller
@RequestMapping(value = "/admin/patients")
public class PatientController {
    public static final String NEW_PATIENT_URL = "patients/new";
    public static final String PATIENT_FORM = "patientForm";
    public static final String SEARCH_PATIENT_URL = "patients/search";
    public static final String SUCCESS = "patients/success";

    private FacilityHelper facilityHelper;
    private PatientService patientService;
    private MessageSource messageSource;
    private IdentifierGenerationService identifierGenerationService;
    private MotechIdVerhoeffValidator motechIdVerhoeffValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    public PatientController() {
    }

    @Autowired
    public PatientController(PatientService patientService, IdentifierGenerationService identifierGenerationService,
                             MessageSource messageSource, FacilityHelper facilityHelper, MotechIdVerhoeffValidator motechIdVerhoeffValidator) {
        this.patientService = patientService;
        this.messageSource = messageSource;
        this.identifierGenerationService = identifierGenerationService;
        this.facilityHelper = facilityHelper;
        this.motechIdVerhoeffValidator = motechIdVerhoeffValidator;
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
            String patientID = "";
            if (createPatientForm.getRegistrationMode().equals(RegistrationType.AUTO_GENERATE_ID)) {
                patientID = identifierGenerationService.newPatientId();
            }
            patientService.registerPatient(getPatient(createPatientForm, patientID), createPatientForm.getTypeOfPatient(), createPatientForm.getParentId());
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
        }
        return SUCCESS;
    }

    @ApiSession
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String searchPatient(ModelMap modelMap) {
        modelMap.put(PATIENT_FORM, new PatientForm());
        return SEARCH_PATIENT_URL;
    }

    public Patient getPatient(PatientForm createPatientForm, String patientID) throws UnallowedIdentifierException {
        List<Attribute> attributes = Arrays.asList(
                new Attribute(PatientAttributes.PHONE_NUMBER.getAttribute(), safeToString(createPatientForm.getPhoneNumber())),
                new Attribute(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute(), safeToString(createPatientForm.getNhisExpirationDate())),
                new Attribute(PatientAttributes.NHIS_NUMBER.getAttribute(), createPatientForm.getNhisNumber()),
                new Attribute(PatientAttributes.INSURED.getAttribute(), safeToString(createPatientForm.getInsured())));
        if (patientID.equals("")) {
            if (!motechIdVerhoeffValidator.isValid(createPatientForm.getMotechId()))
                throw new UnallowedIdentifierException("User Id is not allowed");
            patientID = createPatientForm.getMotechId();
        }

        MRSPerson mrsPerson = new MRSPerson().firstName(createPatientForm.getFirstName()).middleName(createPatientForm.getMiddleName())
                .lastName(createPatientForm.getLastName()).preferredName(createPatientForm.getPreferredName()).dateOfBirth(createPatientForm.getDateOfBirth())
                .birthDateEstimated(createPatientForm.getEstimatedDateOfBirth()).gender(createPatientForm.getSex()).address(createPatientForm.getAddress()).attributes(attributes);
        MRSPatient mrsPatient = new MRSPatient(patientID, mrsPerson, new MRSFacility(createPatientForm.getFacilityId()));

        return new Patient(mrsPatient);
    }

    private void handleError(BindingResult bindingResult, ModelMap modelMap, String message) {
        modelMap.mergeAttributes(facilityHelper.locationMap());
        bindingResult.addError(new FieldError(PATIENT_FORM, "parentId", message));
        modelMap.mergeAttributes(bindingResult.getModel());
    }
}
