package org.motechproject.ghana.national.web.service;

import org.motechproject.ghana.national.messagegateway.domain.SMSPayload;
import org.motechproject.ghana.national.messagegateway.domain.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.store.MessageGroup;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GNAggregatorService {
    @Autowired
    Store store;

    public List<SMSPayload> allMessages(){
        List<SMSPayload> smsPayloadList = new ArrayList<SMSPayload>();
        for (MessageGroup group : store) {
            for (Message<?> message : group.getMessages()) {
                smsPayloadList.add((SMSPayload) message.getPayload());

            }
        }
        return smsPayloadList;
    }
}
