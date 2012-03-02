package org.motechproject.ghana.national.handler;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import static org.joda.time.Period.weeks;
import static org.mockito.Mockito.*;
import static org.motechproject.ghana.national.domain.SmsTemplateKeys.*;

public class CareScheduleHandlerTest {

    private CareScheduleHandler careScheduleHandlerSpy;

    @Before
    public void setUp() throws Exception {
        careScheduleHandlerSpy = spy(new CareScheduleHandler());
    }

    @Test
    public void shouldHandlePregnancyAlert() {
        doNothing().when(careScheduleHandlerSpy).sendSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());

        DateTime conceptionDate = DateUtil.newDateTime(2000, 11, 11, 0, 0, 0);
        final MilestoneEvent milestoneEvent = new MilestoneEvent(null, null, null, null, conceptionDate);

        careScheduleHandlerSpy.handlePregnancyAlert(milestoneEvent);

        verify(careScheduleHandlerSpy).sendSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleTTVaccinationAlert() {
        doNothing().when(careScheduleHandlerSpy).sendSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());

        DateTime referenceDate = DateUtil.newDateTime(2012, 2, 1, 0, 0, 0);
        Milestone milestone = new Milestone("M1", weeks(0), weeks(3), weeks(6), weeks(7));
        final MilestoneEvent milestoneEvent = new MilestoneEvent(null, null, MilestoneAlert.fromMilestone(milestone, referenceDate), null, referenceDate);

        careScheduleHandlerSpy.handleTTVaccinationAlert(milestoneEvent);

        verify(careScheduleHandlerSpy).sendSMSToFacility(TT_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleBCGAlert() {
        doNothing().when(careScheduleHandlerSpy).sendSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());

        DateTime referenceDate = DateUtil.newDateTime(2012, 2, 1, 0, 0, 0);
        Milestone milestone = new Milestone("M1", weeks(0), weeks(3), weeks(6), weeks(7));
        final MilestoneEvent milestoneEvent = new MilestoneEvent(null, null, MilestoneAlert.fromMilestone(milestone, referenceDate), null, referenceDate);

        careScheduleHandlerSpy.handleBCGAlert(milestoneEvent);

        verify(careScheduleHandlerSpy).sendSMSToFacility(BCG_SMS_KEY, milestoneEvent);
    }

    @Test
    public void shouldHandleAncVisitAlert() {
        doNothing().when(careScheduleHandlerSpy).sendSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());

        DateTime referenceDate = DateUtil.newDateTime(2012, 2, 1, 0, 0, 0);
        Milestone milestone = new Milestone("M1", weeks(0), weeks(0), weeks(1), weeks(3));
        final MilestoneEvent milestoneEvent = new MilestoneEvent(null, null, MilestoneAlert.fromMilestone(milestone, referenceDate), null, referenceDate);

        careScheduleHandlerSpy.handleAncVisitAlert(milestoneEvent);

        verify(careScheduleHandlerSpy).sendSMSToFacility(ANC_VISIT_SMS_KEY, milestoneEvent);
    }

    @Test
    public void handleIPTpVaccinationAlert() {
        doNothing().when(careScheduleHandlerSpy).sendSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());

        DateTime conceptionDate = DateUtil.newDateTime(2011, 12, 17, 0, 0, 0);
        Milestone milestone = new Milestone("M1", weeks(12), weeks(2), weeks(3), weeks(0));
        final MilestoneEvent milestoneEvent = new MilestoneEvent(null, null, MilestoneAlert.fromMilestone(milestone, conceptionDate), null, conceptionDate);

        careScheduleHandlerSpy.handleIPTpVaccinationAlert(milestoneEvent);

        verify(careScheduleHandlerSpy).sendSMSToFacility(IPTp_VACCINATION_SMS_KEY, milestoneEvent);
    }

    @Test
    public void handleMeaslesVaccinationAlert() {
        doNothing().when(careScheduleHandlerSpy).sendSMSToFacility(Matchers.<String>any(), Matchers.<MilestoneEvent>any());

        final MilestoneEvent milestoneEvent = mock(MilestoneEvent.class);
        careScheduleHandlerSpy.handleMeaslesVaccinationAlert(milestoneEvent);

        verify(careScheduleHandlerSpy).sendSMSToFacility(CWC_MEASLES_SMS_KEY, milestoneEvent);
    }
}
