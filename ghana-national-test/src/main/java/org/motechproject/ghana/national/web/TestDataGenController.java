package org.motechproject.ghana.national.web;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.web.domain.Alert;
import org.motechproject.ghana.national.web.service.GNFacilityService;
import org.motechproject.ghana.national.web.service.GNPatientService;
import org.motechproject.ghana.national.web.service.GNScheduleService;
import org.motechproject.ghana.national.web.service.GNStaffService;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.lambdaj.Lambda.convert;
import static org.motechproject.util.StringUtil.isNullOrEmpty;

@Controller
@RequestMapping(value = "/debug/schedule/")
public class TestDataGenController {

    @Autowired
    private GNPatientService patientService;

    @Autowired
    private GNStaffService staffService;

    @Autowired
    private GNFacilityService facilityService;

    @Autowired
    private GNScheduleService scheduleService;

    private AtomicInteger phoneNumberCounter = new AtomicInteger(1);

    @RequestMapping(value = "search", method = RequestMethod.GET)
    @LoginAsAdmin
    @ApiSession
    public String schedulesOfAPatientByMotechId(HttpServletRequest request, ModelMap modelMap) throws UnsupportedEncodingException, ParseException {

        HashMap<String, Map<String, List<Alert>>> schedules = null;
        final String patientId = request.getParameter("patientId");
        if (patientId != null) {
            schedules = new HashMap<String, Map<String, List<Alert>>>() {{
                put(patientId, scheduleService.getAllSchedulesByMotechId(patientId));
            }};
        }
        modelMap.put("patientSchedules", schedules);

        modelMap.put("firstNameIndexCounter", incrementPatientNameCounter());
        return "schedule/search";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    @LoginAsAdmin
    @ApiSession
    public String createPatient(@RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                @RequestParam("regDate") String regDate,
                                @RequestParam("patientType") String patientType,
                                @RequestParam("patientDate") String patientDate,
                                ModelMap modelMap) throws UnsupportedEncodingException, ParseException, UserAlreadyExistsException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {

        int patientCounterValue = incrementPatientNameCounter();
        modelMap.put("firstNameIndexCounter", patientCounterValue);

        Date parsedPatientDate = new SimpleDateFormat("dd/MM/yyyy").parse(patientDate);
        Date parsedRegDate = new SimpleDateFormat("dd/MM/yyyy").parse(regDate);
        String serialNumber = "serialNumber";
        Date dateOfBirth = patientType.equals("Mother") ? DateUtil.newDate(1980, 1, 1).toDate() : parsedPatientDate;

        String facilityId = facilityService.getAFacility().get(0).getMrsFacilityId();
        String staffMotechId = staffService.createStaff(firstName, patientCounterValue);
        Patient patient = patientService.createPatient(firstName, lastName, dateOfBirth, facilityId, staffMotechId, patientCounterValue);

        if(patientType.equals("Mother")){
            patientService.enrollForANCWithoutHistory(staffMotechId, facilityId, patient.getMotechId(), parsedRegDate, serialNumber, parsedPatientDate);
        }else if(patientType.equals("Child")){
            patientService.enrollForCWCWithoutHistory(staffMotechId, facilityId, patient.getMotechId(), parsedRegDate, serialNumber);
        }
        return "schedule/search";
    }

    @RequestMapping(value = "searchByOpenmrsId", method = RequestMethod.GET)
    @LoginAsAdmin
    @ApiSession
    public String schedulesOfAPatientByOpenmrsId(HttpServletRequest request, ModelMap modelMap) throws UnsupportedEncodingException, ParseException {
        HashMap<String, Map<String, List<Alert>>> schedules = null;
        final String patientId = request.getParameter("patientId");
        if (patientId != null) {
            schedules = new HashMap<String, Map<String, List<Alert>>>() {{
                put(patientId, scheduleService.getAllSchedulesByMrsId(patientId));
            }};
        }
        modelMap.put("patientSchedules", schedules);
        modelMap.put("firstNameIndexCounter", incrementPatientNameCounter());
        return "schedule/search";
    }

    @RequestMapping(value = "convertPatientId", method = RequestMethod.GET)
    @LoginAsAdmin
    @ApiSession
    public String convertPatientId(@RequestParam("mrsId") String mrsId, @RequestParam("motechId") String motechId, ModelMap modelMap) throws UnsupportedEncodingException, ParseException {
        if (!isNullOrEmpty(mrsId)) {
            motechId = patientService.patientByOpenmrsId(mrsId).getMotechId();
        } else if (!isNullOrEmpty(motechId)) {
            mrsId = patientService.getPatientByMotechId(motechId).getMRSPatientId();
        }
        if (!isNullOrEmpty(mrsId) && !isNullOrEmpty(motechId))
            modelMap.put("ids", "(MoTeCH id) " + motechId + " = " + mrsId + " (OpenMRS id)");
        modelMap.put("firstNameIndexCounter", incrementPatientNameCounter());
        return "schedule/search";
    }

    @RequestMapping(value = "searchWithinRange", method = RequestMethod.GET)
    @LoginAsAdmin
    @ApiSession
    public String schedulesWithinRange(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, ModelMap modelMap) throws UnsupportedEncodingException, ParseException {
        Map<String, Map<String, List<Alert>>> schedules = scheduleService.getAllActiveSchedules();
        final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        scheduleService.filterActiveSchedulesWithin(schedules, formatter.parse(startDate), DateUtil.newDate(formatter.parse(endDate)).plusDays(1).toDate());
        modelMap.put("patientSchedules", schedules);
        modelMap.put("startDate", startDate);
        modelMap.put("endDate", endDate);
        modelMap.put("firstNameIndexCounter", incrementPatientNameCounter());
        return "schedule/search";
    }

    private int incrementPatientNameCounter() {
        final int newCounterValue = Lambda.<Integer>max(convert(patientService.getPatients("Auto", null, null, null, null), new Converter<MRSPatient, Integer>() {
            @Override
            public Integer convert(MRSPatient patient) {
                return Integer.parseInt(patient.getPerson().getFirstName().substring(4));
            }
        })) + 1;
        while (!phoneNumberCounter.compareAndSet(phoneNumberCounter.get(), newCounterValue)){
        }
        return phoneNumberCounter.get();
    }

}
