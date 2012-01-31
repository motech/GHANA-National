package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'SMSAudit'")
public class SMSAudit extends MotechBaseDataObject {

    private String recipient;
    private String message;
    private DateTime timeOfMessage;
    @JsonProperty("type")
    private String type = "SMSAudit";

    public SMSAudit(String recipient, String message, DateTime timeOfMessage) {
        this.recipient = recipient;
        this.message = message;
        this.timeOfMessage = timeOfMessage;
    }

    public SMSAudit() {
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DateTime getTimeOfMessage() {
        return timeOfMessage;
    }

    public void setTimeOfMessage(DateTime timeOfMessage) {
        this.timeOfMessage = timeOfMessage;
    }
}
