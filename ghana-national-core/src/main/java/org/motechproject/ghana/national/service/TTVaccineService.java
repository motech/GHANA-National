package org.motechproject.ghana.national.service;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.mapper.TTVaccinationEnrollmentMapper;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.TETANUS_TOXOID_DOSE;
import static org.motechproject.ghana.national.domain.EncounterType.TT_VISIT;

@Service
public class TTVaccineService extends BaseScheduleService {

    private EncounterService encounterService;

    @Autowired
    public TTVaccineService(EncounterService encounterService, ScheduleTrackingService scheduleTrackingService) {
        super(scheduleTrackingService);
        this.encounterService = encounterService;
    }

    public void received(final TTVaccineDosage dosage, Patient patient, String staffId, String facilityId, final LocalDate vaccinationDate) {
        createEncounterForVisit(dosage, patient, staffId, facilityId, vaccinationDate);

        final EnrollmentRequest enrollmentRequest = new TTVaccinationEnrollmentMapper().map(patient, vaccinationDate, dosage.getScheduleMilestoneName());
        scheduleAlerts(patient, enrollmentRequest);
    }

    protected void createEncounterForVisit(final TTVaccineDosage dosage, Patient patient, String staffId, String facilityId, LocalDate dateOfObservation) {
        final Date observationDate = dateOfObservation.toDate();
        Set<MRSObservation> observation = new HashSet<MRSObservation>() {{
            add(new MRSObservation<Double>(observationDate, TETANUS_TOXOID_DOSE.getName(), dosage.getDosageAsDouble()));
        }};
        encounterService.persistEncounter(patient.getMrsPatient(), staffId, facilityId, TT_VISIT.value(), observationDate, observation);
    }
}
