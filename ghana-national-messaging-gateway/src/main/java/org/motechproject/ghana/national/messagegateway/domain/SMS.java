package org.motechproject.ghana.national.messagegateway.domain;

public class SMS {
    private SMSPayload content;
    private String phoneNumber;

    public SMS(SMSPayload content, String phoneNumber) {
        this.content = content;
        this.phoneNumber = phoneNumber;
    }

    public SMSPayload getContent() {
        return content;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SMS sms = (SMS) o;

        if (content != null ? !content.equals(sms.content) : sms.content != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(sms.phoneNumber) : sms.phoneNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        return result;
    }
}
