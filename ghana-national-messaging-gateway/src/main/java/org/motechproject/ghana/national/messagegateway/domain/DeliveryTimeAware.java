package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;

public interface DeliveryTimeAware {
    Boolean canBeDispatched();
    DateTime getDeliveryTime();

}
