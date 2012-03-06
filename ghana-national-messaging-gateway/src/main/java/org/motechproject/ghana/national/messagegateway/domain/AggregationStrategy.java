package org.motechproject.ghana.national.messagegateway.domain;

import java.util.List;

public interface AggregationStrategy {
    String aggregate(List<SMS> smsMessages);
}
