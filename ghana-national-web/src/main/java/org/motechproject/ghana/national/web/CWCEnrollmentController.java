package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.CWCEnrollment;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.MotechProgram;
import org.motechproject.ghana.national.service.EnrollmentService;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.vo.MotechProgramName;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/admin/enroll/cwc")
public class CWCEnrollmentController {

    public static final String CWC_URL = "enroll/cwc";
    private EnrollmentService enrollmentService;
    private FacilityService facilityService;
    private FacilityHelper facilityHelper;

    public CWCEnrollmentController() {
    }

    @Autowired
    public CWCEnrollmentController(EnrollmentService enrollmentService, FacilityHelper facilityHelper, FacilityService facilityService) {
        this.enrollmentService = enrollmentService;
        this.facilityHelper = facilityHelper;
        this.facilityService = facilityService;
    }

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String enrollCWC(@RequestParam String patientMotechId, ModelMap modelMap) {
        CWCEnrollment CWCEnrollment = enrollmentService.cwcEnrollmentFor(patientMotechId);
        Facility facility = CWCEnrollment != null ? facilityService.getFacilityByMotechId(CWCEnrollment.getFacilityId()) : null;
        modelMap.addAttribute("CWCEnrollment", CWCEnrollment);
        CWCEnrollmentForm cwcEnrollmentForm = new CWCEnrollmentForm(CWCEnrollment, facility);
        cwcEnrollmentForm.setPatientMotechId(patientMotechId);
        modelMap.addAttribute("cwcEnrollmentForm", cwcEnrollmentForm);
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return CWC_URL;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String enrollCWC(@ModelAttribute CWCEnrollmentForm form, ModelMap modelMap) {
        CWCEnrollment CWCEnrollment = constructEnrollment(form);
        enrollmentService.saveOrUpdate(CWCEnrollment);
        return CWC_URL;
    }

    private CWCEnrollment constructEnrollment(CWCEnrollmentForm form) {
        CWCEnrollment CWCEnrollment = new CWCEnrollment();
        CWCEnrollment.facilityId(form.getFacilityForm().getFacilityId());
        CWCEnrollment.program(new MotechProgram(MotechProgramName.CWC));
        CWCEnrollment.serialNumber(form.getSerialNumber());
        CWCEnrollment.registrationDate(form.getRegistrationDate());
        CWCEnrollment.patienId(form.getPatientMotechId());
        return CWCEnrollment;
    }
}
