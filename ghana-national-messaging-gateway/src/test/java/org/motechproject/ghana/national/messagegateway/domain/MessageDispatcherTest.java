package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Comparator;
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
    public void shouldAggregateManySMSBasedOnWindowNames() {
        List<SMS> messagesList = new ArrayList<SMS>() {{
            Comparator<String> alphabeticalOrder = new Comparator<String>() {

                @Override
                public int compare(String s, String s1) {
                    return s.compareTo(s1);
                }
            };
            add(SMS.fromText("B", "ph", null, null, alphabeticalOrder));
            add(SMS.fromText("C", "ph", null, null, alphabeticalOrder));
            add(SMS.fromText("A", "ph", null, null, alphabeticalOrder));
        }};
        assertThat(dispatcher.aggregateSMS(messagesList).getText(), is(equalTo("A%0aB%0aC")));
    }

    @Test
    public void shouldCorrelateBasedOnPhoneNumberAndDeliveryDate() {
        final DateTime generationTime = DateUtil.newDateTime(DateUtil.newDate(2000, 1, 1), new Time(10, 10));
        SMS sms = SMS.fromText("text", "ph", generationTime, new DeliveryStrategy() {
            @Override
            public DateTime deliveryDate(SMS sms) {
                return generationTime;
            }
        }, null);
        assertThat(dispatcher.correlateByRecipientAndDeliveryDate(sms), is(equalTo("ph|2000-01-01")));
    }

    @Test
    public void shouldReturnIfTheMessageCanBeDispatched() {
        final DateTime generationTime = DateUtil.newDateTime(DateUtil.newDate(2000, 1, 1), new Time(10, 10));
        final SMS sms = SMS.fromText("text", "ph", generationTime, new NextMondayDispatcher(), null);
        List<SMS> messagesList = new ArrayList<SMS>() {{
            add(sms);
        }};

        mockCurrentDate(DateUtil.newDateTime(DateUtil.newDate(2000, 1, 8), new Time(10, 11)));
        assertThat(dispatcher.canBeDispatched(messagesList), is(true));

        mockCurrentDate(DateUtil.newDateTime(DateUtil.newDate(1999, 12, 29), new Time(9, 59)));
        assertThat(dispatcher.canBeDispatched(messagesList), is(false));
    }
}
