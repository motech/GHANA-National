package org.motechproject.ghana.national.handler;

import junit.framework.Assert;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ghana.national.exception.EventHandlerException;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
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

    @Test
    public void shouldThrowOnAnyFailureInHandlingAlerts() {
        doThrow(new RuntimeException("some")).when(mockCareScheduleHandler).handleAncVisitAlert(Matchers.<MotechEvent>any());
        final MotechEvent event = new MotechEvent("subject", new HashMap<String, Object>());
        try {
            ancVisitScheduleHandler.handleAlert(event);
            Assert.fail("expected scheduler handler exception");
        } catch (EventHandlerException she) {
            assertThat(she.getMessage(), CoreMatchers.is(event.toString()));
        }
    }
}
