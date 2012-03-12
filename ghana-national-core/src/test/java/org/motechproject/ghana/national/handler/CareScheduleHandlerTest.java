package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.*;

public class CareScheduleHandlerTest {

    private CareScheduleHandler careScheduleHandlerSpy;
    @Mock
    private SMSGateway mockSmsGateway;
    @Mock
    private AllPatients mockAllPatients;
    @Mock
    private AllFacilities mockAllFacilities;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        careScheduleHandlerSpy = spy(new CareScheduleHandler(mockAllPatients, mockAllFacilities, mockSmsGateway));
        doNothing().when(careScheduleHandlerSpy).sendAggregativeSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());
        doNothing().when(careScheduleHandlerSpy).sendInstantSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());
    }

    @Test
    public void shouldHandlePregnancyAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePregnancyAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregativeSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleTTVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleTTVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregativeSMSToFacility(TT_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleBCGAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleBCGAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregativeSMSToFacility(BCG_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleAncVisitAlert() {
        MotechEvent motechEvent = new MotechEvent("subject", new HashMap<String, Object>());
        doNothing().when(careScheduleHandlerSpy).sendAggregativeSMSToFacilityForAnAppointment(ANC_VISIT_SMS_KEY, motechEvent);

        careScheduleHandlerSpy.handleAncVisitAlert(motechEvent);

        verify(careScheduleHandlerSpy).sendAggregativeSMSToFacilityForAnAppointment(ANC_VISIT_SMS_KEY, motechEvent);
    }

    @Test
    public void shouldHandleIPTpVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleIPTpVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregativeSMSToFacility(ANC_IPTp_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleIPTiVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleIPTiVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregativeSMSToFacility(CWC_IPTi_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleMeaslesVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleMeaslesVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregativeSMSToFacility(CWC_MEASLES_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleYellowFeverVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleYellowFeverVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregativeSMSToFacility(CWC_YF_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandlePentaVaccinationAlert() {
        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePentaVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendAggregativeSMSToFacility(CWC_PENTA_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandlePNCMotherAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePNCMotherAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendInstantSMSToFacility(PNC_MOTHER_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandlePNCChildAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePNCChildAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendInstantSMSToFacility(PNC_CHILD_SMS_KEY, milestoneEvent);
    }
}
