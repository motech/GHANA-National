package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.AggregationMessageIdentifier;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.messagegateway.service.MessageGateway;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.util.DateUtil;

import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_VISIT;

public class AllAppointmentsAndMessagesTest {
    @Mock
    private AllAppointments mockAllAppointments;
    @Mock
    private MessageGateway messageGateway;
    private AllAppointmentsAndMessages allAppointmentsAndMessages;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        allAppointmentsAndMessages = new AllAppointmentsAndMessages(mockAllAppointments, messageGateway);
    }

    @Test
    public void shouldDeleteMessagesInAggregationWhileRemovingAppointment(){
        String patientId = "patientId";
        Patient patient = new Patient(new MRSPatient(patientId));
        allAppointmentsAndMessages.remove(patient);

        verify(mockAllAppointments).remove(patient);
        verify(messageGateway).delete(new AggregationMessageIdentifier(patientId, ANC_VISIT.value()).getIdentifier());
    }

    @Test
    public void shouldDeleteMessagesInAggregationWhileFulfillingAppointment(){
        String patientId = "patientId";
        Patient patient = new Patient(new MRSPatient(patientId));
        Date visitDate = DateUtil.today().toDate();
        allAppointmentsAndMessages.fulfillCurrentANCVisit(patient, visitDate);
        verify(mockAllAppointments).fulfillCurrentANCVisit(patient, visitDate);
        verify(messageGateway).delete(new AggregationMessageIdentifier(patientId, ANC_VISIT.value()).getIdentifier());
    }
}
