package org.motechproject.ghana.national.domain.json;

import java.util.List;

public class MobileMidwifeCampaignRecord {

    private String name;
    private String type;
    private String maxDuration;
    private List<MessageRecord> messages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(String maxDuration) {
        this.maxDuration = maxDuration;
    }

    public List<MessageRecord> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageRecord> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "MobileMidwifeCampaignRecord{" +
                "campaignName='" + name + '\'' +
                ", type='" + type + '\'' +
                ", maxDuration='" + maxDuration + '\'' +
                ", messages=" + messages +
                '}';
    }
}
