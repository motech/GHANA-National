package org.motechproject.ghana.national.handler;

import org.motechproject.ghana.national.builder.IVRCallbackUrlBuilder;
import org.motechproject.ghana.national.builder.IVRRequestBuilder;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.IVRGateway;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.retry.EventKeys;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.retry.EventKeys.EXTERNAL_ID;

@Component
public class IVRRetryHandler {

    private IVRGateway ivrGateway;
    private IVRCallbackUrlBuilder ivrCallbackUrlBuilder;
    private MobileMidwifeService mobileMidwifeService;

    @Autowired
    public IVRRetryHandler(IVRGateway ivrGateway, IVRCallbackUrlBuilder ivrCallbackUrlBuilder, MobileMidwifeService mobileMidwifeService) {
        this.ivrGateway = ivrGateway;
        this.ivrCallbackUrlBuilder = ivrCallbackUrlBuilder;
        this.mobileMidwifeService = mobileMidwifeService;
    }


    @MotechListener(subjects = {EventKeys.RETRY_SUBJECT})
    public void handlerIVRRetry(MotechEvent event){
        String motechId = (String) event.getParameters().get(EXTERNAL_ID);
        MobileMidwifeEnrollment enrollment = mobileMidwifeService.findLatestEnrollment(motechId);
        ivrGateway.placeCall(enrollment.getPhoneNumber(), IVRRequestBuilder.build(ivrCallbackUrlBuilder.outboundCallUrl(motechId)));
    }
}
