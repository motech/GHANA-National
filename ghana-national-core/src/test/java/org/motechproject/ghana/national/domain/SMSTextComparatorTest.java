package org.motechproject.ghana.national.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class SMSTextComparatorTest {
    @Test
    public void shouldDefineAComparatorToOrderByWindowName(){
        assertThat(new SMSTextComparator<String>().compare("Upcoming sms", "Upcoming sms"), is(equalTo(0)));
        assertThat(new SMSTextComparator<String>().compare("Due sms", "Due sms"), is(equalTo(0)));
        assertThat(new SMSTextComparator<String>().compare("Overdue sms", "Overdue sms"), is(equalTo(0)));

        assertThat(new SMSTextComparator<String>().compare("Upcoming sms", "Due sms"), is(equalTo(-1)));
        assertThat(new SMSTextComparator<String>().compare("Due sms", "Upcoming sms"), is(equalTo(1)));

        assertThat(new SMSTextComparator<String>().compare("Overdue sms", "Due sms"), is(equalTo(1)));
        assertThat(new SMSTextComparator<String>().compare("Due sms", "Overdue sms"), is(equalTo(-1)));

        assertThat(new SMSTextComparator<String>().compare("Upcoming sms", "OverDue sms"), is(equalTo(-1)));
        assertThat(new SMSTextComparator<String>().compare("Overdue sms", "Upcoming sms"), is(equalTo(1)));
    }
}
