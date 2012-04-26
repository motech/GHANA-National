package org.motechproject.ghana.national.web;

import ch.lambdaj.Lambda;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.core.Is.is;

public class QueryTestDataProvider {
    private static Map<String, PatientWindow> data;

    AllTrackedSchedules allTrackedSchedules;

    AllSchedules allSchedules;

    private PatientService patientService;

    public QueryTestDataProvider(AllTrackedSchedules allTrackedSchedules, AllSchedules allSchedules, PatientService patientService) {
        this.allTrackedSchedules = allTrackedSchedules;
        this.allSchedules = allSchedules;
        this.patientService = patientService;
        data = new HashMap<String, PatientWindow>();
    }

    public void addToEarlyWindow(Patient patient, String scheduleName, String milestoneName) {
        addToWindow(patient, scheduleName, WindowName.earliest.name(), milestoneName);
    }

    public void addToDueWindow(Patient patient, String scheduleName, String milestoneName) {
        addToWindow(patient, scheduleName, WindowName.due.name(), milestoneName);
    }

    public void addToLateWindow(Patient patient, String scheduleName, String milestoneName) {
        addToWindow(patient, scheduleName, WindowName.late.name(), milestoneName);
    }

    public void addToMaxWindow(Patient patient, String scheduleName, String milestoneName) {
        addToWindow(patient, scheduleName, WindowName.max.name(), milestoneName);
    }

    public static List<String> getPatientIdsInWindowsForSchedules(List<String> windowNames, String facilityMRSId, String... scheduleNames) {
        List<Patient> patientsInFacility = new ArrayList<Patient>();
        for (String scheduleName : scheduleNames) {
            for (String windowName : windowNames) {
                List<Patient> patients = getWindowForSchedule(scheduleName).getPatients(windowName);
                patientsInFacility.addAll(Lambda.select(patients, having(on(Patient.class).getMrsPatient().getFacility().getId(), is(facilityMRSId))));
            }
        }

        return Lambda.extract(patientsInFacility, on(Patient.class).getMotechId());
    }


    private void addToWindow(Patient patient, String scheduleName, String windowName, String milestoneName) {
        PatientWindow windowPatient = getWindowForSchedule(scheduleName);
        LocalDate referenceDate = referenceDateToStartInAWindowInAMileStone(scheduleName, windowName, milestoneName);
        if (milestoneName == null) {
            allSchedules.enroll(new EnrollmentRequest(patient.getMRSPatientId(), scheduleName, null,
                    referenceDate, null, null, null, milestoneName, patient.facilityMetaData()));
        } else
            allSchedules.enroll(new EnrollmentRequest(patient.getMRSPatientId(), scheduleName, null,
                    null, null, referenceDate, null, milestoneName, patient.facilityMetaData()));
        windowPatient.addPatient(windowName, patient);

        data.put(scheduleName, windowPatient);
    }

    public static PatientWindow getWindowForSchedule(String scheduleName) {
        return (data.get(scheduleName) != null) ? data.get(scheduleName) : new PatientWindow();
    }

    private LocalDate referenceDateToStartInAWindowInAMileStone(String scheduleName, String windowName, String milestoneName) {
        Period startOfDue = windowPeriodForAMileStone(scheduleName, WindowName.valueOf(windowName), milestoneName);
        return DateUtil.now().minus(startOfDue).toLocalDate();
    }

    private Period windowPeriodForAMileStone(String scheduleName, WindowName window, String milestoneName) {
        Schedule schedule = allTrackedSchedules.getByName(scheduleName);
        return milestoneName==null?
                schedule.getFirstMilestone().getWindowStart(window):
                schedule.getMilestone(milestoneName).getWindowStart(window);
    }

    public void fulfill(List<String> patientMotechIds, String scheduleName, LocalDate fulfillmentDate) {
        for (String motechId : patientMotechIds) {
            Patient patient = patientService.getPatientByMotechId(motechId);
            allSchedules.fulfilCurrentMilestone(patient.getMRSPatientId(), scheduleName, fulfillmentDate);
        }
    }
}

class PatientWindow {
    Map<String, List<Patient>> patientWindow = new HashMap<String, List<Patient>>();

    public List<Patient> getPatients(String windowName) {
        if (patientWindow.get(windowName) == null) {
            patientWindow.put(windowName, new ArrayList<Patient>());
        }
        return patientWindow.get(windowName);
    }


    public void addPatient(String windowName, Patient patient) {
        getPatients(windowName).add(patient);
    }
}
