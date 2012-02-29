package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.factory.PregnancyTerminationEncounterFactory;
import org.motechproject.ghana.national.repository.AllAppointments;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.service.request.PregnancyDeliveryRequest;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PregnancyService {

    public static final String PREGNANCY_TERMINATION = "Pregnancy Termination";
    public static final String OTHER_CAUSE_OF_DEATH = "OTHER";

    private AllPatients allPatients;
    private AllEncounters allEncounters;
    private AllSchedules allSchedules;
    private AllAppointments allAppointments;
    PregnancyTerminationEncounterFactory encounterFactory;

    @Autowired
    public PregnancyService(AllPatients allPatients, AllEncounters allEncounters,
                            AllSchedules allSchedules, AllAppointments allAppointments) {
        this.allPatients = allPatients;
        this.allEncounters = allEncounters;
        this.allSchedules = allSchedules;
        this.allAppointments = allAppointments;
        encounterFactory = new PregnancyTerminationEncounterFactory();
    }

    public void terminatePregnancy(PregnancyTerminationRequest request) {
        allEncounters.persistEncounter(encounterFactory.createEncounter(request));
        if (request.isDead()) {
            allPatients.deceasePatient(request.getTerminationDate(), request.getPatient().getMotechId(), OTHER_CAUSE_OF_DEATH, PREGNANCY_TERMINATION);
        }
        allSchedules.unEnroll(request.getPatient().getMRSPatientId(), request.getPatient().careProgramsToUnEnroll());
        allAppointments.remove(request.getPatient());
    }

    public void handleDelivery(PregnancyDeliveryRequest request) {
        Patient patient = allPatients.getPatientByMotechId(request.getMotechId());

//        allEncounters.persistEncounter()
        allSchedules.unEnroll(patient.getMRSPatientId(), patient.careProgramsToUnEnroll());
        allAppointments.remove(patient);
    }
}
