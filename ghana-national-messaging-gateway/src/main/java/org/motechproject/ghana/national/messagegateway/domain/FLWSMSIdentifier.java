package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;

public class FLWSMSIdentifier implements Serializable {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private String phoneNumber;
    private DateTime deliveryDate;

    public FLWSMSIdentifier(SMSPayload smsPayload) {
        phoneNumber = smsPayload.getUniqueId();
        deliveryDate = smsPayload.getDeliveryTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FLWSMSIdentifier)) return false;

        FLWSMSIdentifier that = (FLWSMSIdentifier) o;

        if (deliveryDate != null ? !deliveryDate.equals(that.deliveryDate) : that.deliveryDate != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = phoneNumber != null ? phoneNumber.hashCode() : 0;
        result = 31 * result + (deliveryDate != null ? deliveryDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return phoneNumber + "|" + deliveryDate.toString(DateTimeFormat.forPattern(DATE_FORMAT));
    }
}

