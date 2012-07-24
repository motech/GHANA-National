package org.motechproject.ghana.national.messagegateway.repositories;

import org.motechproject.ghana.national.messagegateway.domain.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.jdbc.JdbcMessageStore;
import org.springframework.integration.store.MessageGroup;
import org.springframework.integration.store.SimpleMessageGroup;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.util.UUIDConverter;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class EnhancedJdbcMessageStore extends JdbcMessageStore implements Store {
    private Logger logger = LoggerFactory.getLogger(MessageStore.class);
    private volatile JdbcOperations jdbcTemplate;
    private volatile SerializingConverter serializer = new SerializingConverter();
    private volatile LobHandler lobHandler = new DefaultLobHandler();
    private String region = "DEFAULT";

    private static final String CREATE_MESSAGE = "INSERT into %PREFIX%MESSAGE(MESSAGE_ID, IDENTIFIER, REGION, CREATED_DATE, MESSAGE_BYTES)"
            + " values (?, ?, ?, ?, ?   )";

    private static final String DELETE_MESSAGE = "delete INT_MESSAGE, INT_MESSAGE_GROUP from INT_MESSAGE INNER JOIN INT_MESSAGE_GROUP where " +
            "INT_MESSAGE.MESSAGE_ID = INT_MESSAGE_GROUP.MESSAGE_ID and INT_MESSAGE.REGION = INT_MESSAGE_GROUP.REGION and INT_MESSAGE.IDENTIFIER = ? and INT_MESSAGE.REGION = ?";

    private static final String LIST_MESSAGES_AND_GROUP_INFO =
            "SELECT MSG.MESSAGE_BYTES AS INT_MESSAGE_BYTES, GRP.MESSAGE_ID AS MESSAGE_ID, GRP.CREATED_DATE AS CREATED_DATE, GRP.UPDATED_DATE AS UPDATED_DATE, GRP.GROUP_KEY AS GROUP_KEY, GRP.MESSAGE_BYTES AS GROUP_MESSAGE_BYTES, GRP.MARKED AS MARKED, GRP.COMPLETE AS COMPLETE, GRP.LAST_RELEASED_SEQUENCE AS LAST_RELEASED_SEQUENCE" +
                    " FROM %PREFIX%MESSAGE_GROUP GRP, %PREFIX%MESSAGE MSG" +
                    " WHERE MSG.MESSAGE_ID = GRP.MESSAGE_ID and MSG.REGION = GRP.REGION and GRP.REGION = ? order by GRP.UPDATED_DATE";

    private static final String LIST_MESSAGES_AND_GROUP_INFO_WITH_KEY =
            "SELECT MSG.MESSAGE_BYTES AS INT_MESSAGE_BYTES, GRP.MESSAGE_ID AS MESSAGE_ID, GRP.CREATED_DATE AS CREATED_DATE, GRP.UPDATED_DATE AS UPDATED_DATE, GRP.GROUP_KEY AS GROUP_KEY, GRP.MESSAGE_BYTES AS GROUP_MESSAGE_BYTES, GRP.MARKED AS MARKED, GRP.COMPLETE AS COMPLETE, GRP.LAST_RELEASED_SEQUENCE AS LAST_RELEASED_SEQUENCE" +
                    " FROM %PREFIX%MESSAGE_GROUP GRP, %PREFIX%MESSAGE MSG" +
                    " WHERE MSG.MESSAGE_ID = GRP.MESSAGE_ID and MSG.REGION = GRP.REGION and GRP.GROUP_KEY = ? and GRP.REGION = ? order by GRP.UPDATED_DATE";

    private volatile DeserializingConverter deserializer;

    public EnhancedJdbcMessageStore(DataSource dataSource) {
        super(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
        deserializer = new DeserializingConverter();
        serializer = new SerializingConverter();
        setJdbcTemplate(jdbcTemplate);
        setRegion(region);
    }

    private String getKey(Object input) {
        return input == null ? null : UUIDConverter.getUUID(input).toString();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> Message<T> addMessage(final Message<T> message) {
        if (message.getHeaders().containsKey(SAVED_KEY)) {
            Message<T> saved = (Message<T>) getMessage(message.getHeaders().getId());
            if (saved != null) {
                if (saved.equals(message)) {
                    return message;
                } // We need to save it under its own id
            }
        }

        final long createdDate = System.currentTimeMillis();
        Message<T> result = MessageBuilder.fromMessage(message).setHeader(SAVED_KEY, Boolean.TRUE)
                .setHeader(CREATED_DATE_KEY, new Long(createdDate)).build();

        Map innerMap = (Map) new DirectFieldAccessor(result.getHeaders()).getPropertyValue("headers");
        // using reflection to set ID since it is immutable through MessageHeaders
        innerMap.put(MessageHeaders.ID, message.getHeaders().get(MessageHeaders.ID));

        final String messageId = getKey(result.getHeaders().getId());
        final byte[] messageBytes = serializer.convert(result);
        final String identifier = (String) message.getHeaders().get("identifier");

        int count = jdbcTemplate.update(getQuery(CREATE_MESSAGE), new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                logger.debug("Inserting message with id key=" + messageId);
                ps.setString(1, messageId);
                ps.setString(2, identifier);
                ps.setString(3, region);
                ps.setTimestamp(4, new Timestamp(createdDate));
                lobHandler.getLobCreator().setBlobAsBytes(ps, 5, messageBytes);
            }
        });
        return result;
    }

    public Iterator<MessageGroup> iterator() {
        final Iterator<MessageGroup> iterator = jdbcTemplate.query(getQuery(LIST_MESSAGES_AND_GROUP_INFO),
                new Object[]{region}, new RowMapper<MessageGroup>() {
            @Override
            public MessageGroup mapRow(ResultSet resultSet, int i) throws SQLException {
                List<Message<?>> messages = new ArrayList<Message<?>>();
                messages.add((Message<?>) deserializer.convert(lobHandler.getBlobAsBytes(resultSet, "INT_MESSAGE_BYTES")));
                Boolean completeFlag = resultSet.getInt("COMPLETE") > 0;
                long timestamp = resultSet.getTimestamp("CREATED_DATE").getTime();
                String groupId = resultSet.getString("GROUP_KEY");

                SimpleMessageGroup messageGroup = new SimpleMessageGroup(messages, groupId, timestamp, completeFlag);
                messageGroup.setLastReleasedMessageSequenceNumber(resultSet.getInt("LAST_RELEASED_SEQUENCE"));
                return messageGroup;
            }
        }).iterator();

        return new Iterator<MessageGroup>() {
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public MessageGroup next() {
                return iterator.next();
            }

            public void remove() {
                throw new UnsupportedOperationException("Cannot remove MessageGroup from this iterator.");
            }
        };
    }

    public MessageGroup getMessageGroup(Object groupId) {
        String key = getKey(groupId);
        final List<Message<?>> messages = new ArrayList<Message<?>>();
        final AtomicReference<java.util.Date> date = new AtomicReference<java.util.Date>();
        final AtomicReference<java.util.Date> updateDate = new AtomicReference<java.util.Date>();
        final AtomicReference<Boolean> completeFlag = new AtomicReference<Boolean>();
        final AtomicReference<Integer> lastReleasedSequenceRef = new AtomicReference<Integer>();

        final AtomicInteger size = new AtomicInteger();
        jdbcTemplate.query(getQuery(LIST_MESSAGES_AND_GROUP_INFO_WITH_KEY), new Object[] { key, region },
                new RowCallbackHandler() {
                    public void processRow(ResultSet rs) throws SQLException {
                        size.incrementAndGet();

                        messages.add((Message<?>) deserializer.convert(lobHandler.getBlobAsBytes(rs, "INT_MESSAGE_BYTES")));

                        date.set(rs.getTimestamp("CREATED_DATE"));

                        updateDate.set(rs.getTimestamp("UPDATED_DATE"));

                        completeFlag.set(rs.getInt("COMPLETE") > 0);

                        lastReleasedSequenceRef.set(rs.getInt("LAST_RELEASED_SEQUENCE"));
                    }
                });

        if (size.get() == 0){
            return new SimpleMessageGroup(groupId);
        }
        Assert.state(date.get() != null, "Could not locate created date for groupId=" + groupId);
        Assert.state(updateDate.get() != null, "Could not locate updated date for groupId=" + groupId);
        long timestamp = date.get().getTime();
        boolean complete = completeFlag.get().booleanValue();
        SimpleMessageGroup messageGroup = new SimpleMessageGroup(messages, groupId, timestamp, complete);
        if (updateDate.get() != null){
            messageGroup.setLastModified(updateDate.get().getTime());
        }
        int lastReleasedSequenceNumber = lastReleasedSequenceRef.get();
        if (lastReleasedSequenceNumber > 0){
            messageGroup.setLastReleasedMessageSequenceNumber(lastReleasedSequenceNumber);
        }

        return messageGroup;
    }

    @Override
    @Transactional
    public MessageGroup addMessageToGroup(Object groupId, Message<?> message) {
        return super.addMessageToGroup(groupId, message);
    }

    @Override
    @Transactional
    public void removeMessageGroup(Object groupId) {
        super.removeMessageGroup(groupId);
    }

    @Override
    public int removeMessages(String identifier) {
        return jdbcTemplate.update(getQuery(DELETE_MESSAGE), new Object[]{identifier, region}, new int[]{
                Types.VARCHAR, Types.VARCHAR});
    }
}
