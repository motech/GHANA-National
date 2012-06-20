package org.motechproject.ghana.national.builder;

import org.joda.time.DateTime;
import org.motechproject.retry.domain.RetryRequest;

public class RetryRequestBuilder {

    public static RetryRequest ivrRetryReqest(String patientId, DateTime now) {
        return new RetryRequest("retry-ivr-every-2hrs-and-30mins", patientId, now);
    }
}
