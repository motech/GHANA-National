package org.motechproject.ghana.national.messagegateway.repositories;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.store.MessageGroup;
import org.springframework.integration.store.SimpleMessageGroup;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class EnhancedJdbcMessageStoreTest {
    EnhancedJdbcMessageStore messageStore;
    @Mock
    private DataSource mockDatasource;

    @Before
    public void setUp() {
        initMocks(this);
        messageStore = new EnhancedJdbcMessageStore(mockDatasource);
    }


    @Test
    public void shouldGroupMessagesByGroupId() {
        List<MessageGroup> groupMessages = new ArrayList<>();
        Object groupOne = "abcdHashOne";
        Object groupTwo = "abcdHashTwo";

        groupMessages.add(new SimpleMessageGroup(asList(getMessage("Dummy Measles Message")), groupOne));
        groupMessages.add(new SimpleMessageGroup(asList(getMessage("Dummy YF Message")), groupOne));
        groupMessages.add(new SimpleMessageGroup(asList(getMessage("Dummy OPV Message")), groupOne));
        groupMessages.add(new SimpleMessageGroup(asList(getMessage("Dummy Measles Message")), groupTwo));

        List<MessageGroup> messageGroups = messageStore.groupByMessageGroupId(groupMessages);

        assertThat(messageGroups.size(), is(2));
        for (MessageGroup messageGroup : messageGroups) {
            if (messageGroup.getGroupId().equals(groupOne))
                assertThat(messageGroup.getMessages().size(), is(3));
            else if (messageGroup.getGroupId().equals(groupTwo))
                assertThat(messageGroup.getMessages().size(), is(1));
        }
    }

    private Message<?> getMessage(final String message) {
        return new Message() {

            @Override
            public MessageHeaders getHeaders() {
                return new MessageHeaders(new HashMap<String, Object>());
            }

            @Override
            public Object getPayload() {
                return message;
            }
        };
    }

}
