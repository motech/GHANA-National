package org.motechproject.ghana.national.domain.mobilemidwife;

public enum Medium{
    SMS("SMS"), VOICE("Voice");
    private String displayName;

    Medium(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue(){
        return name();
    }

}
