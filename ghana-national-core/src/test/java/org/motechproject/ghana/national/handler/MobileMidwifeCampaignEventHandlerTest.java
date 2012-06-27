package org.motechproject.ghana.national.handler;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.ghana.national.builder.IVRCallbackUrlBuilder;
import org.motechproject.ghana.national.builder.IVRRequestBuilder;
import org.motechproject.ghana.national.domain.ivr.MobileMidwifeAudioClips;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.exception.EventHandlerException;
import org.motechproject.ghana.national.repository.AllPatientsOutbox;
import org.motechproject.ghana.national.repository.IVRGateway;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.retry.domain.RetryRequest;
import org.motechproject.retry.service.RetryService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.joda.time.DateTime.now;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.messagecampaign.scheduler.MessageCampaignScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT;

public class MobileMidwifeCampaignEventHandlerTest extends BaseUnitTest{

    MobileMidwifeCampaignEventHandler handler = new MobileMidwifeCampaignEventHandler();
    @Mock
    MobileMidwifeService mockMobileMidwifeService;
    @Mock
    SMSGateway mockSMSGateway;
    @Mock
    IVRGateway mockIVRGateway;
    @Mock
    AllPatientsOutbox mockAllPatientsOutbox;
    @Mock
    IVRCallbackUrlBuilder mockIVRCallbackUrlBuilder;
    @Mock
    RetryService mockRetryService;
    private DateTime now = DateUtil.now();

    @Before
    public void init() {
        initMocks(this);
        ReflectionTestUtils.setField(handler, "ivrCallbackUrlBuilder", mockIVRCallbackUrlBuilder);
        ReflectionTestUtils.setField(handler, "mobileMidwifeService", mockMobileMidwifeService);
        ReflectionTestUtils.setField(handler, "smsGateway", mockSMSGateway);
        ReflectionTestUtils.setField(handler, "ivrGateway", mockIVRGateway);
        ReflectionTestUtils.setField(handler, "allPatientsOutbox", mockAllPatientsOutbox);
        ReflectionTestUtils.setField(handler, "retryService", mockRetryService);
        super.mockCurrentDate(now);

    }

    @Test
    public void shouldSendSMSForEnrollmentWithSMSMedium() throws ContentNotFoundException {
        ServiceType serviceType = ServiceType.CHILD_CARE;
        String patientId = "1234568";
        String mobileNumber = "9845312345";
        String genMessageKey = "childcare-calendar-week-33-Monday";
        Language language = Language.EN;
        MobileMidwifeEnrollment mobileMidwifeEnrollment = new MobileMidwifeEnrollment(now()).setPatientId(patientId)
                .setServiceType(serviceType).setMedium(Medium.SMS).setLanguage(language).setPhoneNumber(mobileNumber);

        when(mockMobileMidwifeService.findActiveBy(patientId)).thenReturn(mobileMidwifeEnrollment);

        handler.sendProgramMessage(motechEvent(patientId, serviceType.name(), genMessageKey));
        verify(mockSMSGateway).dispatchSMS(genMessageKey, language.name(), mobileNumber);
    }

    @Test
    public void shouldSendUnRegisteredUserIfItIsTheLastEventForTheProgram() throws ContentNotFoundException {
        ServiceType serviceType = ServiceType.CHILD_CARE;
        String patientId = "1234568";
        String genMessageKey = "childcare-calendar-week-33-Monday";
        Language language = Language.EN;
        MobileMidwifeEnrollment mobileMidwifeEnrollment = new MobileMidwifeEnrollment(now()).setPatientId(patientId)
                .setServiceType(serviceType).setMedium(Medium.SMS).setLanguage(language).setPhoneNumber("9845312345");
        when(mockMobileMidwifeService.findActiveBy(patientId)).thenReturn(mobileMidwifeEnrollment);

        MotechEvent lastEvent = motechEvent(patientId, serviceType.name(), genMessageKey).setLastEvent(true);
        handler.sendProgramMessage(lastEvent);
        ArgumentCaptor<DateTime> dateArgumentCaptor = ArgumentCaptor.forClass(DateTime.class);
        ArgumentCaptor<String> idArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockMobileMidwifeService).rollover(idArgumentCaptor.capture(),dateArgumentCaptor.capture());
        assertThat(idArgumentCaptor.getValue(),is(equalTo(patientId)));
        assertThat(dateArgumentCaptor.getValue().toLocalDate(),is(equalTo(DateTime.now().toLocalDate())));
    }

    @Test
    public void shouldThrowOnAnyFailureInHandlingAlerts() {
        doThrow(new RuntimeException("some")).when(mockMobileMidwifeService).findActiveBy(anyString());
        final MotechEvent event = new MotechEvent("subjectMM", new HashMap<String, Object>());
        try {
            handler.sendProgramMessage(event);
            Assert.fail("expected scheduler handler exception");
        } catch (EventHandlerException she) {
            assertThat(she.getMessage(), is(event.toString()));
        }
    }

    @Test
    public void shouldPlaceCallWhenTheIVRCallEventIsReceived() {
        ServiceType serviceType = ServiceType.PREGNANCY;
        String patientId = "1234568";
        String genMessageKey = "33";
        final String url = "http://ivr";
        Language language = Language.EN;

        MobileMidwifeEnrollment mobileMidwifeEnrollment = new MobileMidwifeEnrollment(now()).setPatientId(patientId)
                .setServiceType(serviceType).setMedium(Medium.VOICE).setPhoneNumber("9845312345").setLanguage(language);
        when(mockMobileMidwifeService.findActiveBy(patientId)).thenReturn(mobileMidwifeEnrollment);
        when(mockIVRCallbackUrlBuilder.outboundCallUrl(patientId, language.name(), "OutboundDecisionTree")).thenReturn(url);

        handler.sendProgramMessage(motechEvent(patientId, serviceType.name(), genMessageKey));

        verify(mockAllPatientsOutbox).addMobileMidwifeMessage(patientId, MobileMidwifeAudioClips.instance(mobileMidwifeEnrollment.getServiceType().getValue(), genMessageKey), Period.weeks(1));
        verify(mockRetryService).schedule(eq(new RetryRequest("retry-ivr-every-2hrs-and-30mins", patientId, now)));
        verify(mockIVRGateway).placeCall(mobileMidwifeEnrollment.getPhoneNumber(), new HashMap<String, String>() {{
            put(IVRRequestBuilder.CALLBACK_URL, url);
        }});
    }

    private MotechEvent motechEvent(String externalId, String campaignName, String genMessageKey) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.CAMPAIGN_NAME_KEY, campaignName);
        parameters.put(EventKeys.GENERATED_MESSAGE_KEY, genMessageKey);
        parameters.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        return new MotechEvent(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, parameters);
    }
}
