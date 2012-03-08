package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.factory.PregnancyEncounterFactory;
import org.motechproject.ghana.national.repository.*;
import org.motechproject.ghana.national.service.request.DeliveredChildRequest;
import org.motechproject.ghana.national.service.request.PregnancyDeliveryRequest;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
    private PregnancyEncounterFactory encounterFactory;
    private AllObservations allObservations;
    private CareService careService;

    @Autowired
    public PregnancyService(AllPatients allPatients, AllEncounters allEncounters,
                            AllSchedules allSchedules, AllAppointments allAppointments, IdentifierGenerator identifierGenerator, AllObservations allObservations, CareService careService) {
        this.allPatients = allPatients;
        this.allEncounters = allEncounters;
        this.allSchedules = allSchedules;
        this.allAppointments = allAppointments;
        this.identifierGenerator = identifierGenerator;
        this.allObservations = allObservations;
        this.careService = careService;
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
                        .lastName("Baby").dateOfBirth(birthDate).birthDateEstimated(false)
                        .gender((childRequest.getChildSex() != null) ? childRequest.getChildSex() : "?");
                Patient child = new Patient(new MRSPatient(childMotechId, childPerson, facility.mrsFacility()), request.getPatient().getMotechId());
                final Patient savedChild = allPatients.save(child);
                allEncounters.persistEncounter(encounterFactory.createBirthEncounter(childRequest, savedChild.getMrsPatient(), staff, facility, birthDate));
                careService.enroll(new CwcVO(staff.getSystemId(), facility.mrsFacilityId(), birthDate, savedChild.getMotechId(),
                        Collections.<CwcCareHistory>emptyList(), null, null, null, null, null, null, null, null, null, null, savedChild.getMotechId(), false));
            }
        }
        MRSObservation activePregnancyObservation = allObservations.activePregnancyObservation(request.getPatient().getMotechId());
        allEncounters.persistEncounter(encounterFactory.createDeliveryEncounter(request, activePregnancyObservation));
        allSchedules.unEnroll(request.getPatient().getMRSPatientId(), request.getPatient().ancCareProgramsToUnEnroll());
        allAppointments.remove(request.getPatient());
    }
}
