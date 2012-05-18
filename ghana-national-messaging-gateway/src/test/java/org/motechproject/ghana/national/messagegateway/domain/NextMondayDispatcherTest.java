package org.motechproject.ghana.national.messagegateway.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class NextMondayDispatcherTest {

    @Test
    public void shouldReturnDateOfUpComingMondayGiveADate(){
        DateTime generationTime = DateUtil.newDateTime(DateUtil.newDate(2012, 2, 27), new Time(10, 10));
        DateTime returnedNextMondayDate = new NextMondayDispatcher().deliveryDate(SMSPayload.fromText("", "", generationTime, null, MessageRecipientType.FACILITY));
        DateTime expectedNextMondayDate = DateUtil.newDateTime(DateUtil.newDate(2012, 3, 5), new Time(0, 0));
        assertThat(returnedNextMondayDate, is(equalTo(expectedNextMondayDate)));

        generationTime = DateUtil.newDateTime(DateUtil.newDate(2012, 2, 29), new Time(10, 10));
        returnedNextMondayDate = new NextMondayDispatcher().deliveryDate(SMSPayload.fromText("", "", generationTime, null, MessageRecipientType.FACILITY));
        expectedNextMondayDate = DateUtil.newDateTime(DateUtil.newDate(2012, 3, 5), new Time(0, 0));
        assertThat(returnedNextMondayDate, is(equalTo(expectedNextMondayDate)));

        generationTime = DateUtil.newDateTime(DateUtil.newDate(2012, 3, 4), new Time(10, 10));
        returnedNextMondayDate = new NextMondayDispatcher().deliveryDate(SMSPayload.fromText("", "", generationTime, null, MessageRecipientType.FACILITY));
        expectedNextMondayDate = DateUtil.newDateTime(DateUtil.newDate(2012, 3, 5), new Time(0, 0));
        assertThat(returnedNextMondayDate, is(equalTo(expectedNextMondayDate)));
    }
}
