package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.AlertType;
import org.motechproject.ghana.national.repository.*;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.*;

public class CareScheduleHandlerTest {

    private CareScheduleAlerts careScheduleHandlerSpy;
    @Mock
    private SMSGateway mockSmsGateway;
    @Mock
    private VoiceGateway mockVoiceGateway;
    @Mock
    private PatientService mockPatientService;
    @Mock
    private FacilityService mockFacilityService;
    @Mock
    private AllObservations mockAllObservations;
    @Mock
    private AllMobileMidwifeEnrollments mockAllMobileMidwifeEnrollments;
    @Mock
    private AllPatientsOutbox mockAllPatientsOutbox;
    @Mock
    private ScheduleJsonReader mockScheduleJsoneReader;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        careScheduleHandlerSpy = spy(new CareScheduleAlerts(mockPatientService, mockFacilityService, mockSmsGateway,
                mockVoiceGateway, mockAllObservations, mockAllMobileMidwifeEnrollments, mockAllPatientsOutbox, mockScheduleJsoneReader));
        doNothing().when(careScheduleHandlerSpy).sendAggregatedSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());
        doNothing().when(careScheduleHandlerSpy).sendAggregatedMessageToPatient(Matchers.<String>any(), Matchers.<MilestoneEvent>any());
        doNothing().when(careScheduleHandlerSpy).sendAggregatedSMSToPatientForAppointment(Matchers.<String>any(), Matchers.<MotechEvent>any());
        doNothing().when(careScheduleHandlerSpy).sendInstantMessageToPatient(Matchers.<String>any(), Matchers.<MilestoneEvent>any(), eq(AlertType.CARE));
        doNothing().when(careScheduleHandlerSpy).sendInstantSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());
    }

    @Test
    public void shouldHandlePregnancyAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePregnancyAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleTTVaccinationAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleTTVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedSMSToFacility(TT_VACCINATION_SMS_KEY, milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedMessageToPatient(PATIENT_TT, milestoneEvent);
    }

    @Test
    public void shouldHandleBCGAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleBCGAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedSMSToFacility(BCG_SMS_KEY, milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedMessageToPatient(PATIENT_BCG, milestoneEvent);
    }

    @Test
    public void shouldHandleOPVAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleOpvVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedSMSToFacility(CWC_OPV_SMS_KEY, milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedMessageToPatient(PATIENT_OPV, milestoneEvent);
    }

    @Test
    public void shouldHandleAncVisitAlert() {
        MotechEvent motechEvent = new MotechEvent("subject", new HashMap<String, Object>());
        doNothing().when(careScheduleHandlerSpy).sendAggregatedSMSToFacilityForAnAppointment(ANC_VISIT_SMS_KEY, motechEvent);

        careScheduleHandlerSpy.handleAncVisitAlert(motechEvent);

        verify(careScheduleHandlerSpy).sendAggregatedSMSToFacilityForAnAppointment(ANC_VISIT_SMS_KEY, motechEvent);
        verify(careScheduleHandlerSpy).sendAggregatedSMSToPatientForAppointment(PATIENT_ANC_VISIT, motechEvent);
    }

    @Test
    public void shouldHandleIPTpVaccinationAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleIPTpVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedSMSToFacility(ANC_IPTp_VACCINATION_SMS_KEY, milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedMessageToPatient(PATIENT_IPT, milestoneEvent);
    }

    @Test
    public void shouldHandleIPTiVaccinationAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleIPTiVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedSMSToFacility(CWC_IPTi_VACCINATION_SMS_KEY, milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedMessageToPatient(PATIENT_IPTI, milestoneEvent);
    }

    @Test
    public void shouldHandleMeaslesVaccinationAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleMeaslesVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedSMSToFacility(CWC_MEASLES_SMS_KEY, milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedMessageToPatient(PATIENT_MEASLES, milestoneEvent);
    }

    @Test
    public void shouldHandleYellowFeverVaccinationAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleYellowFeverVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedSMSToFacility(CWC_YF_SMS_KEY, milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedMessageToPatient(PATIENT_YELLOW_FEVER, milestoneEvent);
    }

    @Test
    public void shouldHandlePentaVaccinationAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePentaVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedSMSToFacility(CWC_PENTA_SMS_KEY, milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregatedMessageToPatient(PATIENT_PENTA, milestoneEvent);
    }

    @Test
    public void shouldHandlePNCMotherAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePNCMotherAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendInstantSMSToFacility(PNC_MOTHER_SMS_KEY, milestoneEvent);
        verify(careScheduleHandlerSpy).sendInstantMessageToPatient(PATIENT_PNC_MOTHER, milestoneEvent, AlertType.CARE);
    }

    @Test
    public void shouldHandlePNCChildAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePNCChildAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendInstantSMSToFacility(PNC_CHILD_SMS_KEY, milestoneEvent);
        verify(careScheduleHandlerSpy).sendInstantMessageToPatient(PATIENT_PNC_BABY, milestoneEvent, AlertType.CARE);
    }
}
