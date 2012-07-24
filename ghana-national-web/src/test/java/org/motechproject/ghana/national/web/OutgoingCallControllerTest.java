package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.repository.IVRGateway;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutgoingCallControllerTest {

    private OutgoingCallController controller = new OutgoingCallController();

    @Mock
    private IVRGateway mockIvrGateway;

    @Before
    public void setUp() {
        initMocks(this);
        ReflectionTestUtils.setField(controller, "ivrGateway", mockIvrGateway);
    }

    @Test
    public void shouldPickMessagesFromUrlAndPlayThem_GivenMotechIdAndLanguage() {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("callerid", "12345");
        controller.call(mockHttpServletRequest);
        verify(mockIvrGateway).placeCall("12345", null);
    }
}
