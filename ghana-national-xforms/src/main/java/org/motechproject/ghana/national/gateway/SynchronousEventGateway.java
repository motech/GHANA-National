package org.motechproject.ghana.national.gateway;

import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.EventListener;
import org.motechproject.server.event.EventListenerRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class SynchronousEventGateway implements OutboundEventGateway {

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    @Override
    public void sendEventMessage(MotechEvent motechEvent) {
        Set<EventListener> listeners = eventListenerRegistry.getListeners(motechEvent.getSubject());
        if (!listeners.isEmpty()) {
            for (EventListener listener : listeners) {
                listener.handle(motechEvent);
            }
        }
    }
}
