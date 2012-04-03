package org.motechproject.ghana.national.web.service;

import org.motechproject.ghana.national.messagegateway.domain.MessageStore;
import org.motechproject.ghana.national.messagegateway.domain.SMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.store.MessageGroup;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GNAggregatorService {
    @Autowired
    MessageStore messageStore;

    public List<SMS> allMessages(){
        List<SMS> smsList = new ArrayList<SMS>();
        for (MessageGroup group : messageStore) {
            for (Message<?> message : group.getMessages()) {
                smsList.add((SMS) message.getPayload());

            }
        }
        return smsList;
    }
}
