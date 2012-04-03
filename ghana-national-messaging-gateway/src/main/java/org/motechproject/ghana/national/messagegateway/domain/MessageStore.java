package org.motechproject.ghana.national.messagegateway.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.jdbc.JdbcMessageStore;
import org.springframework.integration.store.MessageGroup;
import org.springframework.integration.store.MessageGroupCallback;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.util.UUIDConverter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

@Component("messageStore")
public class MessageStore extends JdbcMessageStore {
    private ExpiryStrategy expiryStrategy;
    private Collection<MessageGroupCallback> expiryCallbacksCopy = new LinkedHashSet<MessageGroupCallback>();
    private Logger logger = LoggerFactory.getLogger(MessageStore.class);
    private volatile JdbcOperations jdbcTemplate;
    private volatile DeserializingConverter deserializer = new DeserializingConverter();
    private volatile SerializingConverter serializer = new SerializingConverter();
    private volatile LobHandler lobHandler = new DefaultLobHandler();
    private String region = "DEFAULT";

    private static final String CREATE_MESSAGE = "INSERT into %PREFIX%MESSAGE(MESSAGE_ID, IDENTIFIER, REGION, CREATED_DATE, MESSAGE_BYTES)"
			+ " values (?, ?, ?, ?, ?   )";
    private static final String DELETE_MESSAGE = "DELETE from %PREFIX%MESSAGE where IDENTIFIER=? and REGION=?";

    @Autowired
    public MessageStore(ExpiryStrategy expiryStrategy, @Qualifier("aggregatorDataSource") DataSource dataSource) {
        super(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
        deserializer = new DeserializingConverter();
		serializer = new SerializingConverter();
        this.expiryStrategy = expiryStrategy;
        setJdbcTemplate(jdbcTemplate);
        setRegion(region);
    }

    @Override
    public int expireMessageGroups(long timeout) {
        return expiryStrategy.expireMessageGroups(this, timeout);
    }

    @Override
    public void registerMessageGroupExpiryCallback(MessageGroupCallback callback) {
        expiryCallbacksCopy.add(callback);
        super.registerMessageGroupExpiryCallback(callback);
    }

    public void expire(MessageGroup group) {

        RuntimeException exception = null;

        for (MessageGroupCallback callback : expiryCallbacksCopy) {
            try {
                callback.execute(this, group);
            } catch (RuntimeException e) {
                if (exception == null) {
                    exception = e;
                }
                logger.error("Exception in expiry callback", e);
            }
        }

        if (exception != null) {
            throw exception;
        }
    }

    private String getKey(Object input) {
        return input == null ? null : UUIDConverter.getUUID(input).toString();
    }


    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
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

    public int removeMessages(String identifier) {
        return jdbcTemplate.update(getQuery(DELETE_MESSAGE), new Object[] { identifier, region }, new int[] {
				Types.VARCHAR, Types.VARCHAR });
    }
}
