package org.motechproject.ghana.national.handler;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles the message triggered by weekly cron job for sending 'DEFAULT' SMSPayload to all facilities to aggregate
 */
@Component
public class FacilitiesDefaultMessageHandler {

    @Autowired
    private DefaultMessageFeeder messageHandler;


    @MotechListener(subjects = {Constants.FACILITIES_DEFAULT_MESSAGE_SUBJECT})
    public void handleAlert(MotechEvent motechEvent) {
        messageHandler.handleDefaultMessagesForFacility();
    }
}
