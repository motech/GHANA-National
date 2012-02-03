package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.domain.Configuration;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllConfigurations;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.sms.api.service.SmsService;

import java.util.Locale;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class TextMessageServiceTest {

    TextMessageService textMessageService;
    @Mock
    AllConfigurations mockAllConfigurations;
    @Mock
    CMSLiteService mockCMSLiteService;

    @Mock
    private SmsService mockSMSService;

    @Before
    public void init() {
        initMocks(this);
        textMessageService = new TextMessageService();
        setField(textMessageService, "allConfigurations", mockAllConfigurations);
        setField(textMessageService, "smsService", mockSMSService);
        setField(textMessageService, "cmsLiteService", mockCMSLiteService);
    }
                                      
    @Test
    public void shouldConstructAndSendMessage() {

        String motechId = "021";
        String firstName = "test";
        String lastName = "Charlie";
        Patient patient = patient(motechId, firstName, lastName);
              
        Configuration configuration = new Configuration();
        configuration.setPropertyName("SMS");
        configuration.setValue("test message ${motechId} ${firstName} ${lastName}");
        when(mockAllConfigurations.getConfigurationValue(Constants.REGISTER_SUCCESS_SMS)).thenReturn(configuration);

        String someRecipient = "someRecipient";
        textMessageService.sendSMS(someRecipient, patient, Constants.REGISTER_SUCCESS_SMS);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockSMSService).sendSMS(eq(someRecipient), messageCaptor.capture());
        String message = messageCaptor.getValue();

        assertTrue(message.contains(motechId));
        assertTrue(message.contains(firstName));
        assertTrue(message.contains(lastName));
    }

    @Test
    public void shouldSendMessageFromCMSLite() throws ContentNotFoundException {
        String recepientNo = "9500011223";
        Patient patient = patient("motechId", "Sach", "Tend");
        String messageKey = "messageKey";
        String smsText = "smstext";
        when(mockCMSLiteService.getStringContent(Locale.getDefault().getLanguage(), messageKey)).thenReturn(new StringContent("","", smsText));
        textMessageService.sendLocalizedSMS(recepientNo, patient, messageKey);
        
        verify(mockSMSService).sendSMS(recepientNo, smsText);
    }

    private Patient patient(String motechId, String firstName, String lastName) {
        MRSPatient mrsPatient = new MRSPatient(motechId, new MRSPerson().firstName(firstName).lastName(lastName), null);
        return new Patient(mrsPatient);
    }
}
