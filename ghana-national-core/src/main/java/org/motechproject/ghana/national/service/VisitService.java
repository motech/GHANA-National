package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.TTVaccine;
import org.motechproject.ghana.national.domain.TTVisit;
import org.motechproject.ghana.national.factory.TTVaccinationVisitEncounterFactory;
import org.motechproject.ghana.national.mapper.TTVaccinationEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.today;

@Service
public class VisitService {
    private AllSchedules allSchedules;
    private AllEncounters allEncounters;

    @Autowired
    public VisitService(AllSchedules allSchedules, AllEncounters allEncounters) {
        this.allSchedules = allSchedules;
        this.allEncounters = allEncounters;
    }

    public void receivedTT(final TTVaccine ttVaccine, MRSUser staff, Facility facility) {
        TTVisit ttVisit = new TTVisit().dosage(ttVaccine.getDosage()).facility(facility).patient(ttVaccine.getPatient()).staff(staff).date(ttVaccine.getVaccinationDate().toDate());
        Encounter encounter = new TTVaccinationVisitEncounterFactory().createEncounterForVisit(ttVisit);
        allEncounters.persistEncounter(encounter);
        createTTSchedule(ttVaccine);
    }

    public void createTTSchedule(TTVaccine ttVaccine) {
        final EnrollmentRequest enrollmentRequest = new TTVaccinationEnrollmentMapper().map(ttVaccine.getPatient(), ttVaccine.getVaccinationDate().toLocalDate(), ttVaccine.getDosage().getScheduleMilestoneName(), today());
        allSchedules.enrollOrFulfill(enrollmentRequest, newDate(ttVaccine.getVaccinationDate().toDate()));
    }
}
