package org.motechproject.ghana.national.handler;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.exception.EventHandlerException;
import org.motechproject.ghana.national.repository.IVRGateway;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.joda.time.DateTime.now;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.messagecampaign.scheduler.MessageCampaignScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT;

public class MobileMidwifeCampaignEventHandlerTest {

    @InjectMocks
    MobileMidwifeCampaignEventHandler handler = new MobileMidwifeCampaignEventHandler();
    @Mock
    MobileMidwifeService mockMobileMidwifeService;
    @Mock
    SMSGateway mockSMSGateway;
    @Mock
    IVRGateway mockIVRGateway;

    @Before
    public void init() {
        initMocks(this);
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
        verify(mockMobileMidwifeService).unRegister(patientId);
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
        String genMessageKey = "pregnancy-calendar-week-33-Monday";
        MobileMidwifeEnrollment mobileMidwifeEnrollment = new MobileMidwifeEnrollment(now()).setPatientId(patientId)
                .setServiceType(serviceType).setMedium(Medium.VOICE).setPhoneNumber("9845312345");
        when(mockMobileMidwifeService.findActiveBy(patientId)).thenReturn(mobileMidwifeEnrollment);

        handler.sendProgramMessage(motechEvent(patientId, serviceType.name(), genMessageKey));
        verify(mockIVRGateway).placeCall(mobileMidwifeEnrollment.getPhoneNumber());
    }

    private MotechEvent motechEvent(String externalId, String campaignName, String genMessageKey) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.CAMPAIGN_NAME_KEY, campaignName);
        parameters.put(EventKeys.GENERATED_MESSAGE_KEY, genMessageKey);
        parameters.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        return new MotechEvent(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, parameters);
    }
}
