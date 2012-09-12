package org.motechproject.ghana.national.exception;

import org.motechproject.event.MotechEvent;

public class EventHandlerException extends RuntimeException {
    public EventHandlerException(MotechEvent motechEvent, Exception e) {
        super(motechEvent.toString(), e);
    }
}
