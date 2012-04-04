package org.motechproject.ghana.national.repository;

import org.motechproject.ghana.national.domain.AggregationMessageIdentifier;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.messagegateway.service.MessageGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;

import static org.motechproject.ghana.national.domain.EncounterType.ANC_VISIT;

@Repository
public class AllAppointmentsAndMessages {

    private AllAppointments allAppointments;
    private MessageGateway messageGateway;

    @Autowired
    public AllAppointmentsAndMessages(AllAppointments allAppointments, MessageGateway messageGateway){
        this.allAppointments = allAppointments;
        this.messageGateway = messageGateway;
    }

    public void remove(Patient patient) {
        allAppointments.remove(patient);
        messageGateway.delete(new AggregationMessageIdentifier(patient.getMRSPatientId(), ANC_VISIT.value()).getIdentifier());
    }

    public void fulfillCurrentANCVisit(Patient patient, Date visitedDate) {
        allAppointments.fulfillCurrentANCVisit(patient, visitedDate);
        messageGateway.delete(new AggregationMessageIdentifier(patient.getMRSPatientId(), ANC_VISIT.value()).getIdentifier());
    }
}
