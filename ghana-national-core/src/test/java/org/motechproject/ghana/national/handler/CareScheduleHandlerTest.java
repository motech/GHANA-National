package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.*;

public class CareScheduleHandlerTest {

    private CareScheduleHandler careScheduleHandlerSpy;

    @Before
    public void setUp() throws Exception {
        careScheduleHandlerSpy = spy(new CareScheduleHandler());
        doNothing().when(careScheduleHandlerSpy).sendSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());
    }

    @Test
    public void shouldHandlePregnancyAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePregnancyAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleTTVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleTTVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(TT_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleBCGAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleBCGAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(BCG_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleAncVisitAlert() {
        MotechEvent motechEvent = new MotechEvent("subject", new HashMap<String, Object>());
        doNothing().when(careScheduleHandlerSpy).sendSMSToFacilityForAnAppointment(ANC_VISIT_SMS_KEY, motechEvent);

        careScheduleHandlerSpy.handleAncVisitAlert(motechEvent);

        verify(careScheduleHandlerSpy).sendSMSToFacilityForAnAppointment(ANC_VISIT_SMS_KEY, motechEvent);
    }

    @Test
    public void handleIPTpVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleIPTpVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(ANC_IPTp_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @Test
    public void handleIPTiVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleIPTiVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(CWC_IPTi_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @Test
    public void handleMeaslesVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleMeaslesVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(CWC_MEASLES_SMS_KEY, milestoneEvent);
    }

    @Test
    public void handleYellowFeverVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleYellowFeverVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(CWC_YF_SMS_KEY, milestoneEvent);
    }

    @Test
    public void handlePentaVaccinationAlert() {

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handlePentaVaccinationAlert(milestoneEvent);
        verify(careScheduleHandlerSpy).sendSMSToFacility(CWC_PENTA_SMS_KEY, milestoneEvent);
    }
}
