package org.motechproject.ghana.national.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.ivr.MobileMidwifeAudioClips;
import org.motechproject.ghana.national.messagegateway.domain.Payload;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AggregatedMessageHandlerImplTest {
    @Mock
    private AllPatientsOutbox mockAllPatientsOutbox;
    @Mock
    private SMSGateway mockSMSGateway;
    private AggregatedMessageHandlerImpl aggregatedMessageHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        aggregatedMessageHandler = new AggregatedMessageHandlerImpl(mockSMSGateway, mockAllPatientsOutbox);
    }

    @Test
    public void shouldDispatchMessagesViaCorrespondingHandlers() {
        DateTime now = DateTime.now();
        String careClipName = "clipName";
        MobileMidwifeAudioClips mmClipName = MobileMidwifeAudioClips.PREGNANCY_WEEK_7;
        String motechId = "motechId";
        String appointmentClipName = "appointmentClipName";

        DateTime scheduleWindowStart = DateUtil.newDateTime(2000, 1, 1);
        CareVoicePayload voicePayload = new CareVoicePayload(careClipName, motechId, now, null, Period.weeks(1), AlertWindow.DUE, scheduleWindowStart);
        MobileMidwifeVoicePayload mobileMidwifeVoicePayload = new MobileMidwifeVoicePayload(mmClipName, motechId, now, null, Period.weeks(2));
        AppointmentVoicePayload appointmentVoicePayload = new AppointmentVoicePayload(appointmentClipName, motechId, now, null, Period.weeks(3));

        String smsText = "text";
        String phoneNumber = "phoneNumber";
        SMSPayload smsPayload = SMSPayload.fromPhoneNoAndText(phoneNumber, smsText);
        aggregatedMessageHandler.handle(Arrays.<Payload>asList(smsPayload, voicePayload, mobileMidwifeVoicePayload, appointmentVoicePayload));
        verify(mockAllPatientsOutbox).addCareMessage(motechId, careClipName, Period.weeks(1), AlertWindow.DUE, scheduleWindowStart);
        verify(mockAllPatientsOutbox).addMobileMidwifeMessage(motechId, mmClipName, Period.weeks(2));
        verify(mockAllPatientsOutbox).addAppointmentMessage(motechId, appointmentClipName, Period.weeks(3));
        verify(mockSMSGateway).dispatchSMS(phoneNumber, smsText);
    }
}
