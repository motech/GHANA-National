package org.motechproject.ghana.national.service;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.TTVaccineDosage;
import org.motechproject.ghana.national.factory.MotherVisitEncounterFactory;
import org.motechproject.ghana.national.factory.TTVaccinationVisitEncounterFactory;
import org.motechproject.ghana.national.mapper.TTVaccinationEnrollmentMapper;
import org.motechproject.ghana.national.vo.ANCVisit;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION_VISIT;

@Service
public class MotherVisitService extends BaseScheduleService {

    private EncounterService encounterService;

    @Autowired
    public MotherVisitService(EncounterService encounterService, ScheduleTrackingService scheduleTrackingService) {
        super(scheduleTrackingService);
        this.encounterService = encounterService;
    }

    public MRSEncounter registerANCVisit(ANCVisit ancVisit) {
        return encounterService.persistEncounter(new MotherVisitEncounterFactory().createEncounter(ancVisit));
    }

    public void receivedTT(final TTVaccineDosage dosage, Patient patient, String staffId, String facilityId, final LocalDate vaccinationDate) {
        new TTVaccinationVisitEncounterFactory().createEncounterForVisit(encounterService, dosage, patient, staffId, facilityId, vaccinationDate);
        final EnrollmentRequest enrollmentRequest = new TTVaccinationEnrollmentMapper().map(patient, vaccinationDate, dosage.getScheduleMilestoneName());
        scheduleAlerts(patient, enrollmentRequest);
    }

    public void unScheduleAll(Patient patient) {
        scheduleTrackingService.unenroll(patient.getMRSPatientId(), TT_VACCINATION_VISIT);
    }
}
