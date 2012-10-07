package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.factory.PregnancyEncounterFactory;
import org.motechproject.ghana.national.repository.*;
import org.motechproject.ghana.national.service.request.DeliveredChildRequest;
import org.motechproject.ghana.national.service.request.PregnancyDeliveryRequest;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.ghana.national.vo.CwcVO;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.motechproject.ghana.national.domain.Concept.EDD;
import static org.motechproject.ghana.national.domain.Concept.PREGNANCY;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.REGISTER_SUCCESS_SMS_KEY;

@Service
public class PregnancyService {

    public static final String PREGNANCY_TERMINATION = "Pregnancy Termination";
    public static final String OTHER_CAUSE_OF_DEATH = "OTHER";

    private AllPatients allPatients;
    private AllEncounters allEncounters;
    private AllAppointmentsAndMessages allAppointmentsAndMessages;
    private IdentifierGenerator identifierGenerator;
    private PregnancyEncounterFactory encounterFactory;
    private AllObservations allObservations;
    private CareService careService;
    private SMSGateway smsGateway;
    private PatientService patientService;
    private MobileMidwifeService mobileMidwifeService;
    private AllSchedulesAndMessages allSchedulesAndMessages;

    @Autowired
    public PregnancyService(AllPatients allPatients, AllEncounters allEncounters,
                            AllAppointmentsAndMessages allAppointmentsAndMessages, IdentifierGenerator identifierGenerator,
                            AllObservations allObservations, CareService careService, SMSGateway smsGateway, PatientService patientService,
                            MobileMidwifeService mobileMidwifeService, AllSchedulesAndMessages allSchedulesAndMessages) {
        this.allPatients = allPatients;
        this.allEncounters = allEncounters;
        this.allAppointmentsAndMessages = allAppointmentsAndMessages;
        this.identifierGenerator = identifierGenerator;
        this.allObservations = allObservations;
        this.careService = careService;
        this.smsGateway = smsGateway;
        this.patientService = patientService;
        this.mobileMidwifeService = mobileMidwifeService;
        this.allSchedulesAndMessages = allSchedulesAndMessages;
        encounterFactory = new PregnancyEncounterFactory();
    }

    public void terminatePregnancy(PregnancyTerminationRequest request) throws PatientNotFoundException {
        allEncounters.persistEncounter(encounterFactory.createTerminationEncounter(request, allObservations.activePregnancyObservation(request.getPatient().getMotechId(), request.getTerminationDate())));
        if (request.isDead()) {
            MobileMidwifeEnrollment enrollment = mobileMidwifeService.findActiveBy(request.getPatient().getMotechId());
            if(enrollment != null && ServiceType.PREGNANCY.equals(enrollment.getServiceType()))
                mobileMidwifeService.unRegister(request.getPatient().getMotechId());
            patientService.deceasePatient(request.getTerminationDate(), request.getPatient().getMotechId(), OTHER_CAUSE_OF_DEATH, PREGNANCY_TERMINATION);

        } else {
            allSchedulesAndMessages.unEnroll(request.getPatient().getMRSPatientId(), Patient.ancCarePrograms);
            allAppointmentsAndMessages.remove(request.getPatient());
        }
    }

    private Patient registerChild(DeliveredChildRequest childRequest, Date birthDate, String parentMotechId, Facility facility) {
        String childMotechId = childRequest.getChildMotechId();
        if (childRequest.getChildRegistrationType().equals(RegistrationType.AUTO_GENERATE_ID)) {
            childMotechId = identifierGenerator.newPatientId();
        }
        MRSPerson childPerson = new MRSPerson();
        childPerson.firstName((childRequest.getChildFirstName() != null) ? childRequest.getChildFirstName() : "Baby")
                .lastName("Baby").dateOfBirth(birthDate).birthDateEstimated(false)
                .gender((childRequest.getChildSex() != null) ? childRequest.getChildSex() : "?");
        Patient child = new Patient(new MRSPatient(childMotechId, childPerson, facility.mrsFacility()), parentMotechId);
        return allPatients.save(child);
    }

    public void handleDelivery(PregnancyDeliveryRequest request) {
        Facility facility = request.getFacility();
        MRSUser staff = request.getStaff();
        Patient patient = request.getPatient();

        MRSObservation activePregnancyObservation = allObservations.activePregnancyObservation(patient.getMotechId(), request.getDeliveryDateTime().toDate());
        allEncounters.persistEncounter(encounterFactory.createDeliveryEncounter(request, activePregnancyObservation));
        allSchedulesAndMessages.safeFulfilCurrentMilestone(patient.getMRSPatientId(), ScheduleNames.ANC_DELIVERY.getName(), request.getDeliveryDateTime().toLocalDate());
        allSchedulesAndMessages.unEnroll(patient.getMRSPatientId(), Patient.ancCarePrograms);
        allAppointmentsAndMessages.remove(patient);

        List<SMSTemplate> smsForEachChild = new ArrayList<SMSTemplate>();

        for (DeliveredChildRequest childRequest : request.getDeliveredChildRequests()) {
            if (childRequest.getChildBirthOutcome().equals(BirthOutcome.ALIVE)) {
                Date birthDate = request.getDeliveryDateTime().toDate();
                final Patient savedChild = registerChild(childRequest, birthDate, patient.getMotechId(), facility);
                allEncounters.persistEncounter(encounterFactory.createBirthEncounter(childRequest, savedChild.getMrsPatient(), staff, facility, birthDate));
                careService.enroll(new CwcVO(staff.getSystemId(), facility.mrsFacilityId(), birthDate, savedChild.getMotechId(),
                        Collections.<CwcCareHistory>emptyList(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, savedChild.getMotechId(), false));
                careService.enrollChildForPNC(savedChild);
                smsForEachChild.add(new SMSTemplate().fillPatientDetails(savedChild));
            }
        }
        for (SMSTemplate smsTemplate : smsForEachChild) {
            smsGateway.dispatchSMS(REGISTER_SUCCESS_SMS_KEY, smsTemplate.getRuntimeVariables(), request.getSender());
        }
    }

    public Date activePregnancyEDD(String motechId) {
        MRSObservation pregObservation = allObservations.findLatestObservation(motechId, PREGNANCY.getName());
        if (pregObservation != null && pregObservation.getDependantObservations() != null) {
            Set<MRSObservation> dependentObservations = pregObservation.getDependantObservations();
            for (MRSObservation observation : dependentObservations) {
                if (EDD.getName().equals(observation.getConceptName())) {
                    return (Date) observation.getValue();
                }
            }
        }
        return null;
    }

    public boolean isDeliverySuccessful(PregnancyDeliveryRequest deliveryRequest) {
        for (DeliveredChildRequest childRequest : deliveryRequest.getDeliveredChildRequests()) {
            if (childRequest.getChildBirthOutcome().equals(BirthOutcome.ALIVE)) {
                return true;
            }
        }
        return false;
    }

}
