package org.motechproject.ghana.national.gateway;

import org.apache.log4j.Logger;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.OutboundEventGateway;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class SynchronousEventGateway implements OutboundEventGateway {

    @Autowired
    private EventListenerRegistry eventListenerRegistry;
    private final static Logger log = Logger.getLogger(SynchronousEventGateway.class);

    @Override
    public void sendEventMessage(MotechEvent motechEvent) {
        log.debug("Synchronous Event Received" + motechEvent);
        Set<EventListener> listeners = eventListenerRegistry.getListeners(motechEvent.getSubject());
        if (!listeners.isEmpty()) {
            for (EventListener listener : listeners) {
                listener.handle(motechEvent);
            }
        }
    }
}
