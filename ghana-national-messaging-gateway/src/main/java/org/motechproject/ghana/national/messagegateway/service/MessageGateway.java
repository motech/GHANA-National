package org.motechproject.ghana.national.messagegateway.service;


import org.motechproject.ghana.national.messagegateway.domain.MessageStore;
import org.motechproject.ghana.national.messagegateway.domain.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class MessageGateway {

    @Autowired
    private MessageStore messageStore;

    @Autowired
    private MessageAggregatorGateway messageAggregatorGateway;

    public void dispatch(Payload payload, @Header("identifier") String identifier){
        messageAggregatorGateway.dispatch(payload, identifier);
    }

    public void delete(String identifier){
        messageStore.removeMessages(identifier);
    }
}
