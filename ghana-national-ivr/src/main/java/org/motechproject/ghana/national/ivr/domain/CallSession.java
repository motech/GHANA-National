package org.motechproject.ghana.national.ivr.domain;

public class CallSession {
    private String languageOption;

    private String id;

    public CallSession(String id){
        this.id = id;
    }

    public CallSession languageOption(String option) {
        this.languageOption = option;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getLanguageOption() {
        return languageOption;
    }
}
