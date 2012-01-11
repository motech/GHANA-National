package org.motechproject.ghana.national.web;

import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationToday;
import org.motechproject.ghana.national.service.CWCService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;
import org.motechproject.ghana.national.web.form.FacilityForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.openmrs.advice.ApiSession;
import org.openmrs.Concept;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

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
    public static final String REGISTRATION_OPTIONS = "registrationOptions";

    @Autowired
    PatientService patientService;

    @Autowired
    CWCService cwcService;

    @Autowired
    FacilityHelper facilityHelper;

    @Autowired
    StaffService staffService;
    private String error = "error";

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

        MRSEncounter encounter = cwcService.getEncounter(motechPatientId);
        CWCEnrollmentForm cwcEnrollmentForm = new CWCEnrollmentForm();

        if (encounter != null) {
            final FacilityForm facilityForm = new FacilityForm();
            final MRSFacility facility = encounter.getFacility();
            facilityForm.setCountry(facility.getCountry());
            facilityForm.setRegion(facility.getRegion());
            facilityForm.setCountyDistrict(facility.getCountyDistrict());
            facilityForm.setStateProvince(facility.getStateProvince());
            facilityForm.setFacilityId(facility.getId());
            facilityForm.setName(facility.getName());
            cwcEnrollmentForm.setFacilityForm(facilityForm);
            cwcEnrollmentForm.setRegistrationDate(encounter.getDate());
            cwcEnrollmentForm.setPatientMotechId(motechPatientId);
            cwcEnrollmentForm.setStaffId(encounter.getCreator().getSystemId());

            Set<MRSObservation> observations = encounter.getObservations();
            cwcEnrollmentForm.setAddHistory(!observations.isEmpty());
            final ArrayList<CwcCareHistory> careHistories = new ArrayList<CwcCareHistory>();
            for (MRSObservation observation : observations) {

                if (CWCService.CONCEPT_IMMUNIZATIONS_ORDERED.equals(observation.getConceptName())) {
                    final Concept concept = (Concept) observation.getValue();
                    final String conceptName = concept.getName().getName();
                    if (CWCService.CONCEPT_YF.equals(conceptName)) {
                        cwcEnrollmentForm.setYfDate(observation.getDate());
                        careHistories.add(CwcCareHistory.YF);
                    }
                    if (CWCService.CONCEPT_MEASLES.equals(conceptName)) {
                        cwcEnrollmentForm.setMeaslesDate(observation.getDate());
                        careHistories.add(CwcCareHistory.MEASLES);
                    }
                    if (CWCService.CONCEPT_BCG.equals(conceptName)) {
                        cwcEnrollmentForm.setBcgDate(observation.getDate());
                        careHistories.add(CwcCareHistory.BCG);
                    }
                    if (CWCService.CONCEPT_VITA.equals(conceptName)) {
                        cwcEnrollmentForm.setVitADate(observation.getDate());
                        careHistories.add(CwcCareHistory.VITA_A);
                    }
                }
                if (CWCService.CONCEPT_IPTI.equals(observation.getConceptName())) {
                    cwcEnrollmentForm.setLastIPTiDate(observation.getDate());
                    cwcEnrollmentForm.setLastIPTi((Integer) observation.getValue());
                    careHistories.add(CwcCareHistory.IPTI);
                }

                if (CWCService.CONCEPT_PENTA.equals(observation.getConceptName())) {
                    cwcEnrollmentForm.setLastPentaDate(observation.getDate());
                    cwcEnrollmentForm.setLastPenta((Integer) observation.getValue());
                    careHistories.add(CwcCareHistory.PENTA);
                }
                if (CWCService.CONCEPT_OPV.equals(observation.getConceptName())) {
                    cwcEnrollmentForm.setLastOPVDate(observation.getDate());
                    cwcEnrollmentForm.setLastOPV((Integer) observation.getValue());
                    careHistories.add(CwcCareHistory.OPV);
                }
                cwcEnrollmentForm.setCareHistory(careHistories);
                if (CWCService.CONCEPT_CWC_REG_NUMBER.equals(observation.getConceptName())) {
                    cwcEnrollmentForm.setSerialNumber((String) observation.getValue());
                }
            }
        }

        modelMap.addAttribute(ENROLLMENT_CWC_FORM, cwcEnrollmentForm);
        setViewAttributes(modelMap);

        if (patient == null) {
            modelMap.addAttribute(error, PATIENT_NOT_FOUND);
            return ENROLL_CWC_URL;
        }

        if (patientService.getAgeOfPatientByMotechId(motechPatientId) >= 5) {
            modelMap.addAttribute(error, PATIENT_IS_NOT_A_CHILD);
            return ENROLL_CWC_URL;
        }
        cwcEnrollmentForm.setPatientMotechId(motechPatientId);
        return ENROLL_CWC_URL;
    }

    private void setViewAttributes(ModelMap modelMap) {
        modelMap.addAttribute(CARE_HISTORIES, Arrays.asList(CwcCareHistory.values()));
        modelMap.addAttribute(REGISTRATION_OPTIONS, Arrays.asList(RegistrationToday.values()));
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
        modelMap.mergeAttributes(facilityHelper.locationMap());
    }

    @ApiSession
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@Valid CWCEnrollmentForm cwcEnrollmentForm, ModelMap modelMap) {
        modelMap.addAttribute(ENROLLMENT_CWC_FORM, cwcEnrollmentForm);
        setViewAttributes(modelMap);

        if (staffService.getUserById(cwcEnrollmentForm.getStaffId()) == null) {
            modelMap.addAttribute(error, "Staff Id not found");
            return ENROLL_CWC_URL;
        }

        Date registrationDate = cwcEnrollmentForm.getRegistrationDate();
        if(cwcEnrollmentForm.getRegistrationToday().equals(RegistrationToday.TODAY)) {
            registrationDate = new Date();
        }
        cwcService.enroll(new CwcVO(
                cwcEnrollmentForm.getStaffId(),
                cwcEnrollmentForm.getFacilityForm().getFacilityId(),
                registrationDate,
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
                cwcEnrollmentForm.getLastIPTi(),
                cwcEnrollmentForm.getSerialNumber()));
        modelMap.addAttribute("success", "Client registered for CWC successfully.");
        return ENROLL_CWC_URL;
    }
}
