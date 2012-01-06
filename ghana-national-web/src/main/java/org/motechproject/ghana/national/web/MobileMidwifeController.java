package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.Displayable;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.web.form.MobileMidwifeEnrollmentForm;
import org.motechproject.model.DayOfWeek;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/enroll/mobile-midwife")
public class MobileMidwifeController {

    public static final String MOBILE_MIDWIFE_URL = "enroll/mobile-midwife/new";

    public MobileMidwifeController() {
    }

    @RequestMapping(value = "form", method = RequestMethod.GET)
    public String form(@RequestParam String motechPatientId, ModelMap modelMap){
        return enroll(motechPatientId, modelMap);
    }

    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String enroll(@RequestParam String motechPatientId, ModelMap modelMap) {
        modelMap.addAttribute("mobileMidwifeEnrollmentForm", new MobileMidwifeEnrollmentForm());
        modelMap.addAttribute("serviceTypes", collectOptions(ServiceType.values()));
        modelMap.addAttribute("phoneOwnerships", collectOptions(PhoneOwnership.values()));
        modelMap.addAttribute("reasonsToJoin", collectOptions(ReasonToJoin.values()));
        modelMap.addAttribute("learnedFrom", collectOptions(LearnedFrom.values()));
        modelMap.addAttribute("languages", collectOptions(Language.values()));
        modelMap.addAttribute("mediums", collectOptions(Medium.values()));
        modelMap.addAttribute("dayOfWeeks", collectDayOfWeekOptions());
        modelMap.addAttribute("pregnancyMessageStartWeeks", ServiceType.PREGNANCY.messageStartWeeks());
        modelMap.addAttribute("childCareMessageStartWeeks", ServiceType.CHILD_CARE.messageStartWeeks());
        return MOBILE_MIDWIFE_URL;
    }

    @ApiSession
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(MobileMidwifeEnrollmentForm mobileMidwifeEnrollmentForm, BindingResult bindingResult, ModelMap modelMap) {
        return MOBILE_MIDWIFE_URL;

    }

    private Map<String, String> collectDayOfWeekOptions() {
        Map<String, String> options = new LinkedHashMap<String, String>();
        for(DayOfWeek option : DayOfWeek.values()){
            options.put(option.name(), option.name());
        }
        return options;
    }

    private Map<String, String> collectOptions(Displayable[] displayables) {
        Map<String, String> options = new LinkedHashMap<String, String>();
        for (Displayable displayable : displayables) {
            options.put(displayable.value(), displayable.displayName());
        }
        return options;
    }

}
