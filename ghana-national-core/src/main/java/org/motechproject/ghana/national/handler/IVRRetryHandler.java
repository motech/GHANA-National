package org.motechproject.ghana.national.handler;

import org.motechproject.ghana.national.builder.IVRCallbackUrlBuilder;
import org.motechproject.ghana.national.builder.IVRRequestBuilder;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.mobilemidwife.MobileMidwifeEnrollment;
import org.motechproject.ghana.national.repository.IVRGateway;
import org.motechproject.ghana.national.service.MobileMidwifeService;
import org.motechproject.model.Time;
import org.motechproject.retry.EventKeys;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.ghana.national.domain.Constants.MOBILE_MIDWIFE_MAX_TIMEOFDAY_FOR_VOICE;
import static org.motechproject.ghana.national.domain.Constants.MOBILE_MIDWIFE_MIN_TIMEOFDAY_FOR_VOICE;
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
    public void handlerIVRRetry(MotechEvent event) {
        if (withinValidTimeRange(new Time(DateUtil.now().toLocalTime()))) {
            String motechId = (String) event.getParameters().get(EXTERNAL_ID);
            MobileMidwifeEnrollment enrollment = mobileMidwifeService.findLatestEnrollment(motechId);
            ivrGateway.placeCall(enrollment.getPhoneNumber(), IVRRequestBuilder.build(ivrCallbackUrlBuilder.outboundCallUrl(motechId)));
        }
    }

    private boolean withinValidTimeRange(Time timeOfDay) {
        Time maxTime = MOBILE_MIDWIFE_MAX_TIMEOFDAY_FOR_VOICE;
        Time minTime = MOBILE_MIDWIFE_MIN_TIMEOFDAY_FOR_VOICE;
        return (timeOfDay.ge(minTime) && timeOfDay.le(maxTime));
    }
}
