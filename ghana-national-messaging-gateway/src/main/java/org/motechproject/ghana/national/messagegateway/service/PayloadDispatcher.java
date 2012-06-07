package org.motechproject.ghana.national.messagegateway.service;

import org.motechproject.ghana.national.messagegateway.domain.Payload;
import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("payloadDispatcher")
public class PayloadDispatcher {

    @Autowired
    AggregatedMessageHandler aggregatedMessageHandler;

    public void dispatch(List<Payload> payload) {
        aggregatedMessageHandler.handle(payload);
    }
}
