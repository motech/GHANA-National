package org.motechproject.ghana.national.messagegateway.domain;

import java.util.List;

public interface AggregationStrategy {
    List<Payload> aggregate(List<Payload> payloads);
}
