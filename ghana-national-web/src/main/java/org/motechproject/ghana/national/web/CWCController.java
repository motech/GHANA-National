package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.CWCService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.openmrs.advice.ApiSession;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Controller
@RequestMapping("admin/cwc")
public class CWCController {

    static final String ENROLLMENT_CWC_FORM = "cwcEnrollmentForm";
    static final String ENROLL_CWC_URL = "cwc/new";
    static final String CARE_HISTORIES = "careHistories";
    static final String LAST_IPTI = "lastIPTi";
    static final String IPTI_1 = "IPTi 1";
    static final String IPTI_2 = "IPTi 2";
    static final String IPTI_3 = "IPTi 3";
    static final String OPV_0 = "OPV 0";
    static final String OPV_1 = "OPV 1";
    static final String OPV_2 = "OPV 2";
    static final String OPV_3 = "OPV 3";
    static final String LAST_OPV = "lastOPV";
    static final String LAST_PENTA = "lastPenta";
    static final String PENTA_1 = "Penta 1";
    static final String PENTA_2 = "Penta 2";
    static final String PENTA_3 = "Penta 3";
    static final String PATIENT_NOT_FOUND = "Patient Not Found";
    static final String PATIENT_IS_NOT_A_CHILD = "Patient is Not A Child";

    @Autowired
    PatientService patientService;

    @Autowired
    CWCService cwcService;

    @Autowired
    FacilityHelper facilityHelper;

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
        final CWCEnrollmentForm cwcEnrollmentForm = new CWCEnrollmentForm();
        modelMap.addAttribute(ENROLLMENT_CWC_FORM, cwcEnrollmentForm);
        modelMap.addAttribute(CARE_HISTORIES, Arrays.asList(CwcCareHistory.values()));
        modelMap.addAttribute(LAST_IPTI, new HashMap<Integer, String>() {{
            put(1, IPTI_1);
            put(2, IPTI_2);
            put(3, IPTI_3);
        }});
        modelMap.addAttribute(LAST_OPV, new HashMap<Integer, String>() {{
            put(0, OPV_0);
            put(1, OPV_1);
            put(2, OPV_2);
            put(3, OPV_3);
        }});
        modelMap.addAttribute(LAST_PENTA, new HashMap<Integer, String>() {{
            put(1, PENTA_1);
            put(2, PENTA_2);
            put(3, PENTA_3);
        }});

        final String error = "error";
        if (patient == null) {
            modelMap.addAttribute(error, PATIENT_NOT_FOUND);
            return ENROLL_CWC_URL;
        }

        if (patientService.getAgeOfPatientByMotechId(motechPatientId) >= 5) {
            modelMap.addAttribute(error, PATIENT_IS_NOT_A_CHILD);
            return ENROLL_CWC_URL;
        }
        cwcEnrollmentForm.setPatientMotechId(motechPatientId);
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return ENROLL_CWC_URL;
    }

    @ApiSession
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@Valid CWCEnrollmentForm cwcEnrollmentForm, ModelMap modelMap) {
        MRSEncounter mrsEncounter = cwcService.enroll(new CwcVO(
                cwcEnrollmentForm.getStaffId(),
                cwcEnrollmentForm.getFacilityForm().getFacilityId(),
                cwcEnrollmentForm.getRegistrationDate(),
                cwcEnrollmentForm.getPatientMotechId(),
                cwcEnrollmentForm.getBcgDate(),
                cwcEnrollmentForm.getVitADate(),
                cwcEnrollmentForm.getMeaslesDate(),
                cwcEnrollmentForm.getYfDate(),
                cwcEnrollmentForm.getLastPentaDate(),
                cwcEnrollmentForm.getLastPenta(),
                cwcEnrollmentForm.getLastOPVDate(),
                cwcEnrollmentForm.getLastOPV(),
                cwcEnrollmentForm.getLastIPTiDate(),
                cwcEnrollmentForm.getLastIPTi()));
        modelMap.addAttribute("success", "Client registered for CWC successfully.");
        return ENROLL_CWC_URL;
    }
}
