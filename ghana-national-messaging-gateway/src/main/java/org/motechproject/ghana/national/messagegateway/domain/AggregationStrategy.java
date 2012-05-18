package org.motechproject.ghana.national.messagegateway.domain;

import java.util.List;

public interface AggregationStrategy {
    List<SMS> aggregate(List<SMSPayload> smsPayloadMessages);
}
