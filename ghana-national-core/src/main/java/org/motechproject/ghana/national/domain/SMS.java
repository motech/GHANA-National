package org.motechproject.ghana.national.domain;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import static org.motechproject.ghana.national.configuration.TextMessageTemplateVariables.*;

public class SMS {
    private String template;
    private String text;

    private SMS() {
    }

    public static SMS fromTemplate(String template) {
        return new SMS().template(template);
    }

    public static SMS fromSMSText(String text) {
        return new SMS().text(text);
    }

    public SMS fill(Map<String, String> runTimeValues) {
        for (Map.Entry<String, String> runTimeValue : runTimeValues.entrySet()) {
            text = template.replace(runTimeValue.getKey(), runTimeValue.getValue());
            template = text;
        }
        return this;
    }

    public SMS fillPatientDetails(String motechId, String firstName, String lastName) {
        text = template.replace(MOTECH_ID, motechId).replace(FIRST_NAME, firstName).replace(LAST_NAME, lastName);
        template = text;
        return this;
    }

    public SMS fillDateTime(Date date) {
        text = template.replace(DATE, DateFormat.getDateTimeInstance().format(date));
        template = text;
        return this;
    }

    public String getText() {
        return text;
    }

    public SMS template(String template) {
        this.template = template;
        return this;
    }

    public SMS text(String text) {
        this.text = text;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SMS)) return false;

        SMS sms = (SMS) o;

        if (text != null ? !text.equals(sms.text) : sms.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SMS{" +
                "text='" + text + '\'' +
                '}';
    }
}
