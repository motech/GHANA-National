package org.motechproject.ghana.national.eventhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.SMS;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.domain.mobilemidwife.Medium;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.domain.mobilemidwife.ServiceType;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.TextMessageService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
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
    TextMessageService mockTextMessageService;

    @Before
    public void init() {
        initMocks(this);
        handler = new MobileMidwifeCampaignEventHandler();
        setField(handler, "mobileMidwifeService", mockMobileMidwifeService);
        setField(handler, "textMessageService", mockTextMessageService);
        setField(handler, "patientService", mockPatientService);
    }

    @Test
    public void shouldSendSMSForEnrollmentWithSMSMedium() throws ContentNotFoundException {
        ServiceType serviceType = ServiceType.CHILD_CARE;
        String patientId = "1234568";
        String mobileNumber = "9845312345";
        String messageKey = "childcare-calendar-week-33-Monday";
        String messageTemplate = "${motechId}-${firstName}-${lastName}";
        Language language = Language.EN;
        MobileMidwifeEnrollment mobileMidwifeEnrollment = MobileMidwifeEnrollment.newEnrollment().setPatientId(patientId)
                .setServiceType(serviceType).setMedium(Medium.SMS).setLanguage(language).setPhoneNumber(mobileNumber);

        MRSPerson person = new MRSPerson().firstName("firstname").lastName("lastname");
        Patient patient = new Patient(new MRSPatient("motechid", person, new MRSFacility("")));

        when(mockMobileMidwifeService.findActiveBy(patientId)).thenReturn(mobileMidwifeEnrollment);
        when(mockPatientService.getPatientByMotechId(patientId)).thenReturn(patient);
        when(mockTextMessageService.getSMSTemplate(language.name(), messageKey)).thenReturn(messageTemplate);

        handler.sendProgramMessage(motechEvent(patientId, serviceType.name(), messageKey));
        verify(mockTextMessageService).sendSMS(mobileNumber, SMS.fromSMSText("motechid-firstname-lastname"));
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
        verify(mockTextMessageService, never()).sendSMS(Matchers.<Facility>any(), Matchers.<SMS>any());
    }

    @Test
    public void shouldSendUnregisterUserIfItIsTheLastEventForTheProgram() throws ContentNotFoundException {
        ServiceType serviceType = ServiceType.CHILD_CARE;
        String patientId = "1234568";
        String messageKey = "childcare-calendar-week-33-Monday";
        Language language = Language.EN;
        MobileMidwifeEnrollment mobileMidwifeEnrollment = MobileMidwifeEnrollment.newEnrollment().setPatientId(patientId)
                .setServiceType(serviceType).setMedium(Medium.SMS).setLanguage(language).setPhoneNumber("9845312345");
        when(mockMobileMidwifeService.findActiveBy(patientId)).thenReturn(mobileMidwifeEnrollment);

        MRSPerson person = new MRSPerson().firstName("firstname").lastName("lastname");
        Patient patient = new Patient(new MRSPatient("motechid", person, new MRSFacility("")));
        when(mockPatientService.getPatientByMotechId(patientId)).thenReturn(patient);
        when(mockTextMessageService.getSMSTemplate(language.name(), messageKey)).thenReturn("${motechId}-${firstName}-${lastName}");

        MotechEvent lastEvent = motechEvent(patientId, serviceType.name(), messageKey).setLastEvent(true);
        handler.sendProgramMessage(lastEvent);
        verify(mockMobileMidwifeService).unregister(patientId);
    }

     private MotechEvent motechEvent(String externalId, String campaignName, String messageKey) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.CAMPAIGN_NAME_KEY, campaignName);
        parameters.put(EventKeys.MESSAGE_KEY, messageKey);
        parameters.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        return new MotechEvent(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, parameters);
    }

}
