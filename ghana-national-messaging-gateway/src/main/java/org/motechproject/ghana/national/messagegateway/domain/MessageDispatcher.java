package org.motechproject.ghana.national.messagegateway.domain;

import org.motechproject.ghana.national.messagegateway.service.MessageGateway;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ReleaseStrategy;

import java.util.List;

@MessageEndpoint
public class MessageDispatcher {

    public static final String SMS_SEPARATOR = "%0a";

    @Autowired
    private MessageGateway messageGateway;

    @Autowired
    AggregationStrategy aggregationStrategy;

    public SMS aggregateSMS(List<SMS> smsMessages) {
        final SMS firstSMSInTheGroup = smsMessages.get(0);
        return SMS.fromText(aggregationStrategy.aggregate(smsMessages), firstSMSInTheGroup.getPhoneNumber(), DateUtil.now(),
                firstSMSInTheGroup.getDeliveryStrategy(), firstSMSInTheGroup.getComparator());
    }

    @CorrelationStrategy
    public String correlateByRecipientAndDeliveryDate(SMS sms) {
        return new FLWSMSIdentifier(sms).toString();
    }

    @ReleaseStrategy
    public boolean canBeDispatched(List<SMS> smsMessages) {
        return smsMessages.get(0).canBeDispatched();
    }
}
