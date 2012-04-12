package org.motechproject.ghana.national.web;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.web.domain.Alert;
import org.motechproject.ghana.national.web.service.*;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSPatient;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.join;
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

    @Autowired
    private GNAggregatorService gnAggregatorService;

    private AtomicInteger phoneNumberCounter = new AtomicInteger(1);

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String createPatient(@RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                @RequestParam("regDate") String regDate,
                                @RequestParam("patientType") String patientType,
                                @RequestParam("patientDate") String patientDate,
                                ModelMap modelMap) throws UnsupportedEncodingException, ParseException, UserAlreadyExistsException, PatientIdIncorrectFormatException, PatientIdNotUniqueException, ObservationNotFoundException {

        int patientCounterValue = incrementPatientNameCounter();
        modelMap.put("firstNameIndexCounter", patientCounterValue);

        Date parsedPatientDate = new SimpleDateFormat("dd/MM/yyyy").parse(patientDate);
        Date parsedRegDate = new SimpleDateFormat("dd/MM/yyyy").parse(regDate);
        String serialNumber = "serialNumber";
        Date dateOfBirth = patientType.equals("Mother") ? DateUtil.newDate(1980, 1, 1).toDate() : parsedPatientDate;

        String facilityId = facilityService.getAFacility().get(0).getMrsFacilityId();
        String staffMotechId = staffService.createStaff(firstName, patientCounterValue);
        Patient patient = patientService.createPatient(firstName, lastName, dateOfBirth, facilityId, staffMotechId, patientCounterValue);

        if (patientType.equals("Mother")) {
            patientService.enrollForANCWithoutHistory(staffMotechId, facilityId, patient.getMotechId(), parsedRegDate, serialNumber, parsedPatientDate);
        } else if (patientType.equals("Child")) {
            patientService.enrollForCWCWithoutHistory(staffMotechId, facilityId, patient.getMotechId(), parsedRegDate, serialNumber);
        }
        return "schedule/search";
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String schedulesOfAPatientByMotechId(HttpServletRequest request, ModelMap modelMap) throws UnsupportedEncodingException, ParseException {

        HashMap<String, Map<String, List<Alert>>> schedules = null;
        final String patientId = request.getParameter("patientId");
        if (patientId != null) {
            schedules = new HashMap<String, Map<String, List<Alert>>>() {{
                put(patientId, scheduleService.getAllSchedulesByMotechId(patientId));
            }};
        }
        modelMap.put("patientSchedules", schedules);
        listAggregatedSMS(modelMap);
        modelMap.put("firstNameIndexCounter", incrementPatientNameCounter());
        return "schedule/search";
    }

    @RequestMapping(value = "searchByOpenmrsId", method = RequestMethod.GET)
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
    public String convertPatientId(@RequestParam("mrsId") String mrsId, @RequestParam("motechId") String motechId, @RequestParam("name") String name, ModelMap modelMap) throws UnsupportedEncodingException, ParseException {
        List<Patient> patients = new ArrayList<Patient>();
        if (!isNullOrEmpty(mrsId)) {
            final Patient patient = patientService.patientByOpenmrsId(mrsId);
            if (patient != null) {
                patients.add(patient);
            }
        } else if (!isNullOrEmpty(motechId)) {
            final Patient patient = patientService.getPatientByMotechId(motechId);
            if (patient != null) {
                patients.add(patient);
            }
        } else if (!isNullOrEmpty(name)) {
            patients.addAll(convert(patientService.getPatients(name, null, null, null, null), new Converter<MRSPatient, Patient>() {
                @Override
                public Patient convert(MRSPatient mrsPatient) {
                    return new Patient(mrsPatient);
                }
            }));
        }

        modelMap.put("ids", join(convert(patients, new Converter<Patient, String>() {
            @Override
            public String convert(Patient patient) {
                return "(MoTeCH id) " + patient.getMotechId() + " = " + patient.getMRSPatientId() + " (OpenMRS id)" + " = (First name) " + patient.getFirstName();
            }
        }), "  |||  "));

        modelMap.put("firstNameIndexCounter", incrementPatientNameCounter());
        return "schedule/search";
    }


    @RequestMapping(value = "searchWithinRange", method = RequestMethod.GET)
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

    private void listAggregatedSMS(ModelMap modelMap) throws UnsupportedEncodingException, ParseException {
        modelMap.put("smsList", gnAggregatorService.allMessages());
    }

    private int incrementPatientNameCounter() {
        final List<MRSPatient> patients = patientService.getPatients("Auto", null, null, null, null);
        int newCounterValue = 0;
        if (!patients.isEmpty()) {
            newCounterValue = Lambda.<Integer>max(convert(patients, new Converter<MRSPatient, Integer>() {
                @Override
                public Integer convert(MRSPatient patient) {
                    return Integer.parseInt(patient.getPerson().getFirstName().substring(4));
                }
            })) + 1;
        }
        while (!phoneNumberCounter.compareAndSet(phoneNumberCounter.get(), newCounterValue)) {
        }
        return phoneNumberCounter.get();
    }

}
