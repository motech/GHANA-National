package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.RegisterClientAction;
import org.motechproject.ghana.national.domain.mobilemidwife.*;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.validator.MobileMidwifeValidator;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.ghana.national.web.form.MobileMidwifeEnrollmentForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.model.DayOfWeek;
import org.motechproject.openmrs.advice.ApiSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Controller
@RequestMapping(value = "/admin/enroll/mobile-midwife")
public class MobileMidwifeController {

    public static final String MOBILE_MIDWIFE_URL = "enroll/mobile-midwife/new";
    public static final String SUCCESS_MESSAGE = "mobilemidwife_enroll_success";
    private MobileMidwifeValidator mobileMidwifeValidator;
    private MobileMidwifeService mobileMidwifeService;
    private MessageSource messages;
    private FacilityHelper facilityHelper;
    private FacilityService facilityService;

    public MobileMidwifeController() {
    }

    @Autowired
    public MobileMidwifeController(MobileMidwifeValidator mobileMidwifeValidator, MobileMidwifeService mobileMidwifeService, MessageSource messages, FacilityHelper facilityHelper, FacilityService facilityService) {
        this.mobileMidwifeValidator = mobileMidwifeValidator;
        this.mobileMidwifeService = mobileMidwifeService;
        this.messages = messages;
        this.facilityHelper = facilityHelper;
        this.facilityService = facilityService;
    }

    @ApiSession
    @RequestMapping(value = "form", method = RequestMethod.GET)
    public String form(@RequestParam String motechPatientId, ModelMap modelMap) {

        MobileMidwifeEnrollment midwifeEnrollment = mobileMidwifeService.findLatestEnrollment(motechPatientId);
        MobileMidwifeEnrollmentForm enrollmentForm = midwifeEnrollment != null ? createMobileMidwifeEnrollmentForm(midwifeEnrollment)
                : new MobileMidwifeEnrollmentForm().setPatientMotechId(motechPatientId).setStatus("ACTIVE");
        addFormInfo(modelMap, enrollmentForm);
        return MOBILE_MIDWIFE_URL;
    }

    private MobileMidwifeEnrollmentForm createMobileMidwifeEnrollmentForm(MobileMidwifeEnrollment enrollment) {
        MobileMidwifeEnrollmentForm enrollmentForm = new MobileMidwifeEnrollmentForm();
        if (enrollment != null) {
            enrollmentForm.setPatientMotechId(enrollment.getPatientId()).setStaffMotechId(enrollment.getStaffId()).
                    setConsent(enrollment.getConsent()).setServiceType(enrollment.getServiceType()).setPhoneOwnership(enrollment.getPhoneOwnership())
                    .setPhoneNumber(enrollment.getPhoneNumber()).setMedium(enrollment.getMedium()).setDayOfWeek(enrollment.getDayOfWeek())
                    .setTimeOfDay(enrollment.getTimeOfDay()).setLanguage(enrollment.getLanguage()).setLearnedFrom(enrollment.getLearnedFrom())
                    .setReasonToJoin(enrollment.getReasonToJoin()).setMessageStartWeek(enrollment.getMessageStartWeek()).setStatus(enrollment.getActive() ? "ACTIVE" : "INACTIVE");

            Facility facility = facilityService.getFacilityByMotechId(enrollment.getFacilityId());
            FacilityForm facilityForm = new FacilityForm();
            facilityForm.setFacilityId(facility.getMrsFacilityId());
            facilityForm.setCountry(facility.country());
            facilityForm.setRegion(facility.region());
            facilityForm.setCountyDistrict(facility.district());
            facilityForm.setStateProvince(facility.province());
            enrollmentForm.setFacilityForm(facilityForm);
        }
        return enrollmentForm;
    }

    @ApiSession
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(MobileMidwifeEnrollmentForm form, BindingResult bindingResult, ModelMap modelMap) {
        MobileMidwifeEnrollment midwifeEnrollment = createEnrollment(form);
        List<FormError> formErrors = mobileMidwifeValidator.validate(midwifeEnrollment);
        if (isNotEmpty(formErrors)) {
            addFormInfo(modelMap, form).addAttribute("formErrors", formErrors);
        } else {
            mobileMidwifeRegistration(form, midwifeEnrollment);
            addFormInfo(modelMap, form).addAttribute("successMessage", messages.getMessage(SUCCESS_MESSAGE, null, Locale.getDefault()));
        }
        return MOBILE_MIDWIFE_URL;
    }

    private void mobileMidwifeRegistration(MobileMidwifeEnrollmentForm form, MobileMidwifeEnrollment midwifeEnrollment) {
        if (RegisterClientAction.REGISTER.name().equals(form.getAction())) {
            mobileMidwifeService.register(midwifeEnrollment);
        } else {
            mobileMidwifeService.unregister(form.getPatientMotechId());
            form.setStatus("INACTIVE");
        }
    }

    private ModelMap addFormInfo(ModelMap modelMap, MobileMidwifeEnrollmentForm enrollmentForm) {
        modelMap.addAttribute("mobileMidwifeEnrollmentForm", enrollmentForm)
                .addAttribute("serviceTypes", ServiceType.values())
                .addAttribute("phoneOwnerships", PhoneOwnership.values())
                .addAttribute("reasonsToJoin", ReasonToJoin.values())
                .addAttribute("learnedFrom", LearnedFrom.values())
                .addAttribute("languages", Language.values())
                .addAttribute("mediums", Medium.values())
                .addAttribute("dayOfWeeks", collectDayOfWeekOptions())
                .addAttribute("messageStartWeeks", MessageStartWeek.messageStartWeeks())
                .addAttribute("minTimeOfDay", Constants.MOBILE_MIDWIFE_MIN_TIMEOFDAY_FOR_VOICE)
                .addAttribute("maxTimeOfDay", Constants.MOBILE_MIDWIFE_MAX_TIMEOFDAY_FOR_VOICE);
        modelMap.mergeAttributes(facilityHelper.locationMap());
        return modelMap;
    }

    private Map<String, String> collectDayOfWeekOptions() {
        Map<String, String> options = new LinkedHashMap<String, String>();
        for (DayOfWeek option : DayOfWeek.values()) {
            options.put(option.name(), option.name());
        }
        return options;
    }

    MobileMidwifeEnrollment createEnrollment(MobileMidwifeEnrollmentForm form) {
        MobileMidwifeEnrollment enrollment = MobileMidwifeEnrollment.newEnrollment();
        String facilityId = facilityService.getFacility(form.getFacilityForm().getFacilityId()).getMotechId();

        enrollment.setStaffId(form.getStaffMotechId()).setPatientId(form.getPatientMotechId()).setFacilityId(facilityId)
                .setConsent(form.getConsent()).setDayOfWeek(form.getDayOfWeek()).setLearnedFrom(form.getLearnedFrom())
                .setLanguage(form.getLanguage()).setMedium(form.getMedium()).setMessageStartWeek(form.getMessageStartWeek()).setPhoneNumber(form.getPhoneNumber())
                .setPhoneOwnership(form.getPhoneOwnership()).setReasonToJoin(form.getReasonToJoin()).setServiceType(form.getServiceType())
                .setTimeOfDay(form.getTimeOfDay()).setActive(form.getStatus().equals("ACTIVE"));
        return enrollment;
    }
}
