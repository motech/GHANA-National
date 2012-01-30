package org.motechproject.ghana.national.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.motechproject.ghana.national.BaseIntegrationTest;
import org.motechproject.ghana.national.domain.SMSAudit;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static ch.lambdaj.function.matcher.AndMatcher.and;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;


public class AllSMSTest extends BaseIntegrationTest {
    @Autowired
    AllSMS allSMS;

    @Test
    public void shouldPersistSMS() {
        String recipient= "0987654321";
        String message = "Test Message";
        DateTime timeOfMessage = DateUtil.now();
        SMSAudit sms = new SMSAudit(recipient,message,timeOfMessage);
        allSMS.add(sms);

        List<SMSAudit> allMessages = allSMS.getAll();
        List<SMSAudit> expectedMessage = select(allMessages, and(having(on(SMSAudit.class).getRecipient(), is(recipient)), having(on(SMSAudit.class).getMessage(), is(message))));
        assertNotNull(expectedMessage.get(0));
    }

    @After
    public void tearDown() {
        List<SMSAudit> allMessages = allSMS.getAll();
        for (SMSAudit allMessage : allMessages) {
            allSMS.remove(allMessage);
        }
    }
}
