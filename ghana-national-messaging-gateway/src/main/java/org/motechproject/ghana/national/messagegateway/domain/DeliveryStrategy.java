package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public interface DeliveryStrategy extends Serializable {
    DateTime deliveryDate(SMS sms);
}
