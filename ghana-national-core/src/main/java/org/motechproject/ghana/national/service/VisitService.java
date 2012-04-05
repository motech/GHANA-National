package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.factory.TTVaccinationVisitEncounterFactory;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllSchedulesAndMessages;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.motechproject.util.DateUtil.newDate;

@Service
public class VisitService {
    private AllSchedulesAndMessages allSchedulesAndMessages;
    private AllEncounters allEncounters;

    @Autowired
    public VisitService(AllSchedulesAndMessages allSchedulesAndMessages, AllEncounters allEncounters) {
        this.allSchedulesAndMessages = allSchedulesAndMessages;
        this.allEncounters = allEncounters;
    }

    public void receivedTT(final TTVaccine ttVaccine, MRSUser staff, Facility facility) {
        TTVisit ttVisit = new TTVisit().dosage(ttVaccine.getDosage()).facility(facility).patient(ttVaccine.getPatient()).staff(staff).date(ttVaccine.getVaccinationDate().toDate());
        Encounter encounter = new TTVaccinationVisitEncounterFactory().createEncounterForVisit(ttVisit);
        allEncounters.persistEncounter(encounter);
        createTTSchedule(ttVaccine);
    }

    public void createTTSchedule(TTVaccine ttVaccine) {
        Patient patient = ttVaccine.getPatient();
        PatientCare patientCare = patient.ttVaccinePatientCareOnVisit(ttVaccine.getVaccinationDate().toLocalDate()).milestoneName(ttVaccine.getDosage().getScheduleMilestoneName());
        final EnrollmentRequest enrollmentRequest = new ScheduleEnrollmentMapper().map(patient, patientCare);
        allSchedulesAndMessages.enrollOrFulfill(enrollmentRequest, newDate(ttVaccine.getVaccinationDate().toDate()));
    }
}
