package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Configuration;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllConfigurations;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.sms.api.service.SmsService;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class TextMessageServiceTest {

    TextMessageService textMessageService;
    @Mock
    AllConfigurations mockAllConfigurations;

    @Mock
    private SmsService mockSMSService;

    @Before
    public void init() {
        initMocks(this);
        textMessageService = new TextMessageService();
        setField(textMessageService, "allConfigurations", mockAllConfigurations);
        setField(textMessageService, "smsService", mockSMSService);

    }

    @Test
    public void shouldConstructAndSendMessage() {

        Patient mockPatient = mock(Patient.class);
        MRSPatient mockMRSPatient = mock(MRSPatient.class);
        MRSPerson mockPerson = mock(MRSPerson.class);
        String motechId = "021";
        String firstName = "test";
        String lastName = "Charlie";

        when(mockPatient.getMrsPatient()).thenReturn(mockMRSPatient);
        when(mockMRSPatient.getPerson()).thenReturn(mockPerson);
        when(mockMRSPatient.getMotechId()).thenReturn(motechId);
        when(mockPerson.getFirstName()).thenReturn(firstName);
        when(mockPerson.getLastName()).thenReturn(lastName);

        Configuration configuration = new Configuration();
        configuration.setPropertyName("SMS");
        configuration.setValue("test message ${motechId} ${firstName} ${lastName}");
        when(mockAllConfigurations.getConfigurationValue("REGISTER_SUCCESS_SMS")).thenReturn(configuration);

        String someRecipient = "someRecipient";
        textMessageService.sendSMS(someRecipient, mockPatient, "REGISTER_SUCCESS_SMS");
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSMSService).sendSMS(eq(someRecipient), messageCaptor.capture());
        String message = messageCaptor.getValue();

        assertTrue(message.contains(motechId));
        assertTrue(message.contains(firstName));
        assertTrue(message.contains(lastName));
    }

}
