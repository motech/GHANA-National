package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;

public class TestPayload implements Payload{

    private DateTime generationTime;
    private DateTime deliveryTime;
    private String uniqueId;
    private Boolean canBeDispatched;

    public TestPayload(DateTime generationTime, DateTime deliveryTime, String uniqueId) {
        this.generationTime = generationTime;
        this.deliveryTime = deliveryTime;
        this.uniqueId = uniqueId;
    }

    @Override
    public DateTime getGenerationTime() {
        return generationTime;
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public Boolean canBeDispatched() {
        return getDeliveryTime().toDate().before(DateUtil.now().toDate());
    }

    @Override
    public DateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(DateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
