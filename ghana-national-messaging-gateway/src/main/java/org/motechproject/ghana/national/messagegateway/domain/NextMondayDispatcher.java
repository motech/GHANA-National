package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

public class NextMondayDispatcher implements DeliveryStrategy{
    @Override
    public DateTime deliveryDate(SMS sms) {
        return DateUtil.newDateTime(sms.getGenerationTime().plusWeeks(1).withDayOfWeek(DateTimeConstants.MONDAY).toLocalDate(), new Time(0, 0));
    }
}
