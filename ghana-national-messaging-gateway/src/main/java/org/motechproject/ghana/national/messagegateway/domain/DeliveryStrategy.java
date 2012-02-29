package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.LocalDateTime;

import java.io.Serializable;

public interface DeliveryStrategy extends Serializable{
    LocalDateTime deliveryDate(SMS sms);
}
