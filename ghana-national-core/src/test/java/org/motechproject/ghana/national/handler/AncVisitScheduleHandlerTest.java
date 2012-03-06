package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
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
        MotechEvent motechEvent = new MotechEvent("", new HashMap<String, Object>());
        ancVisitScheduleHandler.handleAlert(motechEvent);
        verify(mockCareScheduleHandler).handleAncVisitAlert(motechEvent);
    }

    @Test
    public void shouldBeAnnotatedWithMotechListner() throws NoSuchMethodException {
        Annotation[] declaredAnnotations = AncVisitScheduleHandler.class.getMethod("handleAlert", MotechEvent.class).getAnnotations();
        assertThat(declaredAnnotations.length, is(1));
        assertThat(declaredAnnotations[0], is(MotechListener.class));
        assertThat(((MotechListener) declaredAnnotations[0]).subjects(), is(new String[]{EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT}));
    }
}
