package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;

@Controller
@RequestMapping("admin/cwc")
public class CWCController {

    private static final String ENROLLMENT_CWC_FORM = "cwcEnrollmentForm";
    private static final String ENROLL_CWC_URL = "cwc/new";

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String create(ModelMap modelMap) {
        modelMap.addAttribute(ENROLLMENT_CWC_FORM, new CWCEnrollmentForm());
        modelMap.addAttribute("careHistories", Arrays.asList(CwcCareHistory.values()));
        modelMap.addAttribute("lastIPTi", Arrays.asList("IPTi 1", "IPTi 2", "IPTi 3"));
        modelMap.addAttribute("lastOPV", Arrays.asList("OPV 0", "OPV 1", "OPV 2", "OPV 3"));
        modelMap.addAttribute("lastPenta", Arrays.asList("Penta 1", "Penta 2", "Penta 3"));
        return ENROLL_CWC_URL;
    }
}
