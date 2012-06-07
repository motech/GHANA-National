package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

public class PayloadIdentifier implements Serializable {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private String uniqueId;
    private DateTime deliveryDate;

    public PayloadIdentifier(Payload payload) {
        uniqueId = payload.getUniqueId();
        deliveryDate = payload.getDeliveryTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayloadIdentifier)) return false;

        PayloadIdentifier that = (PayloadIdentifier) o;

        if (deliveryDate != null ? !deliveryDate.equals(that.deliveryDate) : that.deliveryDate != null) return false;
        if (uniqueId != null ? !uniqueId.equals(that.uniqueId) : that.uniqueId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uniqueId != null ? uniqueId.hashCode() : 0;
        result = 31 * result + (deliveryDate != null ? deliveryDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return uniqueId + "|" + deliveryDate.toString(DateTimeFormat.forPattern(DATE_FORMAT));
    }
}

