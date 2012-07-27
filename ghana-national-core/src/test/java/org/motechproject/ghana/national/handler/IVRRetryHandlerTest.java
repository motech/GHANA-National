package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ghana.national.builder.IVRCallbackUrlBuilder;
import org.motechproject.ghana.national.builder.IVRRequestBuilder;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.IVRGateway;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.model.Time;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.retry.EventKeys.EXTERNAL_ID;

public class IVRRetryHandlerTest extends BaseUnitTest{
    @Mock
    private IVRGateway ivrGateway;
    @Mock
    private IVRCallbackUrlBuilder ivrCallbackUrlBuilder;
    @Mock
    private MobileMidwifeService mobileMidwifeService;
    private IVRRetryHandler ivrRetryHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ivrRetryHandler = new IVRRetryHandler(ivrGateway, ivrCallbackUrlBuilder, mobileMidwifeService);
    }

    @Test
    public void shouldPlaceAnOutboundIvrCallWhileRetrying(){
        super.mockCurrentDate(DateUtil.newDateTime(DateUtil.today(), new Time(6, 0)));
        final String motechId = "motechId";
        MotechEvent event = new MotechEvent("some_subject", new HashMap<String, Object>(){{
            put(EXTERNAL_ID, motechId);
        }});

        String phoneNumber = "phonenumber";
        when(mobileMidwifeService.findLatestEnrollment(motechId)).thenReturn(new MobileMidwifeEnrollment(null).setPhoneNumber(phoneNumber));
        when(ivrCallbackUrlBuilder.outboundCallUrl(motechId)).thenReturn("url");
        ivrRetryHandler.handlerIVRRetry(event);

        verify(ivrGateway).placeCall(phoneNumber, new HashMap<String, String>(){{
            put(IVRRequestBuilder.CALLBACK_URL, "url");
        }});
    }

    @Test
    public void shouldNotPlaceOutboundIVRRetryCallInBlackoutPeriod(){
        super.mockCurrentDate(DateUtil.newDateTime(DateUtil.today(), new Time(4, 0)));

        MotechEvent event = new MotechEvent("");
        ivrRetryHandler.handlerIVRRetry(event);
        verify(ivrGateway, never()).placeCall(anyString(), anyMap());


    }
}
