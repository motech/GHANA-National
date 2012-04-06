package org.motechproject.ghana.national.messagegateway.domain;

import org.motechproject.ghana.national.messagegateway.repositories.MessageStore;

public interface ExpiryStrategy {
    int expireMessageGroups(MessageStore messageStore, long timeout);
}
