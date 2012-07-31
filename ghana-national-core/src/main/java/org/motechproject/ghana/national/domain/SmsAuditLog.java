package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

import java.util.List;

@TypeDiscriminator("doc.type === 'SmsAuditLog'")
public class SmsAuditLog extends MotechBaseDataObject {
    @JsonProperty
    private DateTime dateTime;
    @JsonProperty
    private List<String> recipients;
    @JsonProperty
    private String messageContent;

    public SmsAuditLog() {
    }

    public SmsAuditLog(DateTime dateTime, List<String> recipients, String messageContent) {
        this.dateTime = dateTime;
        this.recipients = recipients;
        this.messageContent = messageContent;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
