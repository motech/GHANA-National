package org.motechproject.ghana.national.messagegateway.domain;

import org.springframework.integration.store.MessageGroup;
import org.springframework.integration.store.MessageStore;

public interface Store extends Iterable<MessageGroup>, MessageStore {
    public int removeMessages(String identifier);
}
