package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ghana.national.domain.AlertWindow;
import org.motechproject.ghana.national.domain.AppointmentVoicePayload;
import org.motechproject.ghana.national.domain.CareVoicePayload;
import org.motechproject.ghana.national.messagegateway.domain.NextMondayDispatcher;
import org.motechproject.ghana.national.messagegateway.service.MessageGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class VoiceGateway {
    @Autowired
    @Qualifier("messageGateway")
    MessageGateway messageGateway;

    public void dispatchCareMsgToAggregator(String clipName, String clipIdentifier, String recipientIdentifier, Period validity, AlertWindow window, DateTime scheduleWindowStart){
        messageGateway.dispatch(new CareVoicePayload(clipName, recipientIdentifier, DateTime.now(), new NextMondayDispatcher(), validity, window, scheduleWindowStart), clipIdentifier);
    }

    public void dispatchAppointmentMsgToAggregator(String clipName, String clipIdentifier, String recipientIdentifier, Period validity) {
        messageGateway.dispatch(new AppointmentVoicePayload(clipName, recipientIdentifier, DateTime.now(), new NextMondayDispatcher(), validity), clipIdentifier);
    }
}
