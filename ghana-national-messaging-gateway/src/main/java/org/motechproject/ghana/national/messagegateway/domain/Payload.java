package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public interface Payload extends DeliveryTimeAware, Serializable {
    DateTime getGenerationTime();
    String getUniqueId();
}
