package org.motechproject.ghana.national.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'FormUpload'")
public class FormUploadLog extends MotechBaseDataObject {
    @JsonProperty
    private DateTime dateTime;
    @JsonProperty
    private String formContent;

    public FormUploadLog() {
    }

    public FormUploadLog(DateTime dateTime, String messageContent) {
        this.dateTime = dateTime;
        this.formContent = messageContent;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getFormContent() {
        return formContent;
    }

    public void setFormContent(String formContent) {
        this.formContent = formContent;
    }
}
