package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.service.ANCService;
import org.motechproject.ghana.national.web.form.ANCEnrollmentForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
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

@Controller
@RequestMapping("admin/anc")
public class ANCController {

    public static final String ANC_URL = "anc/new";
    private FacilityHelper facilityHelper;
    @Autowired
    private ANCService service;

    public ANCController() {
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @Autowired
    public ANCController(FacilityHelper facilityHelper, ANCService service) {
        this.facilityHelper = facilityHelper;
        this.service = service;
    }

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String enroll(@RequestParam("motechPatientId") String motechPatientId, ModelMap modelMap) {
        modelMap.put("ancEnrollmentForm", new ANCEnrollmentForm(motechPatientId));
        addPageAttributes(modelMap);
        return ANC_URL;
    }

    @ApiSession
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(@Valid ANCEnrollmentForm ancEnrollmentForm, ModelMap modelMap) {
        modelMap.put("ancEnrollmentForm", ancEnrollmentForm);
        addPageAttributes(modelMap);
        return ANC_URL;
    }

    private void addPageAttributes(ModelMap modelMap) {
        modelMap.put("careHistories", Arrays.asList("TT", "IPT"));
        modelMap.put("lastIPT", Arrays.asList("IPT 1", "IPT 2", "IPT 3"));
        modelMap.put("lastTT", Arrays.asList("TT 1", "TT 2", "TT 3", "TT 4", "TT 5"));
        modelMap.mergeAttributes(facilityHelper.locationMap());
    }
}
