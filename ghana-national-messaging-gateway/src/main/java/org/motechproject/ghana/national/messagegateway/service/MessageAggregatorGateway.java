package org.motechproject.ghana.national.messagegateway.service;

import org.motechproject.ghana.national.messagegateway.domain.Payload;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.Header;

public interface MessageAggregatorGateway {

    @Gateway(requestChannel = "receivePayload")
    public void dispatch(Payload payload, @Header("identifier") String identifier);
}
