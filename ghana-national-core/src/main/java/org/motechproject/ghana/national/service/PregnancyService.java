package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.BirthOutcome;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.factory.PregnancyEncounterFactory;
import org.motechproject.ghana.national.repository.*;
import org.motechproject.ghana.national.service.request.DeliveredChildRequest;
import org.motechproject.ghana.national.service.request.PregnancyDeliveryRequest;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PregnancyService {

    public static final String PREGNANCY_TERMINATION = "Pregnancy Termination";
    public static final String OTHER_CAUSE_OF_DEATH = "OTHER";

    private AllPatients allPatients;
    private AllEncounters allEncounters;
    private AllSchedules allSchedules;
    private AllAppointments allAppointments;
    private IdentifierGenerator identifierGenerator;
    PregnancyEncounterFactory encounterFactory;
    private AllObservations allObservations;

    @Autowired
    public PregnancyService(AllPatients allPatients, AllEncounters allEncounters,
                            AllSchedules allSchedules, AllAppointments allAppointments, IdentifierGenerator identifierGenerator, AllObservations allObservations) {
        this.allPatients = allPatients;
        this.allEncounters = allEncounters;
        this.allSchedules = allSchedules;
        this.allAppointments = allAppointments;
        this.identifierGenerator = identifierGenerator;
        this.allObservations = allObservations;
        encounterFactory = new PregnancyEncounterFactory();
    }

    public void terminatePregnancy(PregnancyTerminationRequest request) {
        allEncounters.persistEncounter(encounterFactory.createTerminationEncounter(request));
        if (request.isDead()) {
            allPatients.deceasePatient(request.getTerminationDate(), request.getPatient().getMotechId(), OTHER_CAUSE_OF_DEATH, PREGNANCY_TERMINATION);
        }
        allSchedules.unEnroll(request.getPatient().getMRSPatientId(), request.getPatient().ancCareProgramsToUnEnroll());
        allAppointments.remove(request.getPatient());
    }

    public void handleDelivery(PregnancyDeliveryRequest request) {
        Facility facility = request.getFacility();
        MRSUser staff = request.getStaff();
        for (DeliveredChildRequest childRequest : request.getDeliveredChildRequests()) {
            if (childRequest.getChildBirthOutcome().equals(BirthOutcome.ALIVE)) {
                String childMotechId = childRequest.getChildMotechId();
                if (childRequest.getChildRegistrationType().equals(RegistrationType.AUTO_GENERATE_ID)) {
                    childMotechId = identifierGenerator.newPatientId();
                }
                MRSPerson childPerson = new MRSPerson();
                Date birthDate = request.getDeliveryDateTime().toDate();
                childPerson.firstName((childRequest.getChildFirstName() != null) ? childRequest.getChildFirstName() : "Baby")
                        .lastName("Baby").dateOfBirth(birthDate)
                        .gender((childRequest.getChildSex() != null) ? childRequest.getChildSex() : "?");
                Patient patient = new Patient(new MRSPatient(childMotechId, childPerson, facility.mrsFacility()), request.getPatient().getMotechId());
                allPatients.save(patient);
                allEncounters.persistEncounter(encounterFactory.createBirthEncounter(childRequest, patient.getMrsPatient(), staff, facility, birthDate));
            }
        }
        MRSObservation activePregnancyObservation = allObservations.activePregnancyObservation(request.getPatient().getMotechId());
        allEncounters.persistEncounter(encounterFactory.createDeliveryEncounter(request, activePregnancyObservation));
        allSchedules.unEnroll(request.getPatient().getMRSPatientId(), request.getPatient().ancCareProgramsToUnEnroll());
        allAppointments.remove(request.getPatient());
    }
}
