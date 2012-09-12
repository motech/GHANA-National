package org.motechproject.ghana.national.handler;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.ghana.national.domain.SmsAuditLog;
import org.motechproject.ghana.national.repository.AllSmsAuditLogs;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.http.SmsDeliveryFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SmsEventLogger {

    @Autowired
    private AllSmsAuditLogs allSmsAuditLogs;

    @MotechListener(subjects = EventSubjects.SEND_SMS)
    public void handle(MotechEvent event) throws SmsDeliveryFailureException {
        List<String> recipients = (List<String>) event.getParameters().get(EventDataKeys.RECIPIENTS);
        String message = (String) event.getParameters().get(EventDataKeys.MESSAGE);
        SmsAuditLog smsAuditLog = new SmsAuditLog(DateTime.now(),recipients, message);
        allSmsAuditLogs.add(smsAuditLog);

    }

}
