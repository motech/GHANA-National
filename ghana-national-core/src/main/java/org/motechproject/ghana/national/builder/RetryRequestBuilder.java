package org.motechproject.ghana.national.builder;

import org.joda.time.DateTime;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.retry.domain.RetryRequest;

public class RetryRequestBuilder {

    public static RetryRequest ivrRetryReqest(String patientId, DateTime now) {
        return new RetryRequest(Constants.RETRY_FOR_2_HOURS_EVERY_30MIN, patientId, now);
    }
}
