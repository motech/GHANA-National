package org.motechproject.ghana.national.messagegateway.service;

import org.motechproject.ghana.national.messagegateway.domain.Payload;

import java.util.List;

public interface AggregatedMessageHandler {
    void handle(List<Payload> payload);
}
