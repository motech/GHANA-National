package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;

public class ScheduleHandlerTest {

    private ScheduleHandler scheduleHandler;

    @Mock
    private CareScheduleHandler careScheduleHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        scheduleHandler = new ScheduleHandler();
        ReflectionTestUtils.setField(scheduleHandler, "careScheduleHandler", careScheduleHandler);
    }

    @Test
    public void shouldHandleDeliveryNotificationSchedules(){
        fireScheduleHandler(ANC_DELIVERY);
        verify(careScheduleHandler).handlePregnancyAlert(Matchers.<MilestoneEvent>any());
    }

    @Test
    public void shouldHandleTTVaccinationSchedules(){
        fireScheduleHandler(TT_VACCINATION_VISIT);
        verify(careScheduleHandler).handleTTVaccinationAlert(Matchers.<MilestoneEvent>any());
    }

    @Test
    public void shouldHandleBCGSchedules(){
        fireScheduleHandler(CWC_BCG);
        verify(careScheduleHandler).handleBCGAlert(Matchers.<MilestoneEvent>any());
    }

    @Test
    public void shouldHandleIPTpVaccinationSchedules(){
        fireScheduleHandler(ANC_IPT_VACCINE);
        verify(careScheduleHandler).handleIPTpVaccinationAlert(Matchers.<MilestoneEvent>any());
    }
    
    @Test
    public void shouldHandleMeaslesVaccinationSchedules(){
        fireScheduleHandler(CWC_MEASLES_VACCINE);
        verify(careScheduleHandler).handleMeaslesVaccinationAlert(Matchers.<MilestoneEvent>any());
    }

    private void fireScheduleHandler(final String scheduleName) {
        scheduleHandler.handleAlert(new MotechEvent(EventSubjects.MILESTONE_ALERT, new HashMap<String, Object>(){{
            put("schedule_name", scheduleName);
        }}));
    }


}
