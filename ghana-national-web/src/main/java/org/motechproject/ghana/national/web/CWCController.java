package org.motechproject.ghana.national.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.validator.FormValidator;
import org.motechproject.ghana.national.validator.RegisterCWCFormValidator;
import org.motechproject.ghana.national.validator.patient.HasValidHistoryDates;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.ghana.national.web.helper.CwcFormMapper;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("admin/cwc")
public class CWCController {

    @Autowired
    PatientService patientService;

    @Autowired
    CareService careService;

    @Autowired
    FacilityHelper facilityHelper;

    @Autowired
    CwcFormMapper cwcFormMapper;

    @Autowired
    RegisterCWCFormValidator registerCWCFormValidator;

    @Autowired
    FormValidator formValidator;

    @Autowired
    AllEncounters allEncounters;

    private String errors = "errors";
    public static final String ENROLLMENT_CWC_FORM = "cwcEnrollmentForm";
    public static final String ENROLL_CWC_URL = "cwc/new";
    public static final String PATIENT_NOT_FOUND = "Patient Not Found";
    public static final String PATIENT_IS_NOT_A_CHILD = "Patient is Not A Child";
    public static final String STAFF_ID_NOT_FOUND = "Staff Not Found";
    public static final String REGISTRATION_OPTIONS = "registrationOptions";
    static final String CWCREGVISIT = "CWCREGVISIT";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @ApiSession
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String create(@RequestParam String motechPatientId, ModelMap modelMap) {
        Patient patient = patientService.getPatientByMotechId(motechPatientId);
        MRSEncounter encounter = allEncounters.getLatest(motechPatientId, CWCREGVISIT);
        CWCEnrollmentForm cwcEnrollmentForm;
        if(encounter != null) {
            cwcEnrollmentForm = cwcFormMapper.mapEncounterToView(encounter);
        } else {
            cwcEnrollmentForm = new CWCEnrollmentForm();
            cwcEnrollmentForm.setPatientMotechId(motechPatientId);
        }
        modelMap.addAttribute(ENROLLMENT_CWC_FORM, cwcEnrollmentForm);
        modelMap.mergeAttributes(cwcFormMapper.setViewAttributes());
        modelMap.mergeAttributes(facilityHelper.locationMap());

        if (patient == null) {
            modelMap.addAttribute(errors, Arrays.asList(PATIENT_NOT_FOUND));
            return ENROLL_CWC_URL;
        }

        if (patientService.getAgeOfPatientByMotechId(motechPatientId) >= 5) {
            modelMap.addAttribute(errors, Arrays.asList(PATIENT_IS_NOT_A_CHILD));
            return ENROLL_CWC_URL;
        }
        return ENROLL_CWC_URL;
    }

    @ApiSession
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@Valid CWCEnrollmentForm cwcEnrollmentForm, ModelMap modelMap) {
        modelMap.addAttribute(ENROLLMENT_CWC_FORM, cwcEnrollmentForm);
        modelMap.mergeAttributes(cwcFormMapper.setViewAttributes());
        modelMap.mergeAttributes(facilityHelper.locationMap());
        Patient patient = formValidator.getPatient(cwcEnrollmentForm.getPatientMotechId());
        List<FormError> formErrors = registerCWCFormValidator.validatePatient(patient, cwcEnrollmentForm, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList());
        formErrors.addAll(formValidator.validateIfStaffExists(cwcEnrollmentForm.getStaffId()));
        if(cwcEnrollmentForm.getAddHistory())
            formErrors.addAll(historyDateValidator(cwcEnrollmentForm).validate(patient, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList()));

        List<String> validationErrors = new ArrayList<String>();
        for (FormError formError : formErrors) {
            if(formError.getParameter().equals(Constants.CHILD_AGE_PARAMETER))  {
                validationErrors.add(PATIENT_IS_NOT_A_CHILD);
            }
            else if(formError.getParameter().equals(FormValidator.STAFF_ID)) {
                validationErrors.add(STAFF_ID_NOT_FOUND);
            }
            else if(formError.getParameter().equals(Constants.MOTECH_ID_ATTRIBUTE_NAME)) {
                validationErrors.add("Patient " + StringUtils.capitalize(formError.getError()));
            }
            else validationErrors.add(formError.getParameter() + " " + formError.getError());
        }

        if(!validationErrors.isEmpty()) {
            modelMap.addAttribute(errors, validationErrors);
            return ENROLL_CWC_URL;
        }

        if (cwcEnrollmentForm.getRegistrationToday().equals(RegistrationToday.TODAY)) {
            cwcEnrollmentForm.setRegistrationDate(DateUtil.now().toDate());
        }
        careService.enroll(new CwcVO(
                cwcEnrollmentForm.getStaffId(),
                cwcEnrollmentForm.getFacilityForm().getFacilityId(),
                cwcEnrollmentForm.getRegistrationDate(),
                cwcEnrollmentForm.getPatientMotechId(),
                cwcEnrollmentForm.getCareHistory(),
                cwcEnrollmentForm.getBcgDate(),
                cwcEnrollmentForm.getVitADate(),
                cwcEnrollmentForm.getMeaslesDate(),
                cwcEnrollmentForm.getYfDate(),
                cwcEnrollmentForm.getLastPentaDate(),
                cwcEnrollmentForm.getLastPenta(),
                cwcEnrollmentForm.getLastOPVDate(),
                cwcEnrollmentForm.getLastOPV(),
                cwcEnrollmentForm.getLastIPTiDate(),
                cwcEnrollmentForm.getLastIPTi(),
                cwcEnrollmentForm.getLastRotavirusDate(),
                cwcEnrollmentForm.getLastRotavirus(),
                cwcEnrollmentForm.getLastPneumococcal(),
                cwcEnrollmentForm.getLastPneumococcalDate(),
                cwcEnrollmentForm.getSerialNumber(),
                cwcEnrollmentForm.getAddHistory()));
        modelMap.addAttribute("success", "Client registered for CWC successfully.");
        return ENROLL_CWC_URL;
    }

    HasValidHistoryDates historyDateValidator(CWCEnrollmentForm cwcEnrollmentForm) {
        return new HasValidHistoryDates(cwcEnrollmentForm);
    }
}
