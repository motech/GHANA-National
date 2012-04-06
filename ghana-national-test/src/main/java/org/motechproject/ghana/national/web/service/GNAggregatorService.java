package org.motechproject.ghana.national.web.service;

import org.motechproject.ghana.national.messagegateway.domain.SMS;
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

    public List<SMS> allMessages(){
        List<SMS> smsList = new ArrayList<SMS>();
        for (MessageGroup group : store) {
            for (Message<?> message : group.getMessages()) {
                smsList.add((SMS) message.getPayload());

            }
        }
        return smsList;
    }
}
