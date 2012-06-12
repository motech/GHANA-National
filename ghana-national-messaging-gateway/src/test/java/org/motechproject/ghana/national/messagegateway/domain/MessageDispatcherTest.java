package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MessageDispatcherTest extends BaseUnitTest {

    MessageDispatcher dispatcher;

    @Before
    public void setUp() {
        dispatcher = new MessageDispatcher();
    }

    @Test
    public void shouldCorrelateBasedOnPhoneNumberAndDeliveryDate() {
        final DateTime generationTime = DateUtil.newDateTime(DateUtil.newDate(2000, 1, 1), new Time(10, 10));
        TestPayload testPayload = new TestPayload(generationTime, generationTime.plusWeeks(1), "id");
        assertThat(dispatcher.correlateByRecipientAndDeliveryDate(testPayload), is(equalTo("id|2000-01-08")));
    }

    @Test
    public void shouldReturnIfTheMessageCanBeDispatched() {
        final DateTime generationTime = DateUtil.newDateTime(DateUtil.newDate(2000, 1, 1), new Time(10, 10));
        final TestPayload testPayload = new TestPayload(generationTime, generationTime.plusWeeks(1), "id");

        List<Payload> messagesList = new ArrayList<Payload>() {{
            add(testPayload);
        }};

        mockCurrentDate(DateUtil.newDateTime(DateUtil.newDate(2000, 1, 8), new Time(10, 11)));
        assertThat(dispatcher.canBeDispatched(messagesList), is(true));

        mockCurrentDate(DateUtil.newDateTime(DateUtil.newDate(1999, 12, 29), new Time(9, 59)));
        assertThat(dispatcher.canBeDispatched(messagesList), is(false));
    }
}
