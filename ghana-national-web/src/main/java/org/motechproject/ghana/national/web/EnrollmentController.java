package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.Enrollment;
import org.motechproject.ghana.national.service.EnrollmentService;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/admin/enroll/")
public class EnrollmentController {

    public static final String CWC_URL = "enroll/cwc";
    private EnrollmentService enrollmentService;
    private FacilityHelper facilityHelper;

    public EnrollmentController() {
    }

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService, FacilityHelper facilityHelper) {
        this.enrollmentService = enrollmentService;
        this.facilityHelper = facilityHelper;
    }

    @ApiSession
    @RequestMapping(value = "cwc-form", method = RequestMethod.GET)
    public String enrollCWC(@RequestParam String motechPatientId, ModelMap modelMap) {
        Enrollment enrollment = enrollmentService.enrollmentFor(motechPatientId);
        modelMap.addAttribute("enrollment", enrollment);
        modelMap.addAttribute("cwcEnrollmentForm", new CWCEnrollmentForm());
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return CWC_URL;
    }

//    @RequestMapping(value = "cwc", method = RequestMethod.POST)
//    public String enrollCWC(@ModelAttribute CWCEnrollmentForm form, ModelMap modelMap) {
//        Enrollment enrollment = enrollmentService.enrollmentFor(form.getPatientMotechId());
//        modelMap.addAttribute("enrollment", enrollment);
//        modelMap.addAttribute("cwcEnrollmentForm", form);
//        return CWC_URL;
//    }
}
