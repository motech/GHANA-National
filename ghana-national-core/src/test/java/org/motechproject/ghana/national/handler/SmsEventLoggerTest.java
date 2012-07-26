package org.motechproject.ghana.national.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ghana.national.domain.SmsAuditLog;
import org.motechproject.ghana.national.repository.AllSmsAuditLogs;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.http.SmsDeliveryFailureException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SmsEventLoggerTest {

    @Mock
    private AllSmsAuditLogs allSmsAuditLogs;

    @InjectMocks
    private SmsEventLogger smsEventLogger=new SmsEventLogger();

    @Before
    public void setUp() {
        initMocks(this);

    }

    @Test
    public void shouldLogTheSMSSent() throws SmsDeliveryFailureException {
        Map<String, Object> parameters = new HashMap<>();
        List<String> recipients = Arrays.asList("0123456789","01122334455");
        String message = "some message";

        parameters.put(EventDataKeys.RECIPIENTS, recipients);
        parameters.put(EventDataKeys.MESSAGE,message);
        MotechEvent event = new MotechEvent("subject", parameters);
        smsEventLogger.handle(event);

        ArgumentCaptor<SmsAuditLog> smsAuditLogCaptor = ArgumentCaptor.forClass(SmsAuditLog.class);
        verify(allSmsAuditLogs).add(smsAuditLogCaptor.capture());
        SmsAuditLog smsAuditLogCaptorValue = smsAuditLogCaptor.getValue();
        assertThat(smsAuditLogCaptorValue.getRecipients(),is(recipients));
        assertThat(smsAuditLogCaptorValue.getMessageContent(),is(message));

    }
}
