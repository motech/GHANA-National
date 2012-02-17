package org.motechproject.ghana.national.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.SMS;
import org.motechproject.sms.api.service.SmsService;

import java.util.HashMap;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class TextMessageServiceTest {

    TextMessageService textMessageService;
    @Mock
    CMSLiteService mockCMSLiteService;
    @Mock
    private SmsService mockSMSService;

    @Before
    public void init() {
        initMocks(this);
        textMessageService = new TextMessageService();
        setField(textMessageService, "smsService", mockSMSService);
        setField(textMessageService, "cmsLiteService", mockCMSLiteService);
    }
                                      
    @Test
    public void shouldSendSMSToAFacility() {
        Facility facility = mock(Facility.class);
        String facilityPhoneNumber = "phone number";
        String smsText = "sms message";
        when(facility.phoneNumber()).thenReturn(facilityPhoneNumber);
        textMessageService.sendSMS(facility, SMS.fromSMSText(smsText));
        verify(mockSMSService).sendSMS(facilityPhoneNumber, smsText);
    }

    @Test
    public void shouldSendSMSToAPhoneNumber() throws ContentNotFoundException {
        String phoneNumber = "phone number";
        String smsText = "sms message";
        textMessageService.sendSMS(phoneNumber, SMS.fromSMSText(smsText));
        verify(mockSMSService).sendSMS(phoneNumber, smsText);
    }

    @Test
    public void shouldCreateSMSMessageTextFromMessageTemplateKeyDefaultLocaleAndRuntimeParameters() throws ContentNotFoundException {
        String templateKey = "template key";
        StringContent stringConent = mock(StringContent.class);
        when(stringConent.getValue()).thenReturn("placeholder");
        when(mockCMSLiteService.getStringContent(Locale.getDefault().getLanguage(), templateKey)).thenReturn(stringConent);
        SMS sms = textMessageService.getSMS(templateKey, new HashMap<String, String>() {{
            put("placeholder", "value");
        }});
        assertThat(sms, is(equalTo(SMS.fromSMSText("value"))));
    }
    
    @Test
    public void shouldCreateSMSMessageTextFromMessageTemplateKey_GivenLanguageAndRuntimeParameters() throws ContentNotFoundException {
        String templateKey = "template key";
        StringContent stringConent = mock(StringContent.class);
        when(stringConent.getValue()).thenReturn("placeholder");
        String language = "EN";
        when(mockCMSLiteService.getStringContent(language, templateKey)).thenReturn(stringConent);
        SMS sms = textMessageService.getSMS(language, templateKey, new HashMap<String, String>() {{
            put("placeholder", "value");
        }});
        assertThat(sms, is(equalTo(SMS.fromSMSText("value"))));
    }    
}
