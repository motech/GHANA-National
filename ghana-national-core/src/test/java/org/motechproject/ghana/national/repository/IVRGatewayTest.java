package org.motechproject.ghana.national.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.server.verboice.VerboiceIVRService;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


public class IVRGatewayTest {

    @InjectMocks
    IVRGateway gateway=new IVRGateway();

    @Mock
    VerboiceIVRService mockIVRService;

    @Before
    public void setUp(){
        initMocks(this);

    }

    @Test
    public void shouldInitiateACall() {
        String language="en";
        String phoneNumber = "0987654321";
        gateway.placeCall(phoneNumber,language);
        ArgumentCaptor<CallRequest> callRequestCaptor=ArgumentCaptor.forClass(CallRequest.class);
        verify(mockIVRService).initiateCall(callRequestCaptor.capture());
        CallRequest callRequest = callRequestCaptor.getValue();
        assertThat(callRequest.getPhone(),is(phoneNumber));
        assertThat(callRequest.getTimeOut(),is(0));
        assertThat(callRequest.getCallBackUrl(),is("demo_channel"));

    }
}
