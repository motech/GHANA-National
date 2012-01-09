package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
@RequestMapping("admin/cwc")
public class CWCController {

    static final String ENROLLMENT_CWC_FORM = "cwcEnrollmentForm";
    static final String ENROLL_CWC_URL = "cwc/new";
    static final String CARE_HISTORIES = "careHistories";
    static final String LAST_IP_TI = "lastIPTi";
    public static final String IPTI_1 = "IPTi 1";
    public static final String IPTI_2 = "IPTi 2";
    public static final String IPTI_3 = "IPTi 3";
    public static final String OPV_0 = "OPV 0";
    public static final String OPV_1 = "OPV 1";
    public static final String OPV_2 = "OPV 2";
    public static final String OPV_3 = "OPV 3";
    public static final String LAST_OPV = "lastOPV";
    public static final String LAST_PENTA = "lastPenta";
    public static final String PENTA_1 = "Penta 1";
    public static final String PENTA_2 = "Penta 2";
    public static final String PENTA_3 = "Penta 3";
    public static final String PATIENT_NOT_FOUND = "Patient Not Found";
    public static final String PATIENT_IS_NOT_A_CHILD = "Patient is Not A Child";

    @Autowired
    PatientService patientService;

    @ApiSession
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String create(@RequestParam String motechPatientId, ModelMap modelMap) {
        Patient patient = patientService.getPatientByMotechId(motechPatientId);
        modelMap.addAttribute(ENROLLMENT_CWC_FORM, new CWCEnrollmentForm());
        modelMap.addAttribute(CARE_HISTORIES, Arrays.asList(CwcCareHistory.values()));
        modelMap.addAttribute(LAST_IP_TI, Arrays.asList(IPTI_1, IPTI_2, IPTI_3));
        modelMap.addAttribute(LAST_OPV, Arrays.asList(OPV_0, OPV_1, OPV_2, OPV_3));
        modelMap.addAttribute(LAST_PENTA, Arrays.asList(PENTA_1, PENTA_2, PENTA_3));

        final String error = "error";
        if (patient == null) {
            modelMap.addAttribute(error, PATIENT_NOT_FOUND);
            return ENROLL_CWC_URL;
        }

        if (patientService.getAgeOfPatientByMotechId(motechPatientId) >= 5) {
            modelMap.addAttribute(error, PATIENT_IS_NOT_A_CHILD);
            return ENROLL_CWC_URL;
        }
        return ENROLL_CWC_URL;
    }
}
