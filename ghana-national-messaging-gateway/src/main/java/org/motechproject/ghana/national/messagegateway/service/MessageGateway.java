package org.motechproject.ghana.national.messagegateway.service;

import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.springframework.integration.annotation.Gateway;

public interface MessageGateway {

    @Gateway(requestChannel = "smsMessages")
    public void dispatch(SMS sms);
}
