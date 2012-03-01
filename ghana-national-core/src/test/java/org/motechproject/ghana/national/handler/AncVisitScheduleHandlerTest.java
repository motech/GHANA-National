package org.motechproject.ghana.national.handler;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventDataKeys;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.util.DateUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AncVisitScheduleHandlerTest {

    AncVisitScheduleHandler ancVisitScheduleHandler;
    @Mock
    CareScheduleHandler mockCareScheduleHandler;

    @Before
    public void setUp() {
        initMocks(this);
        ancVisitScheduleHandler = new AncVisitScheduleHandler();
        ReflectionTestUtils.setField(ancVisitScheduleHandler, "careScheduleHandler", mockCareScheduleHandler);
    }

    @Test
    public void shouldSendTextMessageToFacilityRegardingTheForthcomingANCVisit() {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventDataKeys.SCHEDULE_NAME, "scheduleName");
        parameters.put(EventDataKeys.MILESTONE_NAME, MilestoneAlert.fromMilestone(new Milestone("milestone", null, null, null, null), DateTime.now()));
        parameters.put(EventDataKeys.WINDOW_NAME, "windowName");
        parameters.put(EventDataKeys.EXTERNAL_ID, "externalId");
        parameters.put(EventDataKeys.REFERENCE_DATE, DateUtil.now());

        MotechEvent motechEvent = new MotechEvent("", parameters);
        ancVisitScheduleHandler.handleAlert(motechEvent);
        ArgumentCaptor<MilestoneEvent> argumentCaptor = ArgumentCaptor.forClass(MilestoneEvent.class);
        verify(mockCareScheduleHandler).handleAncVisitAlert(argumentCaptor.capture());

        MilestoneEvent actualMilestoneEvent = argumentCaptor.getValue();
        assertThat(actualMilestoneEvent.getScheduleName(), is(equalTo(parameters.get(EventDataKeys.SCHEDULE_NAME))));
        assertThat(actualMilestoneEvent.getMilestoneAlert(), is(equalTo(parameters.get(EventDataKeys.MILESTONE_NAME))));
        assertThat(actualMilestoneEvent.getWindowName(), is(equalTo(parameters.get(EventDataKeys.WINDOW_NAME))));
        assertThat(actualMilestoneEvent.getExternalId(), is(equalTo(parameters.get(EventDataKeys.EXTERNAL_ID))));
        assertThat(actualMilestoneEvent.getReferenceDateTime(), is(equalTo(parameters.get(EventDataKeys.REFERENCE_DATE))));
    }

    @Test
    public void shouldBeAnnotatedWithMotechListner() throws NoSuchMethodException {
        Annotation[] declaredAnnotations = AncVisitScheduleHandler.class.getMethod("handleAlert", MotechEvent.class).getAnnotations();
        assertThat(declaredAnnotations.length, is(1));
        assertThat(declaredAnnotations[0], is(MotechListener.class));
        assertThat(((MotechListener) declaredAnnotations[0]).subjects(), is(new String[]{EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT}));
    }
}
