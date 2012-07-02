package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.AggregationMessageIdentifier;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.messagegateway.service.MessageGateway;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.outbox.api.contract.SortKey;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_VISIT;

public class AllAppointmentsAndMessagesTest {
    @Mock
    private AllAppointments mockAllAppointments;
    @Mock
    private MessageGateway messageGateway;

    private AllAppointmentsAndMessages allAppointmentsAndMessages;
    @Mock
    private AllPatientsOutbox allPatientsOutbox;
    @Mock
    private VoiceOutboxService voiceOutboxService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        allAppointmentsAndMessages = new AllAppointmentsAndMessages(mockAllAppointments, messageGateway,allPatientsOutbox);
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
        String motechId = "motechId";
        Patient patient = new Patient(new MRSPatient(patientId,motechId,new MRSPerson(),new MRSFacility("fid")));
        Date visitDate = DateUtil.today().toDate();
        OutboundVoiceMessage outboundVoiceMessage = mock(OutboundVoiceMessage.class);
        when(voiceOutboxService.getMessages(patientId, OutboundVoiceMessageStatus.PENDING, SortKey.CreationTime)).thenReturn(Arrays.asList(outboundVoiceMessage));
        when(outboundVoiceMessage.getParameters()).thenReturn(new HashMap<String, Object>() {{
            put("AUDIO_CLIP_NAME", "prompt_ANCVISIT_XX");
        }});
        allAppointmentsAndMessages.fulfillCurrentANCVisit(patient, visitDate);
        verify(mockAllAppointments).fulfillCurrentANCVisit(patient, visitDate);
        verify(messageGateway).delete(new AggregationMessageIdentifier(patientId, ANC_VISIT.value()).getIdentifier());
        verify(allPatientsOutbox).removeCareAndAppointmentMessages(patientId, ANC_VISIT.value());
    }
}
