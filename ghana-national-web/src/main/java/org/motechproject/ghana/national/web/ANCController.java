package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.service.CareService;
import org.motechproject.ghana.national.validator.FormValidator;
import org.motechproject.ghana.national.validator.RegisterANCFormValidator;
import org.motechproject.ghana.national.vo.ANCVO;
import org.motechproject.ghana.national.web.form.ANCEnrollmentForm;
import org.motechproject.ghana.national.web.helper.ANCFormMapper;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mrs.exception.ObservationNotFoundException;
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

import static org.motechproject.ghana.national.domain.EncounterType.ANC_REG_VISIT;

@Controller
@RequestMapping("admin/anc")
public class ANCController {

    public static final String ENROLL_ANC_URL = "anc/new";
    @Autowired
    FacilityHelper facilityHelper;
    @Autowired
    CareService careService;
    @Autowired
    AllEncounters allEncounters;
    @Autowired
    RegisterANCFormValidator registerANCFormValidator;
    @Autowired
    ANCFormMapper ancFormMapper;
    @Autowired
    private AllObservations allObservations;
    @Autowired
    private FormValidator formValidator;

    public ANCController() {
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @ApiSession
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newANC(@RequestParam("motechPatientId") String motechPatientId, ModelMap modelMap) {
        ANCEnrollmentForm enrollmentForm = new ANCEnrollmentForm(motechPatientId);
            MRSEncounter mrsEncounter = allEncounters.getLatest(motechPatientId, ANC_REG_VISIT.value());
            enrollmentForm = (mrsEncounter == null) ? enrollmentForm : ancFormMapper.convertMRSEncounterToView(mrsEncounter);
            ancFormMapper.populatePregnancyInfo(motechPatientId, enrollmentForm);
        modelMap.put("ancEnrollmentForm", enrollmentForm);
        addCareHistoryValues(modelMap);
        return ENROLL_ANC_URL;
    }

    @ApiSession
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(@Valid ANCEnrollmentForm ancEnrollmentForm, ModelMap modelMap) throws ObservationNotFoundException {
        Patient patient = formValidator.getPatient(ancEnrollmentForm.getMotechPatientId());
        List<FormError> formErrors = registerANCFormValidator.validatePatient(patient, ancEnrollmentForm, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList());
        formErrors.addAll(formValidator.validateIfStaffExists(ancEnrollmentForm.getStaffId()));
        if (formErrors.isEmpty()) {
            careService.enroll(createANCVO(ancEnrollmentForm));
            modelMap.addAttribute("success", "Updated successfully.");
        } else {
            modelMap.addAttribute("validationErrors", formErrors);
        }

        modelMap.put("ancEnrollmentForm", ancEnrollmentForm);
        addCareHistoryValues(modelMap);
        return ENROLL_ANC_URL;
    }


    private ANCVO createANCVO(ANCEnrollmentForm ancForm) {
        ancForm.setRegistrationDate((RegistrationToday.TODAY.equals(ancForm.getRegistrationToday())) ? DateUtil.now().toDate() : ancForm.getRegistrationDate());
        return new ANCVO(ancForm.getStaffId(), ancForm.getFacilityForm().getFacilityId(),
                ancForm.getMotechPatientId(), ancForm.getRegistrationDate(), ancForm.getRegistrationToday(),
                ancForm.getSerialNumber(), ancForm.getEstimatedDateOfDelivery(), ancForm.getHeight(),
                ancForm.getGravida(), ancForm.getParity(), ancForm.getAddHistory(), ancForm.getDeliveryDateConfirmed(),
                ancForm.getCareHistory(), ancForm.getLastIPT(), ancForm.getLastTT(), ancForm.getLastHbLevels(), ancForm.getLastMotherVitaminA(), ancForm.getLastIronOrFolate(),
                ancForm.getLastSyphilis(), ancForm.getLastMalaria(), ancForm.getLastDiarrhea(), ancForm.getLastPnuemonia(), ancForm.getLastIPTDate(), ancForm.getLastTTDate(),
                ancForm.getLastHbDate(), ancForm.getLastMotherVitaminADate(), ancForm.getLastIronOrFolateDate(), ancForm.getLastSyphilisDate(), ancForm.getLastMalariaDate(),
                ancForm.getLastDiarrheaDate(), ancForm.getLastPnuemoniaDate(), ancForm.getAddHistory());
    }

    private void addCareHistoryValues(ModelMap modelMap) {
        modelMap.put("careHistories", Arrays.asList(ANCCareHistory.TT.name(), ANCCareHistory.IPT_SP.name(),ANCCareHistory.HEMOGLOBIN.name(), ANCCareHistory.VITA.name(), ANCCareHistory.IRON_OR_FOLATE.name(), ANCCareHistory.SYPHILIS.name(), ANCCareHistory.MALARIA_RAPID_TEST.name(), ANCCareHistory.DIARRHEA.name(), ANCCareHistory.PNEUMOCOCCAL_A.name()));
        HashMap<Integer, String> lastIPTValues = new LinkedHashMap<Integer, String>();
        lastIPTValues.put(1, "IPT 1");
        lastIPTValues.put(2, "IPT 2");
        lastIPTValues.put(3, "IPT 3");

        HashMap<Integer, String> lastTTValues = new LinkedHashMap<Integer, String>();
        lastTTValues.put(1, "TT 1");
        lastTTValues.put(2, "TT 2");
        lastTTValues.put(3, "TT 3");
        lastTTValues.put(4, "TT 4");
        lastTTValues.put(5, "TT 5");

        modelMap.put("lastIPT", lastIPTValues);
        modelMap.put("lastTT", lastTTValues);


        modelMap.mergeAttributes(facilityHelper.locationMap());
    }
}
