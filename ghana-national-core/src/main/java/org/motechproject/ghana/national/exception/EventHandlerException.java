package org.motechproject.ghana.national.exception;

import org.motechproject.scheduler.domain.MotechEvent;

public class EventHandlerException extends RuntimeException {
    public EventHandlerException(MotechEvent motechEvent, Exception e) {
        super(motechEvent.toString(), e);
    }
}
