package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ghana.national.messagegateway.domain.NextMondayDispatcher;
import org.motechproject.ghana.national.messagegateway.domain.VoicePayload;
import org.motechproject.ghana.national.messagegateway.service.MessageGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class VoiceGateway {
    @Autowired
    @Qualifier("messageGateway")
    MessageGateway messageGateway;

    public void dispatchVoiceToAggregator(String clipName, String clipIdentifier, String recipientIdentifier, Period validity){
        messageGateway.dispatch(new VoicePayload(clipName, recipientIdentifier, DateTime.now(), new NextMondayDispatcher(), validity), clipIdentifier);
    }
}
