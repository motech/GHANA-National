package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


public class IVRGatewayTest {

    IVRGateway gateway;

    @Mock
    VerboiceIVRService mockIVRService;
    String channel = "test_channel";

    @Before
    public void setUp() {
        initMocks(this);
        gateway = new IVRGateway();
        ReflectionTestUtils.setField(gateway, "verboiceIVRService", mockIVRService);
        ReflectionTestUtils.setField(gateway, "channelName", channel);
    }

    @Test
    public void shouldInitiateACall() {
        String phoneNumber = "0987654321";
        Map<String, String> params = new HashMap<String, String>();
        gateway.placeCall(phoneNumber, params);
        ArgumentCaptor<CallRequest> callRequestCaptor = ArgumentCaptor.forClass(CallRequest.class);
        verify(mockIVRService).initiateCall(callRequestCaptor.capture());
        CallRequest callRequest = callRequestCaptor.getValue();
        assertThat(callRequest.getPhone(), is(phoneNumber));
        assertThat(callRequest.getPayload(), is(params));
        assertThat(callRequest.getCallBackUrl(), is(channel)); //refer verboice.properties file
    }
}
