package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.messagegateway.domain.Payload;
import org.motechproject.ghana.national.messagegateway.domain.SMSPayload;
import org.motechproject.ghana.national.messagegateway.domain.VoicePayload;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.sms.api.service.SmsService;

import java.util.Arrays;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AggregatedMessageHandlerImplTest {
    @Mock
    private AllPatientsOutbox mockAllPatientsOutbox;
    @Mock
    private SmsService mockSMSService;
    private AggregatedMessageHandlerImpl aggregatedMessageHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        aggregatedMessageHandler = new AggregatedMessageHandlerImpl(mockSMSService, mockAllPatientsOutbox);
    }

    @Test
    public void shouldDespatchMessagesViaCorrespondingHandlers(){
        DateTime now = DateTime.now();
        String clipName = "clipName";
        String motechId = "motechId";

        VoicePayload voicePayload = new VoicePayload(clipName, motechId, now, null, Period.weeks(1));
        String smsText = "text";
        String phoneNumber = "phoneNumber";
        SMSPayload smsPayload = SMSPayload.fromPhoneNoAndText(phoneNumber, smsText);
        aggregatedMessageHandler.handle(Arrays.<Payload>asList(smsPayload, voicePayload));
        verify(mockAllPatientsOutbox).addAudioClip(motechId, clipName, Period.weeks(1));
        verify(mockSMSService).sendSMS(phoneNumber, smsText);
    }
}
