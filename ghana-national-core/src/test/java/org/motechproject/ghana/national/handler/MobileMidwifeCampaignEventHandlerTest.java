package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.messagecampaign.scheduler.MessageCampaignScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class MobileMidwifeCampaignEventHandlerTest {

    MobileMidwifeCampaignEventHandler handler;
    @Mock
    MobileMidwifeService mockMobileMidwifeService;
    @Mock
    PatientService mockPatientService;
    @Mock
    SMSGateway mockSMSGateway;

    @Before
    public void init() {
        initMocks(this);
        handler = new MobileMidwifeCampaignEventHandler();
        setField(handler, "mobileMidwifeService", mockMobileMidwifeService);
        setField(handler, "smsGateway", mockSMSGateway);
        setField(handler, "patientService", mockPatientService);
    }

    @Test
    public void shouldSendSMSForEnrollmentWithSMSMedium() throws ContentNotFoundException {
        ServiceType serviceType = ServiceType.CHILD_CARE;
        String patientId = "1234568";
        String mobileNumber = "9845312345";
        String messageKey = "childcare-calendar-week-33-Monday";
        String messageTemplate = "text-message";
        Language language = Language.EN;
        MobileMidwifeEnrollment mobileMidwifeEnrollment = MobileMidwifeEnrollment.newEnrollment().setPatientId(patientId)
                .setServiceType(serviceType).setMedium(Medium.SMS).setLanguage(language).setPhoneNumber(mobileNumber);

        when(mockMobileMidwifeService.findActiveBy(patientId)).thenReturn(mobileMidwifeEnrollment);

        handler.sendProgramMessage(motechEvent(patientId, serviceType.name(), messageKey));
        verify(mockSMSGateway).dispatchSMS(messageKey, language.name(), mobileNumber);
    }

    @Test
    public void shouldNotSendSMSForEnrollmentWithNonSMSMedium() throws ContentNotFoundException {
        ServiceType serviceType = ServiceType.PREGNANCY;
        String patientId = "1234568";
        String messageKey = "pregnancy-calendar-week-33-Monday";
        MobileMidwifeEnrollment mobileMidwifeEnrollment = MobileMidwifeEnrollment.newEnrollment().setPatientId(patientId)
                .setServiceType(serviceType).setMedium(Medium.VOICE).setPhoneNumber("9845312345");
        when(mockMobileMidwifeService.findActiveBy(patientId)).thenReturn(mobileMidwifeEnrollment);

        handler.sendProgramMessage(motechEvent(patientId, serviceType.name(), messageKey));
        verify(mockSMSGateway, never()).dispatchSMS(anyString(), anyString(), anyString());
    }

    @Test
    public void shouldSendUnRegisteredUserIfItIsTheLastEventForTheProgram() throws ContentNotFoundException {
        ServiceType serviceType = ServiceType.CHILD_CARE;
        String patientId = "1234568";
        String messageKey = "childcare-calendar-week-33-Monday";
        Language language = Language.EN;
        MobileMidwifeEnrollment mobileMidwifeEnrollment = MobileMidwifeEnrollment.newEnrollment().setPatientId(patientId)
                .setServiceType(serviceType).setMedium(Medium.SMS).setLanguage(language).setPhoneNumber("9845312345");
        when(mockMobileMidwifeService.findActiveBy(patientId)).thenReturn(mobileMidwifeEnrollment);

        MotechEvent lastEvent = motechEvent(patientId, serviceType.name(), messageKey).setLastEvent(true);
        handler.sendProgramMessage(lastEvent);
        verify(mockMobileMidwifeService).unRegister(patientId);
    }

     private MotechEvent motechEvent(String externalId, String campaignName, String messageKey) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.CAMPAIGN_NAME_KEY, campaignName);
        parameters.put(EventKeys.MESSAGE_KEY, messageKey);
        parameters.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        return new MotechEvent(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, parameters);
    }

}
