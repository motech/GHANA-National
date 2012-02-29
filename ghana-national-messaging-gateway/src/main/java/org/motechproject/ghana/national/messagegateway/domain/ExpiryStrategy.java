package org.motechproject.ghana.national.messagegateway.domain;

public interface ExpiryStrategy {
    int expireMessageGroups(MessageStore messageStore, long timeout);
}
