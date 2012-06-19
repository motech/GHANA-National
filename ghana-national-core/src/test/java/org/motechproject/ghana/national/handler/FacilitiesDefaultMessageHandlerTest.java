package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FacilitiesDefaultMessageHandlerTest {

    @Mock
    DefaultMessageFeeder defaultMessageHandler;

    @InjectMocks
    FacilitiesDefaultMessageHandler handler = new FacilitiesDefaultMessageHandler();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldListenToFacilityDefaultMessageSubject() throws NoSuchMethodException {
        Method handleAlertMethod = handler.getClass().getMethod("handleAlert", MotechEvent.class);
        Annotation[] annotations = handleAlertMethod.getDeclaredAnnotations();
        assertEquals(1, annotations.length);
        assertTrue(annotations[0] instanceof MotechListener);
        assertThat(((MotechListener) annotations[0]).subjects(), is(new String[]{Constants.FACILITIES_DEFAULT_MESSAGE_SUBJECT}));
    }

    @Test
    public void shouldHandleDefaultMessageDispatch() {
        handler.handleAlert(null);
        verify(defaultMessageHandler).handleDefaultMessagesForFacility();
    }
}
